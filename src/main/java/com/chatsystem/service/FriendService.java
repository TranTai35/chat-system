package com.chatsystem.service;

import com.chatsystem.common.Session;
import com.chatsystem.common.db.Db;
import com.chatsystem.common.db.DbException;
import com.chatsystem.dao.FriendDao;
import com.chatsystem.model.FriendRequest;
import com.chatsystem.model.User;

import java.util.List;

/**
 * Bạn bè, lời mời kết bạn, chặn (phân hệ người dùng). Viết theo khuôn {@link AuthService}:
 * màn hình chỉ gọi service này, service kiểm tra quy tắc rồi gọi {@link FriendDao}.
 * Mọi hàm làm việc với người đang đăng nhập ({@code Session.currentUserId()}).
 *
 * <p>Quy tắc kiểm tra ở đây (không ở CSDL): không tự kết bạn / chặn chính mình;
 * không gửi lời mời khi đã là bạn, đã có lời mời, hoặc một trong hai người đã chặn người kia;
 * chặn thì huỷ kết bạn và xoá lời mời giữa 2 người trong cùng 1 transaction.
 *
 * <p>Trạng thái online của bạn bè: dùng hàm chung {@code UserDao.isOnline(...)} của Tài khi có
 * (đợt 1), không tự viết lại điều kiện {@code is_online} / {@code last_seen}.
 */
public class FriendService {

    private final FriendDao friendDao;

    public FriendService() {
        this(new FriendDao());
    }

    public FriendService(FriendDao friendDao) {
        this.friendDao = friendDao;
    }

    /** Bạn bè của người đang đăng nhập, lọc theo tên đăng nhập / họ tên ({@code keyword} rỗng thì lấy hết). */
    public List<User> listFriends(String keyword) {
        return friendDao.findFriends(Session.currentUserId(), keyword);
    }

    /** Lời mời đang chờ gửi tới người đang đăng nhập, mới nhất trước. */
    public List<FriendRequest> listIncomingRequests() {
        return friendDao.findIncomingRequests(Session.currentUserId());
    }

    /**
     * Gửi lời mời kết bạn (màn Tìm người của Tài gọi hàm này).
     *
     * @throws ServiceException gửi cho chính mình, đã là bạn, đã có lời mời, hoặc bị chặn
     */
    public void sendRequest(int receiverId) {
        int me = Session.currentUserId();
        checkNotSelf(me, receiverId, "Không thể kết bạn với chính mình.");
        if (friendDao.areFriends(me, receiverId)) {
            throw new ServiceException("Hai bạn đã là bạn bè.");
        }
        if (friendDao.isBlockedEitherWay(me, receiverId)) {
            // Không nói rõ ai chặn ai, để người bị chặn không biết mình bị chặn.
            throw new ServiceException("Không thể gửi lời mời kết bạn cho người này.");
        }
        if (friendDao.hasPendingRequest(receiverId, me)) {
            throw new ServiceException("Người này đã gửi lời mời cho bạn. Hãy vào Lời mời kết bạn để đồng ý.");
        }
        if (friendDao.hasPendingRequest(me, receiverId)) {
            throw new ServiceException("Bạn đã gửi lời mời cho người này rồi.");
        }
        try {
            friendDao.insertRequest(me, receiverId);
        } catch (DbException e) {
            if (e.isDuplicateKey()) {   // bấm 2 lần thật nhanh
                throw new ServiceException("Bạn đã gửi lời mời cho người này rồi.", e);
            }
            throw e;
        }
    }

    /**
     * Đồng ý lời mời của {@code senderId}: xoá lời mời và thêm 2 dòng bạn bè, trong 1 transaction.
     *
     * @throws ServiceException lời mời không còn (người gửi đã huỷ, hoặc đã xử lý ở cửa sổ khác)
     */
    public void acceptRequest(int senderId) {
        int me = Session.currentUserId();
        Db.transaction(c -> {
            if (friendDao.deleteRequest(c, senderId, me) == 0) {
                throw new ServiceException("Lời mời này không còn nữa.");
            }
            friendDao.insertFriendship(c, me, senderId);
            return null;
        });
    }

    /** Từ chối lời mời của {@code senderId}. Lời mời đã không còn thì bỏ qua. */
    public void rejectRequest(int senderId) {
        int me = Session.currentUserId();
        Db.transaction(c -> friendDao.deleteRequest(c, senderId, me));
    }

    /**
     * Huỷ kết bạn (xoá cả 2 chiều).
     *
     * @throws ServiceException hai người không phải bạn bè
     */
    public void unfriend(int friendId) {
        int me = Session.currentUserId();
        int removed = Db.transaction(c -> friendDao.deleteFriendship(c, me, friendId));
        if (removed == 0) {
            throw new ServiceException("Hai bạn không còn là bạn bè.");
        }
    }

    /**
     * Chặn một người: thêm vào {@code blocks}, huỷ kết bạn và xoá lời mời giữa 2 người, cùng 1 transaction.
     * Sau đó người bị chặn không gửi lại lời mời được (xem {@link #sendRequest}).
     *
     * @throws ServiceException chặn chính mình
     */
    public void block(int userId) {
        int me = Session.currentUserId();
        checkNotSelf(me, userId, "Không thể chặn chính mình.");
        Db.transaction(c -> {
            friendDao.insertBlock(c, me, userId);
            friendDao.deleteFriendship(c, me, userId);
            friendDao.deleteRequestsBetween(c, me, userId);
            return null;
        });
    }

    private static void checkNotSelf(int me, int other, String message) {
        if (me == other) {
            throw new ServiceException(message);
        }
    }
}
