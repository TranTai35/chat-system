package com.chatsystem.service;

import com.chatsystem.common.Session;
import com.chatsystem.dao.MessageDao;
import com.chatsystem.model.Conversation;
import com.chatsystem.model.Message;

import java.util.List;
import java.util.Optional;

/**
 * Chat riêng và chat nhóm (phân hệ người dùng). Viết theo khuôn {@link AuthService}:
 * khung chat chỉ gọi service này, service kiểm tra quy tắc rồi gọi {@link MessageDao}.
 *
 * <p>Khung chat lấy tin mới bằng cách hỏi mỗi giây (Swing Timer + {@code Async.run}):
 * {@code loadMessages(conversationId, idTinCuoiDangCo)}; lần đầu mở cuộc chat thì truyền 0.
 *
 * <p>Đợt sau: tạo chat riêng khi 2 người chưa chat lần nào, danh sách hội thoại, tin chưa đọc,
 * xoá tin phía mình, tìm tin, mã hoá nhóm.
 */
public class ChatService {

    /** Độ dài tối đa của 1 tin nhắn (ký tự). */
    public static final int MAX_MESSAGE_LENGTH = 2000;

    private final MessageDao messageDao;

    public ChatService() {
        this(new MessageDao());
    }

    public ChatService(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    /**
     * Id cuộc chat riêng giữa người đang đăng nhập và {@code otherUserId}, rỗng nếu 2 người chưa chat lần nào.
     * (Tạo cuộc chat mới làm ở đợt 3.)
     */
    public Optional<Integer> findPrivateConversation(int otherUserId) {
        int me = Session.currentUserId();
        if (me == otherUserId) {
            throw new ServiceException("Không thể nhắn tin cho chính mình.");
        }
        return messageDao.findPrivateConversationId(Conversation.privateKeyOf(me, otherUserId));
    }

    /**
     * Tin người đang đăng nhập được thấy trong 1 cuộc chat, có id lớn hơn {@code afterId}, theo thứ tự gửi.
     * Đã bỏ tin xoá phía mình và tin trước mốc "Xoá toàn bộ lịch sử".
     *
     * @throws ServiceException không phải thành viên cuộc chat
     */
    public List<Message> loadMessages(int conversationId, long afterId) {
        int me = Session.currentUserId();
        checkMember(conversationId, me);
        return messageDao.findVisibleAfter(conversationId, me, afterId);
    }

    /**
     * Gửi tin nhắn thường (không mã hoá).
     *
     * @return tin vừa lưu (đã có id)
     * @throws ServiceException tin rỗng, quá dài, hoặc không phải thành viên cuộc chat
     */
    public Message sendText(int conversationId, String content) {
        int me = Session.currentUserId();
        String text = content == null ? "" : content.strip();
        if (text.isEmpty()) {
            throw new ServiceException("Tin nhắn trống.");
        }
        if (text.length() > MAX_MESSAGE_LENGTH) {
            throw new ServiceException("Tin nhắn dài quá " + MAX_MESSAGE_LENGTH + " ký tự.");
        }
        checkMember(conversationId, me);
        // Đợt 3: chat riêng thì kiểm tra 2 người không chặn nhau trước khi gửi.

        Message m = new Message();
        m.setConversationId(conversationId);
        m.setSenderId(me);
        m.setContent(text);
        m.setId(messageDao.insert(m));
        return m;
    }

    private void checkMember(int conversationId, int userId) {
        if (!messageDao.isMember(conversationId, userId)) {
            throw new ServiceException("Bạn không còn là thành viên của cuộc trò chuyện này.");
        }
    }
}
