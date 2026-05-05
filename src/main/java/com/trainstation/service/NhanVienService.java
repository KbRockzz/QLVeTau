package com.trainstation.service;

import com.trainstation.dao.NhanVienDAO;
import com.trainstation.model.NhanVien;
import com.trainstation.network.AppClient;
import com.trainstation.network.AppRequest;
import com.trainstation.network.AppResponse;
import com.trainstation.network.NetworkConfig;
import com.trainstation.network.RequestType;
import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Nhân viên
 */
public class NhanVienService {
    private static NhanVienService instance;
    private final NhanVienDAO nhanVienDAO;

    private NhanVienService() {
        this.nhanVienDAO = NhanVienDAO.getInstance();
    }

    public static synchronized NhanVienService getInstance() {
        if (instance == null) {
            instance = new NhanVienService();
        }
        return instance;
    }

    /**
     * Lấy tất cả nhân viên
     */
    public List<NhanVien> layTatCaNhanVien() {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.GET_ALL_NHANVIEN));
            if (resp.isSuccess() && resp.getData() != null) return (List<NhanVien>) resp.getData();
            return java.util.Collections.emptyList();
        }
        return nhanVienDAO.getAll();
    }

    /**
     * Tìm nhân viên theo mã
     */
    public NhanVien timNhanVienTheoMa(String maNV) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.FIND_NHANVIEN_BY_ID, maNV));
            if (resp.isSuccess()) return (NhanVien) resp.getData();
            return null;
        }
        return nhanVienDAO.findById(maNV);
    }

    /**
     * Thêm nhân viên mới
     */
    public boolean themNhanVien(NhanVien nv) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.INSERT_NHANVIEN, nv));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return nhanVienDAO.insert(nv);
    }

    /**
     * Cập nhật thông tin nhân viên
     */
    public boolean capNhatNhanVien(NhanVien nv) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.UPDATE_NHANVIEN, nv));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return nhanVienDAO.update(nv);
    }

    /**
     * Xóa nhân viên
     */
    public boolean xoaNhanVien(String maNV) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.DELETE_NHANVIEN, maNV));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return nhanVienDAO.delete(maNV);
    }

    /**
     * Tạo mã nhân viên tự động
     */
    public String taoMaNhanVien() {
        List<NhanVien> danhSach = nhanVienDAO.getAll();
        int maxId = 0;
        for (NhanVien nv : danhSach) {
            String maNV = nv.getMaNV();
            if (maNV != null && maNV.startsWith("NV")) {
                try {
                    int id = Integer.parseInt(maNV.substring(2));
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs
                }
            }
        }
        return String.format("NV%02d", maxId + 1);
    }
}
