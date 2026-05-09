package com.trainstation.service.iface;

import com.trainstation.dto.CreateTaiKhoanRequest;
import com.trainstation.dto.TaiKhoanDTO;
import com.trainstation.dto.UpdateTaiKhoanRequest;
import com.trainstation.model.TaiKhoan;

import java.util.List;

public interface ITaiKhoanService {
    TaiKhoan xacThuc(String tenTaiKhoan, String matKhau);
    String taoMaTaiKhoan();
    List<TaiKhoan> layTatCaTaiKhoan();
    TaiKhoan timTaiKhoanTheoMa(String maTK);
    boolean themTaiKhoan(TaiKhoan tk);
    boolean capNhatTaiKhoan(TaiKhoan tk);
    boolean doiMatKhau(String maTK, String matKhauMoi);
    boolean xoaTaiKhoan(String maTK);
    TaiKhoanDTO taoTaiKhoanTuRequest(CreateTaiKhoanRequest request);
    TaiKhoanDTO capNhatTaiKhoanTuRequest(UpdateTaiKhoanRequest request);
}
