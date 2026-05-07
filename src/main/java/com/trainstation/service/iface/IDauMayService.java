package com.trainstation.service.iface;

import com.trainstation.dto.CreateDauMayRequest;
import com.trainstation.dto.DauMayDTO;
import com.trainstation.dto.UpdateDauMayRequest;
import com.trainstation.model.DauMay;

import java.util.List;

public interface IDauMayService {
    List<DauMay> layTatCaDauMay();
    DauMay timDauMayTheoMa(String maDauMay);
    boolean themDauMay(DauMay dauMay);
    boolean capNhatDauMay(DauMay dauMay);
    boolean xoaDauMay(String maDauMay);
    String taoMaDauMay();
    boolean dungHoatDongDauMay(String maDauMay);
    List<DauMay> layDauMayDangHoatDong();
    DauMayDTO taoDauMayTuRequest(CreateDauMayRequest request);
    DauMayDTO capNhatDauMayTuRequest(UpdateDauMayRequest request);
}
