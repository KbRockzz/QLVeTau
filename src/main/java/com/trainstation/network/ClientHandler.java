package com.trainstation.network;

import com.trainstation.dao.*;
import com.trainstation.model.*;
import com.trainstation.service.*;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private static final Logger LOG = Logger.getLogger(ClientHandler.class.getName());
    private final Socket socket;

    private final KhachHangService khachHangService = KhachHangService.getInstance();
    private final NhanVienService nhanVienService = NhanVienService.getInstance();
    private final TaiKhoanService taiKhoanService = TaiKhoanService.getInstance();
    private final GaService gaService = GaService.getInstance();
    private final DauMayService dauMayService = DauMayService.getInstance();
    private final VeService veService = VeService.getInstance();
    private final ThongKeService thongKeService = ThongKeService.getInstance();
    private final ChuyenTauDAO chuyenTauDAO = ChuyenTauDAO.getInstance();
    private final ToaTauDAO toaTauDAO = ToaTauDAO.getInstance();
    private final GheDAO gheDAO = GheDAO.getInstance();
    private final BangGiaDAO bangGiaDAO = BangGiaDAO.getInstance();
    private final ChangTauDAO changTauDAO = ChangTauDAO.getInstance();
    private final HoaDonDAO hoaDonDAO = HoaDonDAO.getInstance();
    private final KhachHangDAO khachHangDAO = KhachHangDAO.getInstance();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String clientAddr = socket.getInetAddress().getHostAddress();
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            in.setObjectInputFilter(SerializationFilter.INSTANCE);
            try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
                AppRequest req = (AppRequest) in.readObject();
                AppResponse resp = dispatch(req);
                out.writeObject(resp);
                out.flush();
            }
        } catch (Exception e) {
            LOG.warning("Error handling client " + clientAddr + ": " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    @SuppressWarnings("unchecked")
    private AppResponse dispatch(AppRequest req) {
        try {
            Object[] p = req.getParams();
            switch (req.getType()) {
                // ── KhachHang ──────────────────────────────────────────────
                case GET_ALL_KHACHHANG:
                    return AppResponse.ok(khachHangService.layTatCaKhachHang());
                case FIND_KHACHHANG_BY_ID:
                    return AppResponse.ok(khachHangService.timKhachHangTheoMa((String) p[0]));
                case INSERT_KHACHHANG:
                    return AppResponse.ok(khachHangService.themKhachHang((KhachHang) p[0]));
                case UPDATE_KHACHHANG:
                    return AppResponse.ok(khachHangService.capNhatKhachHang((KhachHang) p[0]));
                case DELETE_KHACHHANG:
                    return AppResponse.ok(khachHangService.xoaKhachHang((String) p[0]));
                case FIND_KHACHHANG_BY_PHONE:
                    return AppResponse.ok(khachHangService.timKhachHangTheoSoDienThoai((String) p[0]));
                case TAO_MA_KHACHHANG:
                    return AppResponse.ok(khachHangService.taoMaKhachHang());
                case GET_DELETED_KHACHHANG:
                    return AppResponse.ok(khachHangDAO.getDeletedCustomers());
                case RESTORE_KHACHHANG:
                    return AppResponse.ok(khachHangDAO.restoreCustomer((String) p[0]));

                // ── NhanVien ───────────────────────────────────────────────
                case GET_ALL_NHANVIEN:
                    return AppResponse.ok(nhanVienService.layTatCaNhanVien());
                case FIND_NHANVIEN_BY_ID:
                    return AppResponse.ok(nhanVienService.timNhanVienTheoMa((String) p[0]));
                case INSERT_NHANVIEN:
                    return AppResponse.ok(nhanVienService.themNhanVien((NhanVien) p[0]));
                case UPDATE_NHANVIEN:
                    return AppResponse.ok(nhanVienService.capNhatNhanVien((NhanVien) p[0]));
                case DELETE_NHANVIEN:
                    return AppResponse.ok(nhanVienService.xoaNhanVien((String) p[0]));

                // ── TaiKhoan ───────────────────────────────────────────────
                case DANG_NHAP:
                    return AppResponse.ok(taiKhoanService.xacThuc((String) p[0], (String) p[1]));
                case GET_ALL_TAIKHOAN:
                    return AppResponse.ok(taiKhoanService.layTatCaTaiKhoan());
                case INSERT_TAIKHOAN:
                    return AppResponse.ok(taiKhoanService.themTaiKhoan((TaiKhoan) p[0]));
                case UPDATE_TAIKHOAN:
                    return AppResponse.ok(taiKhoanService.capNhatTaiKhoan((TaiKhoan) p[0]));
                case DELETE_TAIKHOAN:
                    return AppResponse.ok(taiKhoanService.xoaTaiKhoan((String) p[0]));

                // ── Ga ─────────────────────────────────────────────────────
                case GET_ALL_GA:
                    return AppResponse.ok(gaService.layTatCaGa());
                case FIND_GA_BY_ID:
                    return AppResponse.ok(gaService.timGaTheoMa((String) p[0]));
                case INSERT_GA:
                    return AppResponse.ok(gaService.themGa((Ga) p[0]));
                case UPDATE_GA:
                    return AppResponse.ok(gaService.capNhatGa((Ga) p[0]));
                case DELETE_GA:
                    return AppResponse.ok(gaService.xoaGa((String) p[0]));
                case GET_DELETED_GA:
                    return AppResponse.ok(gaService.layGaDaXoa());
                case RESTORE_GA:
                    return AppResponse.ok(gaService.khoiPhucGa((String) p[0]));

                // ── DauMay ─────────────────────────────────────────────────
                case GET_ALL_DAUMAY:
                    return AppResponse.ok(dauMayService.layTatCaDauMay());
                case FIND_DAUMAY_BY_ID:
                    return AppResponse.ok(dauMayService.timDauMayTheoMa((String) p[0]));
                case INSERT_DAUMAY:
                    return AppResponse.ok(dauMayService.themDauMay((DauMay) p[0]));
                case UPDATE_DAUMAY:
                    return AppResponse.ok(dauMayService.capNhatDauMay((DauMay) p[0]));
                case DELETE_DAUMAY:
                    return AppResponse.ok(dauMayService.xoaDauMay((String) p[0]));

                // ── ChuyenTau ──────────────────────────────────────────────
                case GET_ALL_CHUYENTAU:
                    return AppResponse.ok(chuyenTauDAO.getAll());
                case FIND_CHUYENTAU_BY_ID:
                    return AppResponse.ok(chuyenTauDAO.findById((String) p[0]));
                case INSERT_CHUYENTAU:
                    return AppResponse.ok(chuyenTauDAO.insert((ChuyenTau) p[0]));
                case UPDATE_CHUYENTAU:
                    return AppResponse.ok(chuyenTauDAO.update((ChuyenTau) p[0]));
                case DELETE_CHUYENTAU:
                    return AppResponse.ok(chuyenTauDAO.delete((String) p[0]));
                case TIM_KIEM_CHUYENTAU:
                    return AppResponse.ok(chuyenTauDAO.timKiemChuyenTau(
                        (String) p[0], (String) p[1], (LocalDate) p[2], (LocalTime) p[3]));

                // ── Ve ─────────────────────────────────────────────────────
                case GET_ALL_VE:
                    return AppResponse.ok(veService.layTatCaVe());
                case FIND_VE_BY_ID:
                    return AppResponse.ok(veService.timVeTheoMa((String) p[0]));
                case INSERT_VE:
                    return AppResponse.ok(veService.taoVe((Ve) p[0]));
                case UPDATE_VE:
                    return AppResponse.ok(veService.capNhatVe((Ve) p[0]));
                case DELETE_VE:
                    return AppResponse.ok(veService.xoaVe((String) p[0]));
                case GET_VE_BY_CHUYEN:
                    return AppResponse.ok(veService.layVeTheoChuyenTau((String) p[0]));
                case GET_VE_BY_KHACHHANG:
                    return AppResponse.ok(veService.layVeTheoKhachHang((String) p[0]));
                case GET_VE_BY_TRANGTHAI:
                    return AppResponse.ok(veService.layVeTheoTrangThai((String) p[0]));

                // ── ToaTau ─────────────────────────────────────────────────
                case GET_ALL_TOATAU:
                    return AppResponse.ok(toaTauDAO.getAll());
                case INSERT_TOATAU:
                    return AppResponse.ok(toaTauDAO.insert((ToaTau) p[0]));
                case UPDATE_TOATAU:
                    return AppResponse.ok(toaTauDAO.update((ToaTau) p[0]));
                case DELETE_TOATAU:
                    return AppResponse.ok(toaTauDAO.delete((String) p[0]));

                // ── Ghe ────────────────────────────────────────────────────
                case GET_ALL_GHE:
                    return AppResponse.ok(gheDAO.getAll());
                case GET_GHE_BY_TOA:
                    return AppResponse.ok(gheDAO.getByToa((String) p[0]));
                case UPDATE_GHE:
                    return AppResponse.ok(gheDAO.update((Ghe) p[0]));

                // ── BangGia ────────────────────────────────────────────────
                case GET_ALL_BANGGIA:
                    return AppResponse.ok(bangGiaDAO.getAll());
                case INSERT_BANGGIA:
                    return AppResponse.ok(bangGiaDAO.insert((BangGia) p[0]));
                case UPDATE_BANGGIA:
                    return AppResponse.ok(bangGiaDAO.update((BangGia) p[0]));
                case DELETE_BANGGIA:
                    return AppResponse.ok(bangGiaDAO.delete((String) p[0]));

                // ── ChangTau ───────────────────────────────────────────────
                case GET_ALL_CHANG:
                    return AppResponse.ok(changTauDAO.getAll());
                case INSERT_CHANG:
                    return AppResponse.ok(changTauDAO.insert((ChangTau) p[0]));
                case UPDATE_CHANG:
                    return AppResponse.ok(changTauDAO.update((ChangTau) p[0]));
                case DELETE_CHANG:
                    return AppResponse.ok(changTauDAO.delete((String) p[0]));

                // ── HoaDon ─────────────────────────────────────────────────
                case GET_ALL_HOADON:
                    return AppResponse.ok(hoaDonDAO.getAll());
                case INSERT_HOADON:
                    return AppResponse.ok(hoaDonDAO.insert((HoaDon) p[0]));

                // ── ThongKe ────────────────────────────────────────────────
                case THONG_KE_TONG_DOANH_THU:
                    return AppResponse.ok(thongKeService.tinhTongDoanhThu());
                case THONG_KE_THEO_THANG:
                    return AppResponse.ok(thongKeService.tinhDoanhThuTheoThang((int) p[0], (int) p[1]));

                default:
                    return AppResponse.error("Unsupported request type: " + req.getType());
            }
        } catch (Exception e) {
            LOG.severe("Dispatch error for " + req.getType() + ": " + e.getMessage());
            return AppResponse.error("Server error: " + e.getMessage());
        }
    }
}
