package com.trainstation.service;

import com.trainstation.dao.VeDAO;
import com.trainstation.dao.HoaDonDAO;
import com.trainstation.dao.ChiTietHoaDonDAO;
import com.trainstation.dao.ThongKeDAO;
import com.trainstation.model.Ve;
import com.trainstation.model.HoaDon;
import com.trainstation.model.ChiTietHoaDon;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class ThongKeService {
    private static ThongKeService instance;
    private final VeDAO veDAO;
    private final HoaDonDAO hoaDonDAO;
    private final ChiTietHoaDonDAO chiTietHoaDonDAO;
    private final ThongKeDAO thongKeDAO;

    private ThongKeService() {
        this.veDAO = VeDAO.getInstance();
        this.hoaDonDAO = HoaDonDAO.getInstance();
        this.chiTietHoaDonDAO = ChiTietHoaDonDAO.getInstance();
        this.thongKeDAO = ThongKeDAO.getInstance();
    }

    public static synchronized ThongKeService getInstance() {
        if (instance == null) {
            instance = new ThongKeService();
        }
        return instance;
    }

    public double tinhTongDoanhThu() {
        List<ChiTietHoaDon> chiTietList = chiTietHoaDonDAO.getAll();
        return chiTietList.stream()
                .mapToDouble(ChiTietHoaDon::getGiaDaKM)
                .sum();
    }

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
                .mapToDouble(ChiTietHoaDon::getGiaDaKM)
                .sum();
    }

    public int demTongVeDaBan() {
        return (int) veDAO.getAll().stream()
                .filter(v -> "Đã thanh toán".equals(v.getTrangThai()))
                .count();
    }

    public int demVeDaHoan() {
        return (int) veDAO.getAll().stream()
                .filter(v -> "Đã hoàn".equals(v.getTrangThai()))
                .count();
    }

    public int demVeDaHuy() {
        return (int) veDAO.getAll().stream()
                .filter(v -> "Đã hủy".equals(v.getTrangThai()))
                .count();
    }

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
                        .mapToDouble(ChiTietHoaDon::getGiaDaKM)
                        .sum();

                thongKe.put(ngay, thongKe.getOrDefault(ngay, 0.0) + tongTien);
            }
        }

        return thongKe;
    }

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

    public Map<String, Double> thongKeDoanhThu(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.thongKeDoanhThu(tuNgay, denNgay);
    }

    public List<Map<String, Object>> thongKeVeDoiHoan(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.thongKeVeDoiHoan(tuNgay, denNgay);
    }

    public List<Map<String, Object>> thongKeDoPhuGhe(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.thongKeDoPhuGhe(tuNgay, denNgay);
    }
}
