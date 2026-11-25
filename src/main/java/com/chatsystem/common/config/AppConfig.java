package com.chatsystem.common.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Đọc cấu hình ứng dụng (kết nối MySQL, SMTP, LLM) từ file {@code config.properties}.
 *
 * <p>Tìm file theo thứ tự (lấy chỗ đầu tiên có file):
 * <ol>
 *   <li>đường dẫn đặt bằng {@code -Dchat.config=<file>};</li>
 *   <li>thư mục đang chạy (khi chạy bằng Maven / IDE là thư mục gốc dự án);</li>
 *   <li>thư mục chứa file jar (khi chạy bản release, file nằm cạnh jar).</li>
 * </ol>
 * Nếu cùng thư mục có {@code config.local.properties} thì file đó ghi đè (dùng để
 * giữ mật khẩu riêng trên máy từng người, file này đã nằm trong .gitignore).
 *
 * <p>Thiếu file hoặc thiếu khoá thì dùng giá trị mặc định; chỉ {@link #require} mới báo lỗi.
 */
public final class AppConfig {

    public static final String FILE_NAME = "config.properties";
    public static final String LOCAL_FILE_NAME = "config.local.properties";

    private static final Properties PROPS = new Properties();
    private static final List<Path> LOADED = new ArrayList<>();
    private static final List<Path> SEARCHED = new ArrayList<>();

    static {
        load();
    }

    private AppConfig() {
    }

    private static void load() {
        String explicit = System.getProperty("chat.config");
        if (explicit != null && !explicit.isBlank()) {
            Path file = Paths.get(explicit).toAbsolutePath();
            SEARCHED.add(file);
            loadFile(file);
            return;
        }
        List<Path> dirs = new ArrayList<>();
        dirs.add(Paths.get("").toAbsolutePath());
        Path jarDir = jarDirectory();
        if (jarDir != null && !dirs.contains(jarDir)) {
            dirs.add(jarDir);
        }
        for (Path dir : dirs) {
            Path file = dir.resolve(FILE_NAME);
            SEARCHED.add(file);
            if (Files.isRegularFile(file)) {
                loadFile(file);
                loadFile(dir.resolve(LOCAL_FILE_NAME));
                return;
            }
        }
    }

    private static Path jarDirectory() {
        try {
            Path location = Paths.get(AppConfig.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            // Chạy từ jar: location là file .jar. Chạy từ target/classes: là thư mục, bỏ qua.
            if (Files.isRegularFile(location)) {
                return location.toAbsolutePath().getParent();
            }
        } catch (URISyntaxException | RuntimeException e) {
            // Không xác định được vị trí jar thì chỉ tìm ở thư mục đang chạy.
        }
        return null;
    }

    private static void loadFile(Path file) {
        if (!Files.isRegularFile(file)) {
            return;
        }
        try (Reader reader = new InputStreamReader(Files.newInputStream(file), StandardCharsets.UTF_8)) {
            PROPS.load(reader);
            LOADED.add(file);
        } catch (IOException e) {
            throw new IllegalStateException("Không đọc được file cấu hình " + file + ": " + e.getMessage(), e);
        }
    }

    /** Giá trị của khoá, hoặc {@code null} nếu không có (hoặc để trống). */
    public static String get(String key) {
        String v = PROPS.getProperty(key);
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    public static String get(String key, String defaultValue) {
        String v = get(key);
        return v == null ? defaultValue : v;
    }

    public static int getInt(String key, int defaultValue) {
        String v = get(key);
        if (v == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Giá trị của '" + key + "' trong file cấu hình phải là số, hiện là: " + v);
        }
    }

    /** Giống {@link #get(String)} nhưng báo lỗi rõ ràng nếu khoá bị thiếu. */
    public static String require(String key) {
        String v = get(key);
        if (v == null) {
            throw new IllegalStateException("Thiếu '" + key + "' trong file cấu hình. " + describeSource());
        }
        return v;
    }

    /** Có đọc được file cấu hình nào không. */
    public static boolean isLoaded() {
        return !LOADED.isEmpty();
    }

    /** Mô tả file đã đọc hoặc các chỗ đã tìm, để đưa vào thông báo lỗi. */
    public static String describeSource() {
        if (!LOADED.isEmpty()) {
            return "Đã đọc cấu hình từ: " + LOADED;
        }
        return "Không tìm thấy " + FILE_NAME + ". Đã tìm ở: " + SEARCHED;
    }
}
