package com.trainstation.service;

import com.trainstation.dao.KhachHangDAO;
import com.trainstation.model.KhachHang;
import com.trainstation.model.NhanVien;
import com.trainstation.network.AppClient;
import com.trainstation.network.AppRequest;
import com.trainstation.network.AppResponse;
import com.trainstation.network.NetworkConfig;
import com.trainstation.network.RequestType;

import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Khách hàng
 */
public class KhachHangService {
    private static KhachHangService instance;
    private final KhachHangDAO khachHangDAO;

    private KhachHangService() {
        this.khachHangDAO = KhachHangDAO.getInstance();
    }

    public static synchronized KhachHangService getInstance() {
        if (instance == null) {
            instance = new KhachHangService();
        }
        return instance;
    }

    public List<KhachHang> layTatCaKhachHang() {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.GET_ALL_KHACHHANG));
            if (resp.isSuccess() && resp.getData() != null) return (List<KhachHang>) resp.getData();
            return java.util.Collections.emptyList();
        }
        return khachHangDAO.getAll();
    }

    public KhachHang timKhachHangTheoMa(String maKH) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.FIND_KHACHHANG_BY_ID, maKH));
            if (resp.isSuccess()) return (KhachHang) resp.getData();
            return null;
        }
        return khachHangDAO.findById(maKH);
    }

    public boolean themKhachHang(KhachHang kh) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.INSERT_KHACHHANG, kh));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return khachHangDAO.insert(kh);
    }

    public boolean capNhatKhachHang(KhachHang kh) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.UPDATE_KHACHHANG, kh));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return khachHangDAO.update(kh);
    }

    public boolean xoaKhachHang(String maKH) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.DELETE_KHACHHANG, maKH));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return khachHangDAO.delete(maKH);
    }

    public KhachHang timKhachHangTheoSoDienThoai(String soDienThoai) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.FIND_KHACHHANG_BY_PHONE, soDienThoai));
            if (resp.isSuccess()) return (KhachHang) resp.getData();
            return null;
        }
        return khachHangDAO.timTheoSoDienThoai(soDienThoai);
    }

    /**
     * Tạo mã khách hàng tự động
     */
    public String taoMaKhachHang() {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.TAO_MA_KHACHHANG));
            if (resp.isSuccess() && resp.getData() != null) return (String) resp.getData();
            return null;
        }
        List<KhachHang> danhSach = khachHangDAO.getAll();
        int maxId = 0;
        for (KhachHang kh : danhSach) {
            String maKH = kh.getMaKhachHang();
            if (maKH != null && maKH.startsWith("KH")) {
                try {
                    int id = Integer.parseInt(maKH.substring(2));
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                    // Bỏ qua các mã không hợp lệ
                }
            }
        }
        return String.format("KH%02d", maxId + 1);
    }
}
