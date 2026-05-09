package com.trainstation.service.iface;

import com.trainstation.dto.CreateKhachHangRequest;
import com.trainstation.dto.KhachHangDTO;
import com.trainstation.dto.UpdateKhachHangRequest;
import com.trainstation.model.KhachHang;
import java.util.List;

public interface IKhachHangService {
    List<KhachHang> layTatCaKhachHang();
    KhachHang timKhachHangTheoMa(String maKH);
    KhachHang timKhachHangTheoSoDienThoai(String soDienThoai);
    boolean themKhachHang(KhachHang kh);
    boolean capNhatKhachHang(KhachHang kh);
    boolean xoaKhachHang(String maKH);
    String taoMaKhachHang();
    KhachHangDTO taoKhachHangTuRequest(CreateKhachHangRequest request);
    KhachHangDTO capNhatKhachHangTuRequest(UpdateKhachHangRequest request);
}
