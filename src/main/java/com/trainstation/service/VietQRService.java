package com.trainstation.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class VietQRService {

    // Tạo 1 client dùng chung (không tạo mới mỗi lần gọi)
    private static final OkHttpClient CLIENT = new OkHttpClient();

    /**
     * Tải ảnh QR VietQR và lưu vào file.
     *
     * @param bankCode      VD: 970423
     * @param accountNumber VD: 48608112005
     * @param accountName   Tên chủ tài khoản (có thể có dấu)
     * @param amount        Số tiền (VND). Nên truyền số nguyên, ví dụ 150000
     * @param content       Nội dung chuyển khoản
     * @param savePath      Đường dẫn file PNG muốn lưu (vd: "C:/tmp/qr.png")
     * @return absolute path của file đã lưu
     */
    public static String fetchVietQR(
            String bankCode,
            String accountNumber,
            String accountName,
            float amount,
            String content,
            String savePath
    ) throws Exception {

        if (bankCode == null || bankCode.isBlank()) throw new IllegalArgumentException("bankCode rỗng");
        if (accountNumber == null || accountNumber.isBlank()) throw new IllegalArgumentException("accountNumber rỗng");
        if (savePath == null || savePath.isBlank()) throw new IllegalArgumentException("savePath rỗng");

        // Tránh lỗi số thực cho tiền
        long amt = Math.round(amount);

        // Encode chuẩn UTF-8 (hỗ trợ tiếng Việt, ký tự &, +, ?, ...)
        String addInfoEnc = URLEncoder.encode(content == null ? "" : content, StandardCharsets.UTF_8);
        String accNameEnc = URLEncoder.encode(accountName == null ? "" : accountName, StandardCharsets.UTF_8);

        // Nếu bạn chỉ muốn QR (không khung), dùng "-qr_only.png"
        String baseUrl = "https://img.vietqr.io/image";
        String url = String.format("%s/%s-%s-qr_only.png?amount=%d&addInfo=%s&accountName=%s",
                baseUrl, bankCode, accountNumber, amt, addInfoEnc, accNameEnc);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Không thể tạo mã QR: " + response.code() + " - " + response.message());
            }
            if (response.body() == null) {
                throw new IOException("Response body rỗng");
            }

            File file = new File(savePath);

            // Tự tạo folder nếu chưa tồn tại
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                boolean ok = parent.mkdirs();
                if (!ok) throw new IOException("Không tạo được thư mục: " + parent.getAbsolutePath());
            }

            try (InputStream in = response.body().byteStream();
                 OutputStream out = new FileOutputStream(file)) {
                // Java 9+ (bạn dùng Java 21 OK)
                in.transferTo(out);
            }

            return file.getAbsolutePath();
        } catch (Exception e) {
            System.err.println("Lỗi khi tải mã QR: " + e.getMessage());
            throw e;
        }
    }
}
