package com.trainstation.service;

import com.trainstation.dao.*;
import com.trainstation.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service xử lý nghiệp vụ đổi vé với đầy đủ validations và tính phí
 */
public class DoiVeService {
    private static DoiVeService instance;
    
    private final VeDAO veDAO;
    private final GheDAO gheDAO;
    private final VeHistoryDAO veHistoryDAO;
    private final GiaoDichDoiVeDAO giaoDichDoiVeDAO;
    private final AuditLogDAO auditLogDAO;
    private final CauHinhDoiVeDAO cauHinhDAO;
    private final TinhGiaService tinhGiaService;

    private DoiVeService() {
        this.veDAO = VeDAO.getInstance();
        this.gheDAO = GheDAO.getInstance();
        this.veHistoryDAO = VeHistoryDAO.getInstance();
        this.giaoDichDoiVeDAO = GiaoDichDoiVeDAO.getInstance();
        this.auditLogDAO = AuditLogDAO.getInstance();
        this.cauHinhDAO = CauHinhDoiVeDAO.getInstance();
        this.tinhGiaService = TinhGiaService.getInstance();
    }

    public static synchronized DoiVeService getInstance() {
        if (instance == null) {
            instance = new DoiVeService();
        }
        return instance;
    }

    /**
     * Kiểm tra xem vé có được phép đổi không
     */
    public KetQuaKiemTra kiemTraChoPhepDoiVe(Ve ve) {
        if (ve == null) {
            return new KetQuaKiemTra(false, "Vé không tồn tại");
        }

        // Kiểm tra trạng thái vé
        String trangThai = ve.getTrangThai();
        if (trangThai == null) {
            return new KetQuaKiemTra(false, "Trạng thái vé không hợp lệ");
        }

        // Các trạng thái cho phép đổi
        if (!"Đã đặt".equals(trangThai) && !"Đã thanh toán".equals(trangThai)) {
            return new KetQuaKiemTra(false, "Chỉ được đổi vé ở trạng thái 'Đã đặt' hoặc 'Đã thanh toán'. Trạng thái hiện tại: " + trangThai);
        }

        // Kiểm tra thời hạn đổi vé
        if (ve.getGioDi() == null) {
            return new KetQuaKiemTra(false, "Không xác định được giờ khởi hành");
        }

        LocalDateTime gioDi = ve.getGioDi();
        LocalDateTime now = LocalDateTime.now();
        
        long hoursUntilDeparture = Duration.between(now, gioDi).toHours();
        
        // Lấy thời hạn tối thiểu từ cấu hình (mặc định 24 giờ)
        float thoiHanMin = cauHinhDAO.getGiaTriSo("THOI_HAN_DOI_MIN", 24);
        
        if (hoursUntilDeparture < thoiHanMin) {
            return new KetQuaKiemTra(false, String.format("Không thể đổi vé. Phải đổi trước ít nhất %.0f giờ trước giờ khởi hành. Còn lại: %d giờ", thoiHanMin, hoursUntilDeparture));
        }

        return new KetQuaKiemTra(true, "Vé được phép đổi");
    }

