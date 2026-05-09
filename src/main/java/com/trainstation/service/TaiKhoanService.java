package com.trainstation.service;

import com.trainstation.dao.TaiKhoanDAO;
import com.trainstation.model.NhanVien;
import com.trainstation.model.TaiKhoan;
import com.trainstation.network.AppClient;
import com.trainstation.network.AppRequest;
import com.trainstation.network.AppResponse;
import com.trainstation.network.NetworkConfig;
import com.trainstation.network.RequestType;
import com.trainstation.service.impl.TaiKhoanServiceImpl;
import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Tài khoản
 */
public class TaiKhoanService {
    private static TaiKhoanService instance;
    private final TaiKhoanDAO taiKhoanDAO;
    private final TaiKhoanServiceImpl taiKhoanServiceImpl;

    private TaiKhoanService() {
        this.taiKhoanDAO = TaiKhoanDAO.getInstance();
        this.taiKhoanServiceImpl = TaiKhoanServiceImpl.getInstance();
    }

    public static synchronized TaiKhoanService getInstance() {
        if (instance == null) {
            instance = new TaiKhoanService();
        }
        return instance;
    }

    /**
     * Xác thực đăng nhập
     */
    public TaiKhoan xacThuc(String tenTaiKhoan, String matKhau) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.DANG_NHAP, tenTaiKhoan, matKhau));
            if (resp.isSuccess()) return (TaiKhoan) resp.getData();
            return null;
        }
        return taiKhoanServiceImpl.xacThuc(tenTaiKhoan, matKhau);
    }

    /**
     * Tạo mã tài khoản tự động
     */
    public String taoMaTaiKhoan() {
        return taiKhoanServiceImpl.taoMaTaiKhoan();
    }

    /**
     * Lấy tất cả tài khoản
     */
    public List<TaiKhoan> layTatCaTaiKhoan() {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.GET_ALL_TAIKHOAN));
            if (resp.isSuccess() && resp.getData() != null) return (List<TaiKhoan>) resp.getData();
            return java.util.Collections.emptyList();
        }
        return taiKhoanServiceImpl.layTatCaTaiKhoan();
    }

    /**
     * Tìm tài khoản theo mã
     */
    public TaiKhoan timTaiKhoanTheoMa(String maTK) {
        return taiKhoanServiceImpl.timTaiKhoanTheoMa(maTK);
    }

    /**
     * Thêm tài khoản mới
     */
    public boolean themTaiKhoan(TaiKhoan tk) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.INSERT_TAIKHOAN, tk));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return taiKhoanServiceImpl.themTaiKhoan(tk);
    }

    /**
     * Cập nhật thông tin tài khoản
     */
    public boolean capNhatTaiKhoan(TaiKhoan tk) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.UPDATE_TAIKHOAN, tk));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return taiKhoanServiceImpl.capNhatTaiKhoan(tk);
    }

    /**
     * Đổi mật khẩu
     */
    public boolean doiMatKhau(String maTK, String matKhauMoi) {
        return taiKhoanServiceImpl.doiMatKhau(maTK, matKhauMoi);
    }

    /**
     * Xóa tài khoản
     */
    public boolean xoaTaiKhoan(String maTK) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.DELETE_TAIKHOAN, maTK));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return taiKhoanServiceImpl.xoaTaiKhoan(maTK);
    }
}
