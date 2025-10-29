package com.trainstation.service;

import com.trainstation.dao.TaiKhoanDAO;
import com.trainstation.model.NhanVien;
import com.trainstation.model.TaiKhoan;
import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Tài khoản
 */
public class TaiKhoanService {
    private static TaiKhoanService instance;
    private final TaiKhoanDAO taiKhoanDAO;

    private TaiKhoanService() {
        this.taiKhoanDAO = TaiKhoanDAO.getInstance();
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
        List<TaiKhoan> danhSach = taiKhoanDAO.getAll();
        for (TaiKhoan tk : danhSach) {
            if (tk.getTenTaiKhoan().equals(tenTaiKhoan) && 
                tk.getMatKhau().equals(matKhau) &&
                "Hoạt động".equals(tk.getTrangThai())) {
                return tk;
            }
        }
        return null;
    }

    /**
     * Tạo mã tài khoản tự động
     */
    public String taoMaTaiKhoan() {
        List<TaiKhoan> danhSach = taiKhoanDAO.getAll();
        int maxId = 0;
        for (TaiKhoan tk : danhSach) {
            String maTK = tk.getMaTK();
            if (maTK != null && maTK.startsWith("TK")) {
                try {
                    int id = Integer.parseInt(maTK.substring(2));
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs
                }
            }
        }
        return String.format("TK%02d", maxId + 1);
    }

    /**
     * Lấy tất cả tài khoản
     */
    public List<TaiKhoan> layTatCaTaiKhoan() {
        return taiKhoanDAO.getAll();
    }

    /**
     * Tìm tài khoản theo mã
     */
    public TaiKhoan timTaiKhoanTheoMa(String maTK) {
        return taiKhoanDAO.findById(maTK);
    }

    /**
     * Thêm tài khoản mới
     */
    public boolean themTaiKhoan(TaiKhoan tk) {
        return taiKhoanDAO.insert(tk);
    }

    /**
     * Cập nhật thông tin tài khoản
     */
    public boolean capNhatTaiKhoan(TaiKhoan tk) {
        return taiKhoanDAO.update(tk);
    }

    /**
     * Đổi mật khẩu
     */
    public boolean doiMatKhau(String maTK, String matKhauMoi) {
        TaiKhoan tk = taiKhoanDAO.findById(maTK);
        if (tk != null) {
            tk.setMatKhau(matKhauMoi);
            return taiKhoanDAO.update(tk);
        }
        return false;
    }

    /**
     * Xóa tài khoản
     */
    public boolean xoaTaiKhoan(String maTK) {
        return taiKhoanDAO.delete(maTK);
    }
}