    /**
     * Tính phí đổi vé dựa trên thời gian
     */
    public ThongTinPhiDoiVe tinhPhiDoiVe(Ve veCu, Ve veMoi) {
        ThongTinPhiDoiVe thongTin = new ThongTinPhiDoiVe();
        
        // Tính giá vé cũ và mới
        try {
            float giaVeCu = veCu.getDisplayPrice();
            float giaVeMoi = veMoi.getDisplayPrice();
            
            if (giaVeCu <= 0 || giaVeMoi <= 0) {
                // Giá vé không hợp lệ - không thể tính phí
                thongTin.setGiaVeCu(0);
                thongTin.setGiaVeMoi(0);
                thongTin.setChenhLechGia(0);
                thongTin.setPhiDoiVe(0);
                thongTin.setMucPhi("Lỗi: Không tính được giá vé");
                return thongTin;
            }
            
            thongTin.setGiaVeCu(giaVeCu);
            thongTin.setGiaVeMoi(giaVeMoi);
            thongTin.setChenhLechGia(giaVeMoi - giaVeCu);
        } catch (Exception e) {
            // Log lỗi và trả về thông tin không hợp lệ
            System.err.println("Lỗi khi tính giá vé: " + e.getMessage());
            e.printStackTrace();
            thongTin.setGiaVeCu(0);
            thongTin.setGiaVeMoi(0);
            thongTin.setChenhLechGia(0);
            thongTin.setPhiDoiVe(0);
            thongTin.setMucPhi("Lỗi: " + e.getMessage());
            return thongTin;
        }

        // Tính phí đổi dựa trên thời gian
        if (veCu.getGioDi() != null) {
            LocalDateTime gioDi = veCu.getGioDi();
            LocalDateTime now = LocalDateTime.now();
            long hoursUntilDeparture = Duration.between(now, gioDi).toHours();
            
            float phiDoiVe;
            float phiMin;
            
            if (hoursUntilDeparture >= 72) {
                // Trước 72 giờ: phí 10%
                float tyLePhi = cauHinhDAO.getGiaTriSo("PHI_DOI_72H", 10) / 100;
                phiMin = cauHinhDAO.getGiaTriSo("PHI_DOI_MIN_72H", 20000);
                phiDoiVe = Math.max(thongTin.getGiaVeCu() * tyLePhi, phiMin);
                thongTin.setMucPhi("Trước 72 giờ");
            } else if (hoursUntilDeparture >= 24) {
                // 24-72 giờ: phí 20%
                float tyLePhi = cauHinhDAO.getGiaTriSo("PHI_DOI_24_72H", 20) / 100;
                phiMin = cauHinhDAO.getGiaTriSo("PHI_DOI_MIN_24_72H", 50000);
                phiDoiVe = Math.max(thongTin.getGiaVeCu() * tyLePhi, phiMin);
                thongTin.setMucPhi("24-72 giờ");
            } else {
                // Dưới 24 giờ: không cho phép
                phiDoiVe = 0;
                thongTin.setMucPhi("Không được phép đổi");
            }
            
            thongTin.setPhiDoiVe(phiDoiVe);
        } else {
            thongTin.setPhiDoiVe(0);
        }

        // Tính tổng tiền cần thu hoặc hoàn
        float tongTien = thongTin.getPhiDoiVe() + thongTin.getChenhLechGia();
        if (tongTien > 0) {
            thongTin.setSoTienThu(tongTien);
            thongTin.setSoTienHoan(0);
        } else {
            thongTin.setSoTienThu(0);
            thongTin.setSoTienHoan(Math.abs(tongTien));
        }

        return thongTin;
    }

