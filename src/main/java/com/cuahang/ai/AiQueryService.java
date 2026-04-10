package com.cuahang.ai;

import com.cuahang.util.HibernateUtil;
import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.LlamaOutput;
import de.kherud.llama.ModelParameters;
import com.cuahang.service.ModelDownloadService;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import org.hibernate.Session;

public class AiQueryService {
    private static final String SYSTEM_PROMPT = """
        Bạn là trợ lý tạo SQL cho MySQL.
        Nhiệm vụ: từ câu hỏi tiếng Việt, sinh 1 câu truy vấn SQL an toàn để đọc dữ liệu.
        Quy tắc bắt buộc:
        - CHỈ sinh SELECT (hoặc WITH ... SELECT). Không INSERT/UPDATE/DELETE/DROP/ALTER/CREATE/TRUNCATE.
        - Không dùng nhiều câu lệnh; không thêm dấu ```.
        - Ưu tiên dùng tên bảng/cột đúng theo schema dưới đây.
        - Khi cần join: dùng khóa ngoại như schema.

        Schema (MySQL):
        LoaiSanPham(MaLoai PK, TenLoai)
        NhaCungCap(MaNCC PK, TenNCC, DiaChi, Email)
        KhachHang(MaKH PK, TenKH, SoDT, DiemTichLuy)
        NhanVien(MaNV PK, HoTen, NgaySinh, SoDT, ChucVu)
        TaiKhoan(Username PK, Password, Quyen, MaNV FK->NhanVien.MaNV)
        SanPham(MaSP PK, TenSP, GiaBan, DonViTinh, SoLuongTon, MaLoai FK->LoaiSanPham.MaLoai, MaNCC FK->NhaCungCap.MaNCC)
        HoaDon(MaHD PK, NgayLap, TongTien, TrangThai, MaKH FK->KhachHang.MaKH, MaNV FK->NhanVien.MaNV)
        ChiTietHoaDon(MaCTHD PK AI, MaHD FK->HoaDon.MaHD, MaSP FK->SanPham.MaSP, SoLuong, DonGia, UNIQUE(MaHD, MaSP))
        """;

    private static final int DEFAULT_LIMIT = 200;
    private static final AtomicReference<ModelHolder> MODEL_HOLDER = new AtomicReference<>();
    private static final Object MODEL_LOCK = new Object();

    /**
     * Sinh câu SQL (MySQL) từ câu hỏi tiếng Việt bằng model GGUF chạy CPU.
     *
     * @param userInput câu hỏi tự nhiên
     * @return câu SQL đã được kiểm tra an toàn (chỉ SELECT/WITH, 1 câu lệnh)
     */
    public String generateSqlFromText(String userInput) {
        Objects.requireNonNull(userInput, "userInput");

        Path modelPath = resolveModelPath();
        String prompt = buildPrompt(userInput);

        validateModelFile(modelPath);

        InferenceParameters inferParams = new InferenceParameters(prompt)
            .setUseChatTemplate(false)
            .setTemperature(0.1f)
            .setTopP(0.95f)
            .setNPredict(192)
            .setStopStrings("\n", "```", "<|im_end|>");

        StringBuilder sb = new StringBuilder();
        try {
            LlamaModel model = getOrCreateModel(modelPath);
            synchronized (MODEL_LOCK) {
                Iterable<LlamaOutput> outputs = model.generate(inferParams);
                for (LlamaOutput output : outputs) {
                    sb.append(output);
                }
            }
        } catch (RuntimeException e) {
            throw new IllegalStateException(buildModelLoadError(modelPath, e), e);
        }

        String raw = sb.toString().trim();
        return sanitizeAndValidateSql(raw);
    }

