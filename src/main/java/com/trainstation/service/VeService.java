package com.trainstation.service;

// ... (các import giữ nguyên)
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class VeService {
    private static VeService instance;
    private final VeDAO veDAO;
    private final GheDAO gheDAO;
    private final ChuyenTauDAO chuyenTauDAO;
    private final BangGiaDAO bangGiaDAO;
    private final ChiTietHoaDonDAO chiTietHoaDonDAO;
    private final HoaDonDAO hoaDonDAO;
    private final VeServiceImpl veServiceImpl;
    // ... các hằng số cũ giữ nguyên

    /**
     * Map dùng cho đồng bộ hóa đặt vé trên từng mã ghế (tối ưu)
     */
    private final ConcurrentMap<String, Object> seatLocks = new ConcurrentHashMap<>();

    // ... các method, constructor giữ nguyên

    /**
     * Đặt vé có đồng bộ trên từng ghế, tránh đặt trùng vé khi nhiều luồng đồng thời
     */
    public boolean datVeCoDongBo(Ve ve) {
        String maGhe = ve.getMaSoGhe();
        if (maGhe == null) throw new IllegalArgumentException("Vé không xác định ghế");
        Object lock = seatLocks.computeIfAbsent(maGhe, k -> new Object());
        synchronized (lock) {
            Ve veDB = veDAO.findById(ve.getMaVe());
            if (veDB != null && !"Trống".equalsIgnoreCase(veDB.getTrangThai())) {
                throw new IllegalStateException("Ghế đã bị đặt hoặc không còn trống, thao tác bị chặn");
            }
            // Thực hiện logic đặt vé thực tế như phương thức cũ
            ve.setTrangThai("Đã đặt");
            boolean result = veDAO.insertOrUpdate(ve); // Hàm insertOrUpdate là ví dụ, hoặc tách insert + update
            // Sau khi đặt vé xong, có thể xóa lock khỏi map nếu muốn giải phóng bộ nhớ (nâng cao)
            // seatLocks.remove(maGhe);
            return result;
        }
    }

    /**
     * Ví dụ: gọi phương thức này ở các điểm user/bên ngoài nghiệp vụ đặt vé
     * thay cho insert/update thông thường để đảm bảo đồng bộ hóa
     */

    // ... giữ nguyên các method còn lại
}
