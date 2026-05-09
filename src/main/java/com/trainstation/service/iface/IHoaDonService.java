package com.trainstation.service.iface;

import com.trainstation.dto.CreateHoaDonRequest;
import com.trainstation.dto.HoaDonDTO;
import com.trainstation.dto.UpdateHoaDonRequest;
import com.trainstation.model.HoaDon;

import java.util.List;

public interface IHoaDonService {
    HoaDon taoHoaDon(HoaDon hoaDon);
    boolean capNhatHoaDon(HoaDon hoaDon);
    HoaDon timHoaDonTheoMa(String maHoaDon);
    List<HoaDon> layTatCaHoaDon();
    boolean xoaHoaDon(String maHoaDon);
    HoaDonDTO taoHoaDonTuRequest(CreateHoaDonRequest request);
    HoaDonDTO capNhatHoaDonTuRequest(UpdateHoaDonRequest request);
}