    /**
     * Thực hiện đổi vé với đầy đủ validations và tracking
     */
    public KetQuaDoiVe doiVe(String maVeCu, Ve veMoi, String maNV, String maKH, String ghiChu) {
        KetQuaDoiVe ketQua = new KetQuaDoiVe();
        
        try {
            // 1. Lấy thông tin vé cũ
            Ve veCu = veDAO.findById(maVeCu);
            if (veCu == null) {
                ketQua.setThanhCong(false);
                ketQua.setThongBao("Không tìm thấy vé cũ");
                return ketQua;
            }

            // 2. Kiểm tra cho phép đổi
            KetQuaKiemTra kiemTra = kiemTraChoPhepDoiVe(veCu);
            if (!kiemTra.isHopLe()) {
                ketQua.setThanhCong(false);
                ketQua.setThongBao(kiemTra.getThongBao());
                return ketQua;
            }

            // 3. Kiểm tra ghế mới còn trống
            if (veMoi.getMaSoGhe() != null) {
                Ghe gheMoi = gheDAO.findById(veMoi.getMaSoGhe());
                if (gheMoi == null) {
                    ketQua.setThanhCong(false);
                    ketQua.setThongBao("Ghế mới không tồn tại");
                    return ketQua;
                }
                if (!"Trống".equals(gheMoi.getTrangThai())) {
                    ketQua.setThanhCong(false);
                    ketQua.setThongBao("Ghế mới đã được đặt");
                    return ketQua;
                }
            }

            // 4. Tính phí đổi vé
            ThongTinPhiDoiVe phiDoiVe = tinhPhiDoiVe(veCu, veMoi);
            ketQua.setThongTinPhi(phiDoiVe);

            // 5. Lưu lịch sử vé cũ
            String maLichSu = "LSV_" + UUID.randomUUID().toString().substring(0, 8);
            VeHistory history = VeHistory.fromVe(veCu, maLichSu, "DOI_VE", maNV, 
                "Đổi vé từ " + veCu.getMaChuyen() + " sang " + veMoi.getMaChuyen());
            veHistoryDAO.insert(history);

            // 6. Giải phóng ghế cũ
            if (veCu.getMaSoGhe() != null) {
                Ghe gheCu = gheDAO.findById(veCu.getMaSoGhe());
                if (gheCu != null) {
                    gheCu.setTrangThai("Trống");
                    gheDAO.update(gheCu);
                }
            }

            // 7. Đặt ghế mới
            if (veMoi.getMaSoGhe() != null) {
                Ghe gheMoi = gheDAO.findById(veMoi.getMaSoGhe());
                if (gheMoi != null) {
                    gheMoi.setTrangThai("Đã đặt");
                    gheDAO.update(gheMoi);
                }
            }

            // 8. Cập nhật thông tin vé
            veCu.setMaChuyen(veMoi.getMaChuyen());
            veCu.setMaSoGhe(veMoi.getMaSoGhe());
            veCu.setGaDi(veMoi.getGaDi());
            veCu.setGaDen(veMoi.getGaDen());
            veCu.setGioDi(veMoi.getGioDi());
            veCu.setSoToa(veMoi.getSoToa());
            veCu.setLoaiCho(veMoi.getLoaiCho());
            veCu.setMaBangGia(veMoi.getMaBangGia());
            
            if (!veDAO.update(veCu)) {
                ketQua.setThanhCong(false);
                ketQua.setThongBao("Không thể cập nhật vé");
                return ketQua;
            }

            // 9. Tạo giao dịch đổi vé
            String maGiaoDich = "GD_" + UUID.randomUUID().toString().substring(0, 8);
            GiaoDichDoiVe giaoDich = new GiaoDichDoiVe();
            giaoDich.setMaGiaoDich(maGiaoDich);
            giaoDich.setMaVeCu(maVeCu);
            giaoDich.setMaVeMoi(maVeCu); // Cùng mã vé, chỉ đổi thông tin
            giaoDich.setNgayDoi(LocalDateTime.now());
            giaoDich.setMaNV(maNV);
            giaoDich.setMaKH(maKH);
            giaoDich.setGiaVeCu(phiDoiVe.getGiaVeCu());
            giaoDich.setGiaVeMoi(phiDoiVe.getGiaVeMoi());
            giaoDich.setPhiDoiVe(phiDoiVe.getPhiDoiVe());
            giaoDich.setChenhLechGia(phiDoiVe.getChenhLechGia());
            giaoDich.setSoTienThu(phiDoiVe.getSoTienThu());
            giaoDich.setSoTienHoan(phiDoiVe.getSoTienHoan());
            giaoDich.setGhiChu(ghiChu);

            // Kiểm tra có cần phê duyệt không
            float nguongDuyet = cauHinhDAO.getGiaTriSo("NGUONG_DUYET_DOI_VE", 5000000);
            if (phiDoiVe.getGiaVeMoi() > nguongDuyet) {
                giaoDich.setTrangThai("CHO_DUYET");
                ketQua.setCanPheDuyet(true);
            } else {
                giaoDich.setTrangThai("HOAN_THANH");
                ketQua.setCanPheDuyet(false);
            }

            giaoDichDoiVeDAO.insert(giaoDich);
            ketQua.setMaGiaoDich(maGiaoDich);

            // 10. Ghi audit log
            String maLog = "AL_" + UUID.randomUUID().toString().substring(0, 8);
            AuditLog auditLog = new AuditLog();
            auditLog.setMaLog(maLog);
            auditLog.setLoaiThaoTac("DOI_VE");
            auditLog.setMaThamChieu(maVeCu);
            auditLog.setMaNV(maNV);
            auditLog.setThoiGian(LocalDateTime.now());
            auditLog.setNoiDung(String.format("Đổi vé từ chuyến %s sang %s. Phí: %.0f VNĐ", 
                history.getMaChuyen(), veMoi.getMaChuyen(), phiDoiVe.getPhiDoiVe()));
            auditLogDAO.insert(auditLog);

            ketQua.setThanhCong(true);
            ketQua.setThongBao("Đổi vé thành công");
            ketQua.setVeMoi(veCu);

        } catch (Exception e) {
            ketQua.setThanhCong(false);
            ketQua.setThongBao("Lỗi: " + e.getMessage());
            e.printStackTrace();
        }

        return ketQua;
    }

