package com.trainstation.service.iface;

import com.trainstation.dto.CreateNhanVienRequest;
import com.trainstation.dto.NhanVienDTO;
import com.trainstation.dto.UpdateNhanVienRequest;
import com.trainstation.model.NhanVien;

import java.util.List;

public interface INhanVienService {
    List<NhanVien> layTatCaNhanVien();
    NhanVien timNhanVienTheoMa(String maNV);
    boolean themNhanVien(NhanVien nv);
    boolean capNhatNhanVien(NhanVien nv);
    boolean xoaNhanVien(String maNV);
    String taoMaNhanVien();
    NhanVienDTO taoNhanVienTuRequest(CreateNhanVienRequest request);
    NhanVienDTO capNhatNhanVienTuRequest(UpdateNhanVienRequest request);
}
