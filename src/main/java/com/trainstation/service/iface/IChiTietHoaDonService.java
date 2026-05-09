package com.trainstation.service.iface;

import com.trainstation.dto.ChiTietHoaDonDTO;
import com.trainstation.dto.CreateChiTietHoaDonRequest;
import com.trainstation.dto.UpdateChiTietHoaDonRequest;
import com.trainstation.model.ChiTietHoaDon;

import java.util.List;

public interface IChiTietHoaDonService {
    ChiTietHoaDon themChiTiet(ChiTietHoaDon chiTiet);
    boolean capNhatChiTiet(ChiTietHoaDon chiTiet);
    List<ChiTietHoaDon> getByHoaDon(String maHoaDon);
    List<ChiTietHoaDon> layTatCa();
    boolean xoaChiTiet(String maHoaDon, String maVe);
    ChiTietHoaDonDTO taoChiTietHoaDonTuRequest(CreateChiTietHoaDonRequest request);
    ChiTietHoaDonDTO capNhatChiTietHoaDonTuRequest(UpdateChiTietHoaDonRequest request);
}