    /**
     * Thực thi SELECT đã được kiểm tra an toàn và trả về dữ liệu dạng bảng.
     *
     * @param sql câu lệnh SQL (chỉ SELECT/WITH)
     * @return dữ liệu cột + dòng
     */
    public AiQueryResult executeSelectQuery(String sql) {
        String safeSql = sanitizeSql(sql);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.doReturningWork(conn -> {
                try (PreparedStatement ps = conn.prepareStatement(safeSql)) {
                    try (ResultSet rs = ps.executeQuery()) {
                        ResultSetMetaData md = rs.getMetaData();
                        int colCount = md.getColumnCount();

                        List<String> columns = new ArrayList<>(colCount);
                        for (int i = 1; i <= colCount; i++) {
                            columns.add(md.getColumnLabel(i));
                        }

                        List<List<Object>> rows = new ArrayList<>();
                        while (rs.next()) {
                            List<Object> row = new ArrayList<>(colCount);
                            for (int i = 1; i <= colCount; i++) {
                                row.add(rs.getObject(i));
                            }
                            rows.add(row);
                        }
                        return new AiQueryResult(columns, rows);
                    }
                }
            });
        }
    }

    private static String buildPrompt(String userInput) {
        return SYSTEM_PROMPT + "\n\nCâu hỏi: " + userInput.trim() + "\nSQL:";
    }

    public String sanitizeSql(String sql) {
        return sanitizeAndValidateSql(sql);
    }

    /**
     * Làm sạch và kiểm tra an toàn câu SQL trước khi chạy.
     *
     * @param sql câu SQL đầu vào (có thể chứa markdown/code fence)
     * @return câu SQL sạch, 1 câu lệnh, chỉ SELECT/WITH và có LIMIT
     */
    private static String sanitizeAndValidateSql(String sql) {
        String cleaned = sql
            .replace("```sql", "")
            .replace("```", "")
            .trim();

        int semi = cleaned.indexOf(';');
        if (semi >= 0) {
            cleaned = cleaned.substring(0, semi).trim();
        }

        if (cleaned.length() > 2000) {
            throw new IllegalArgumentException("SQL quá dài, vui lòng hỏi cụ thể hơn.");
        }

        String upper = cleaned.toUpperCase(Locale.ROOT);
        if (!(upper.startsWith("SELECT") || upper.startsWith("WITH"))) {
            throw new IllegalArgumentException("Chỉ cho phép câu lệnh SELECT/WITH.");
        }

        String[] forbidden = {
            "INSERT ",
            "UPDATE ",
            "DELETE ",
            "DROP ",
            "ALTER ",
            "CREATE ",
            "TRUNCATE ",
            "REPLACE ",
            "GRANT ",
            "REVOKE ",
            "LOAD DATA",
            "LOAD_FILE",
            "INTO OUTFILE",
            "INTO DUMPFILE",
            "--",
            "/*",
            "*/",
            "#"
        };
        for (String kw : forbidden) {
            if (upper.contains(kw)) {
                throw new IllegalArgumentException("SQL chứa từ khóa bị cấm: " + kw.trim());
            }
        }

        if (!upper.contains("LIMIT")) {
            cleaned = cleaned + " LIMIT " + DEFAULT_LIMIT;
        }

        return cleaned;
    }

    private static Path resolveModelPath() {
        Path userDir = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        String override = System.getProperty("cuahang.modelPath");
        if (override != null && !override.isBlank()) {
            Path p = Paths.get(override);
            if (!Files.isRegularFile(p)) {
                throw new IllegalStateException("Không tìm thấy model GGUF tại: " + p);
            }
            return p;
        }

        Path appDir = resolveAppDir();
        List<Path> candidates = List.of(
            appDir != null ? appDir.resolve("models") : userDir.resolve("models"),
            userDir.resolve("models"),
            Paths.get("models").normalize(),
            userDir.getParent() != null ? userDir.getParent().resolve("models") : Paths.get("..", "models").normalize(),
            Paths.get("..", "models").normalize(),
            Paths.get("..", "_workspace", "models").normalize(),
            Paths.get("..", "..", "models").normalize()
        );

        for (Path dir : candidates) {
            if (!Files.isDirectory(dir)) continue;
            Path expected = dir.resolve(ModelDownloadService.MODEL_FILE_NAME);
            if (Files.isRegularFile(expected)) return expected;

            try (var stream = Files.list(dir)) {
                var found = stream
                    .filter(p -> Files.isRegularFile(p))
                    .filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".gguf"))
                    .findFirst();
                if (found.isPresent()) return found.get();
            } catch (IOException e) {
                throw new IllegalStateException("Không thể đọc thư mục model: " + dir.toAbsolutePath(), e);
            }
        }

        throw new IllegalStateException(
            "Không tìm thấy model .gguf. Hãy đặt model vào ../models hoặc đặt -Dcuahang.modelPath=..."
        );
    }

    private static Path resolveAppDir() {
        try {
            var url = AiQueryService.class.getProtectionDomain().getCodeSource().getLocation();
            if (url == null) return null;
            Path p = Path.of(url.toURI()).toAbsolutePath().normalize();
            if (Files.isRegularFile(p)) return p.getParent();
            return p;
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private static LlamaModel getOrCreateModel(Path modelPath) {
        ModelHolder holder = MODEL_HOLDER.get();
        if (holder != null && holder.modelPath.equals(modelPath) && holder.model != null) {
            return holder.model;
        }
        synchronized (MODEL_LOCK) {
            holder = MODEL_HOLDER.get();
            if (holder != null && holder.modelPath.equals(modelPath) && holder.model != null) {
                return holder.model;
            }

            if (holder != null && holder.model != null) {
                try {
                    holder.model.close();
                } catch (Exception ignored) {
                }
            }

            int cores = Runtime.getRuntime().availableProcessors();
            int threads = Math.max(1, cores - 1);

            ModelParameters modelParams = new ModelParameters()
                .setModel(modelPath.toString())
                .setGpuLayers(0)
                .setThreads(threads);

            LlamaModel model = new LlamaModel(modelParams);
            MODEL_HOLDER.set(new ModelHolder(modelPath, model));
            return model;
        }
    }

    private static void validateModelFile(Path modelPath) {
        try {
            if (!Files.isRegularFile(modelPath)) {
                throw new IllegalStateException("Model không tồn tại: " + modelPath);
            }
            long size = Files.size(modelPath);
            if (size < 50L * 1024 * 1024) {
                throw new IllegalStateException("Model quá nhỏ (có thể tải lỗi): " + modelPath + " (" + size + " bytes)");
            }
            try (InputStream in = new BufferedInputStream(Files.newInputStream(modelPath))) {
                byte[] head = new byte[4];
                int n = in.read(head);
                if (n <= 0) {
                    throw new IllegalStateException("Không đọc được model: " + modelPath);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Không thể truy cập model: " + modelPath + " (" + e.getMessage() + ")", e);
        }
    }

    private static String buildModelLoadError(Path modelPath, RuntimeException e) {
        long size = -1;
        try {
            size = Files.isRegularFile(modelPath) ? Files.size(modelPath) : -1;
        } catch (IOException ignored) {
        }

        return String.join(
            "\n",
            "Không load được model GGUF.",
            "- Model: " + modelPath.toAbsolutePath(),
            "- Size: " + (size >= 0 ? size + " bytes" : "unknown"),
            "- WorkingDir: " + Path.of(System.getProperty("user.dir")).toAbsolutePath(),
            "Gợi ý:",
            "- Đảm bảo file không bị lỗi (tải đủ ~1.3GB).",
            "- Nếu chạy bằng .exe, hãy đặt model vào thư mục 'models' cạnh file .exe.",
            "- Có thể thử đặt đường dẫn model ngắn, không dấu cách và set -Dcuahang.modelPath=...",
            "Chi tiết: " + e.getMessage()
        );
    }

    private record ModelHolder(Path modelPath, LlamaModel model) {
    }
}
