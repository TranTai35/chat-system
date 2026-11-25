package com.chatsystem.common.ui;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * Chạy việc nặng (truy vấn CSDL) ở luồng nền rồi trả kết quả về luồng giao diện,
 * để cửa sổ không bị đứng.
 *
 * <pre>{@code
 * Async.run(() -> userService.listUsers(), users -> {
 *     users.forEach(u -> addRow(u.getUsername(), u.getFullName()));   // chạy ở luồng giao diện
 * });
 * }</pre>
 * Việc ở lambda đầu chạy nền: <b>không</b> đụng tới component Swing ở đó.
 * Lambda thứ hai chạy ở luồng giao diện: cập nhật màn hình ở đây.
 * Lỗi (ServiceException, mất kết nối CSDL...) mặc định hiện hộp thoại; muốn tự xử lý thì dùng bản có {@code onError}.
 */
public final class Async {

    private Async() {
    }

    public static <T> void run(Callable<T> work, Consumer<? super T> onSuccess) {
        run(work, onSuccess, Async::showError);
    }

    public static <T> void run(Callable<T> work, Consumer<? super T> onSuccess, Consumer<Throwable> onError) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return work.call();
            }

            @Override
            protected void done() {
                try {
                    onSuccess.accept(get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    onError.accept(e.getCause() == null ? e : e.getCause());
                }
            }
        }.execute();
    }

    /** Việc không có kết quả trả về (ví dụ lưu, xoá), xong thì chạy {@code onDone}. */
    public static void runVoid(Runnable work, Runnable onDone) {
        run(() -> {
            work.run();
            return Boolean.TRUE;
        }, ok -> onDone.run());
    }

    /** Hộp thoại lỗi mặc định. Thông báo của DbException đã là tiếng Việt, hiện được thẳng. */
    public static void showError(Throwable t) {
        String message = t.getMessage() == null ? t.toString() : t.getMessage();
        JOptionPane.showMessageDialog(null, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
