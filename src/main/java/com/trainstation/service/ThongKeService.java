package com.trainstation.service;

import com.trainstation.dao.VeDAO;
import com.trainstation.dao.HoaDonDAO;
import com.trainstation.dao.ChiTietHoaDonDAO;
import com.trainstation.model.Ve;
import com.trainstation.model.HoaDon;
import com.trainstation.model.ChiTietHoaDon;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ thống kê
 */
public class ThongKeService {
    private static ThongKeService instance;
    private final VeDAO veDAO;
    private final HoaDonDAO hoaDonDAO;
    private final ChiTietHoaDonDAO chiTietHoaDonDAO;

    private ThongKeService() {
        this.veDAO = VeDAO.getInstance();
        this.hoaDonDAO = HoaDonDAO.getInstance();
        this.chiTietHoaDonDAO = ChiTietHoaDonDAO.getInstance();
    }

    public static synchronized ThongKeService getInstance() {
        if (instance == null) {
            instance = new ThongKeService();
        }
        return instance;
    }

    /**
     * Tính tổng doanh thu
     */
    public double tinhTongDoanhThu() {
        List<ChiTietHoaDon> chiTietList = chiTietHoaDonDAO.getAll();
        return chiTietList.stream()
                .mapToDouble(ct -> ct.getGiaDaKM() != null ? ct.getGiaDaKM() : 0.0)
                .sum();
    }

    /**
     * Tính doanh thu theo tháng
     */
    public double tinhDoanhThuTheoThang(int thang, int nam) {
        List<HoaDon> hoaDonList = hoaDonDAO.getAll();
        List<String> maHoaDonTheoThang = hoaDonList.stream()
                .filter(hd -> {
                    if (hd.getNgayLap() == null) return false;
                    return hd.getNgayLap().getMonthValue() == thang && 
                           hd.getNgayLap().getYear() == nam;
                })
                .map(HoaDon::getMaHoaDon)
                .collect(Collectors.toList());

        List<ChiTietHoaDon> chiTietList = chiTietHoaDonDAO.getAll();
        return chiTietList.stream()
                .filter(ct -> maHoaDonTheoThang.contains(ct.getMaHoaDon()))
                .mapToDouble(ct -> ct.getGiaDaKM() != null ? ct.getGiaDaKM() : 0.0)
                .sum();
    }

    /**
     * Đếm tổng số vé đã bán
     */
    public int demTongVeDaBan() {
        return (int) veDAO.getAll().stream()
                .filter(v -> "Đã thanh toán".equals(v.getTrangThai()))
                .count();
    }

    /**
     * Đếm số vé đã hoàn
     */
    public int demVeDaHoan() {
        return (int) veDAO.getAll().stream()
                .filter(v -> "Đã hoàn".equals(v.getTrangThai()))
                .count();
    }

    /**
     * Đếm số vé đã hủy
     */
    public int demVeDaHuy() {
        return (int) veDAO.getAll().stream()
                .filter(v -> "Đã hủy".equals(v.getTrangThai()))
                .count();
    }

    /**
     * Đếm số vé theo chuyến tàu
     */
    public Map<String, Integer> demVeTheoChuyen() {
        Map<String, Integer> thongKe = new HashMap<>();
        List<Ve> veList = veDAO.getAll();
        
        for (Ve ve : veList) {
            if ("Đã thanh toán".equals(ve.getTrangThai())) {
                String maChuyen = ve.getMaChuyen();
                thongKe.put(maChuyen, thongKe.getOrDefault(maChuyen, 0) + 1);
            }
        }
        
        return thongKe;
    }

    /**
     * Thống kê số vé theo trạng thái
     */
    public Map<String, Integer> thongKeVeTheoTrangThai() {
        Map<String, Integer> thongKe = new HashMap<>();
        List<Ve> veList = veDAO.getAll();
        
        for (Ve ve : veList) {
            String trangThai = ve.getTrangThai();
            if (trangThai != null) {
                thongKe.put(trangThai, thongKe.getOrDefault(trangThai, 0) + 1);
            }
        }
        
        return thongKe;
    }

    /**
     * Thống kê doanh thu theo ngày
     */
    public Map<String, Double> thongKeDoanhThuTheoNgay(int thang, int nam) {
        Map<String, Double> thongKe = new HashMap<>();
        List<HoaDon> hoaDonList = hoaDonDAO.getAll();
        List<ChiTietHoaDon> chiTietList = chiTietHoaDonDAO.getAll();

        for (HoaDon hoaDon : hoaDonList) {
            if (hoaDon.getNgayLap() != null && 
                hoaDon.getNgayLap().getMonthValue() == thang && 
                hoaDon.getNgayLap().getYear() == nam) {
                
                String ngay = hoaDon.getNgayLap().toLocalDate().toString();
                double tongTien = chiTietList.stream()
                        .filter(ct -> ct.getMaHoaDon().equals(hoaDon.getMaHoaDon()))
                        .mapToDouble(ct -> ct.getGiaDaKM() != null ? ct.getGiaDaKM() : 0.0)
                        .sum();
                
                thongKe.put(ngay, thongKe.getOrDefault(ngay, 0.0) + tongTien);
            }
        }
        
        return thongKe;
    }

    /**
     * Lấy tất cả thống kê
     */
    public Map<String, Object> layTatCaThongKe() {
        Map<String, Object> thongKe = new HashMap<>();
        thongKe.put("tongDoanhThu", tinhTongDoanhThu());
        thongKe.put("tongVeDaBan", demTongVeDaBan());
        thongKe.put("veDaHoan", demVeDaHoan());
        thongKe.put("veDaHuy", demVeDaHuy());
        thongKe.put("veTheoTrangThai", thongKeVeTheoTrangThai());
        thongKe.put("veTheoChuyen", demVeTheoChuyen());
        return thongKe;
    }
}
