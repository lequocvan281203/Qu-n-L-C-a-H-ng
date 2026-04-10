package com.cuahang.ai;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class GeminiSqlService {
    private static final String DEFAULT_MODEL = "gemini-2.0-flash";

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

    private final HttpClient http = HttpClient
        .newBuilder()
        .connectTimeout(Duration.ofSeconds(20))
        .build();

    public String generateSql(String apiKey, String question, String model) {
        String key = apiKey != null ? apiKey.trim() : "";
        if (key.isBlank()) {
            throw new IllegalArgumentException("Thiếu Gemini API key.");
        }

        String q = question != null ? question.trim() : "";
        if (q.isBlank()) {
            throw new IllegalArgumentException("Thiếu câu hỏi.");
        }

        String m = model != null && !model.isBlank() ? model.trim() : DEFAULT_MODEL;

        String prompt = SYSTEM_PROMPT + "\n\nCâu hỏi: " + q + "\nSQL:";
        String body = buildRequestBody(prompt);
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + urlEncode(m) + ":generateContent?key=" + urlEncode(key);

        HttpRequest req = HttpRequest
            .newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(60))
            .header("Content-Type", "application/json; charset=UTF-8")
            .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
            .build();

        HttpResponse<String> resp;
        try {
            resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Không gọi được Gemini: " + e.getMessage(), e);
        }

        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            String msg = resp.body() != null ? resp.body().trim() : "";
            throw new IllegalStateException("Gemini trả lỗi HTTP " + resp.statusCode() + (msg.isBlank() ? "" : (": " + msg)));
        }

        String text = extractFirstText(resp.body());
        if (text == null || text.isBlank()) {
            throw new IllegalStateException("Gemini không trả về nội dung SQL.");
        }

        return text.trim();
    }

    private static String buildRequestBody(String prompt) {
        String escaped = jsonEscape(prompt);
        return "{"
            + "\"contents\":[{\"role\":\"user\",\"parts\":[{\"text\":\"" + escaped + "\"}]}],"
            + "\"generationConfig\":{\"temperature\":0.1,\"topP\":0.95,\"maxOutputTokens\":256}"
            + "}";
    }

    private static String extractFirstText(String json) {
        if (json == null) return null;
        int idx = json.indexOf("\"text\"");
        if (idx < 0) return null;
        int colon = json.indexOf(':', idx);
        if (colon < 0) return null;
        int quote = json.indexOf('"', colon + 1);
        if (quote < 0) return null;

        StringBuilder sb = new StringBuilder();
        boolean escaping = false;
        for (int i = quote + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaping) {
                switch (c) {
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    case 'u' -> {
                        if (i + 4 < json.length()) {
                            String hex = json.substring(i + 1, i + 5);
                            try {
                                int code = Integer.parseInt(hex, 16);
                                sb.append((char) code);
                                i += 4;
                            } catch (NumberFormatException ex) {
                                sb.append('u');
                            }
                        } else {
                            sb.append('u');
                        }
                    }
                    default -> sb.append(c);
                }
                escaping = false;
                continue;
            }

            if (c == '\\') {
                escaping = true;
                continue;
            }
            if (c == '"') {
                return sb.toString();
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static String jsonEscape(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '"' -> sb.append("\\\"");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    private static String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
