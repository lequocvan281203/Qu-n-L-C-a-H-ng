package com.cuahang.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class ModelDownloadService {
    public static final String MODEL_FILE_NAME = "qwen2.5-1.5b-instruct-q5_k_m.gguf";
    public static final String MODEL_DOWNLOAD_URL =
        "https://huggingface.co/Qwen/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/qwen2.5-1.5b-instruct-q5_k_m.gguf";

    public boolean ensureModelPresent(Path modelsDir) {
        try {
            Files.createDirectories(modelsDir);
        } catch (Exception e) {
            throw new IllegalStateException("Không tạo được thư mục models: " + modelsDir, e);
        }

        Path modelPath = modelsDir.resolve(MODEL_FILE_NAME);
        if (Files.isRegularFile(modelPath)) return true;

        int choice = JOptionPane.showConfirmDialog(
            null,
            "Chưa có model AI (GGUF). Tải tự động ~1.3GB ngay bây giờ?",
            "Tải model AI",
            JOptionPane.YES_NO_OPTION
        );
        if (choice != JOptionPane.YES_OPTION) return false;

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);

        JOptionPane pane = new JOptionPane(bar, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[] {});
        var dialog = pane.createDialog("Đang tải model AI...");
        dialog.setModal(true);

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                downloadModel(modelPath, percent -> publish(percent));
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int p = chunks.get(chunks.size() - 1);
                bar.setValue(p);
                bar.setString(p + "%");
            }

            @Override
            protected void done() {
                dialog.dispose();
            }
        };

        worker.execute();
        dialog.setVisible(true);

        try {
            worker.get();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Tải model thất bại: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private interface Progress {
        void onPercent(int percent);
    }

    private static void downloadModel(Path target, Progress progress) throws Exception {
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(MODEL_DOWNLOAD_URL)).GET().build();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() >= 400) {
            throw new IllegalStateException("HTTP " + response.statusCode());
        }

        long length = response.headers().firstValueAsLong("content-length").orElse(-1);
        Path tmp = target.resolveSibling(target.getFileName().toString() + ".part");

        try (InputStream in = response.body();
             OutputStream out = Files.newOutputStream(tmp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            byte[] buf = new byte[1024 * 1024];
            long read = 0;
            int n;
            int lastPercent = 0;
            while ((n = in.read(buf)) >= 0) {
                out.write(buf, 0, n);
                read += n;
                if (length > 0) {
                    int p = (int) Math.min(100, (read * 100) / length);
                    if (p != lastPercent) {
                        lastPercent = p;
                        int emit = p;
                        SwingUtilities.invokeLater(() -> progress.onPercent(emit));
                    }
                }
            }
        }

        Files.move(tmp, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }
}