    /**
     * Phê duyệt giao dịch đổi vé
     */
    public boolean pheDuyetDoiVe(String maGiaoDich, String nguoiDuyet, boolean chapNhan, String lyDo) {
        GiaoDichDoiVe giaoDich = giaoDichDoiVeDAO.findById(maGiaoDich);
        if (giaoDich == null || !"CHO_DUYET".equals(giaoDich.getTrangThai())) {
            return false;
        }

        giaoDich.setNguoiDuyet(nguoiDuyet);
        giaoDich.setNgayDuyet(LocalDateTime.now());
        
        if (chapNhan) {
            giaoDich.setTrangThai("DA_DUYET");
        } else {
            giaoDich.setTrangThai("TU_CHOI");
            giaoDich.setLyDoTuChoi(lyDo);
        }

        return giaoDichDoiVeDAO.update(giaoDich);
    }

    // Inner classes for results
    public static class KetQuaKiemTra {
        private boolean hopLe;
        private String thongBao;

        public KetQuaKiemTra(boolean hopLe, String thongBao) {
            this.hopLe = hopLe;
            this.thongBao = thongBao;
        }

        public boolean isHopLe() { return hopLe; }
        public String getThongBao() { return thongBao; }
    }

    public static class ThongTinPhiDoiVe {
        private float giaVeCu;
        private float giaVeMoi;
        private float phiDoiVe;
        private float chenhLechGia;
        private float soTienThu;
        private float soTienHoan;
        private String mucPhi;

        // Getters and Setters
        public float getGiaVeCu() { return giaVeCu; }
        public void setGiaVeCu(float giaVeCu) { this.giaVeCu = giaVeCu; }
        
        public float getGiaVeMoi() { return giaVeMoi; }
        public void setGiaVeMoi(float giaVeMoi) { this.giaVeMoi = giaVeMoi; }
        
        public float getPhiDoiVe() { return phiDoiVe; }
        public void setPhiDoiVe(float phiDoiVe) { this.phiDoiVe = phiDoiVe; }
        
        public float getChenhLechGia() { return chenhLechGia; }
        public void setChenhLechGia(float chenhLechGia) { this.chenhLechGia = chenhLechGia; }
        
        public float getSoTienThu() { return soTienThu; }
        public void setSoTienThu(float soTienThu) { this.soTienThu = soTienThu; }
        
        public float getSoTienHoan() { return soTienHoan; }
        public void setSoTienHoan(float soTienHoan) { this.soTienHoan = soTienHoan; }
        
        public String getMucPhi() { return mucPhi; }
        public void setMucPhi(String mucPhi) { this.mucPhi = mucPhi; }
    }

    public static class KetQuaDoiVe {
        private boolean thanhCong;
        private String thongBao;
        private Ve veMoi;
        private ThongTinPhiDoiVe thongTinPhi;
        private String maGiaoDich;
        private boolean canPheDuyet;

        // Getters and Setters
        public boolean isThanhCong() { return thanhCong; }
        public void setThanhCong(boolean thanhCong) { this.thanhCong = thanhCong; }
        
        public String getThongBao() { return thongBao; }
        public void setThongBao(String thongBao) { this.thongBao = thongBao; }
        
        public Ve getVeMoi() { return veMoi; }
        public void setVeMoi(Ve veMoi) { this.veMoi = veMoi; }
        
        public ThongTinPhiDoiVe getThongTinPhi() { return thongTinPhi; }
        public void setThongTinPhi(ThongTinPhiDoiVe thongTinPhi) { this.thongTinPhi = thongTinPhi; }
        
        public String getMaGiaoDich() { return maGiaoDich; }
        public void setMaGiaoDich(String maGiaoDich) { this.maGiaoDich = maGiaoDich; }
        
        public boolean isCanPheDuyet() { return canPheDuyet; }
        public void setCanPheDuyet(boolean canPheDuyet) { this.canPheDuyet = canPheDuyet; }
    }
}
