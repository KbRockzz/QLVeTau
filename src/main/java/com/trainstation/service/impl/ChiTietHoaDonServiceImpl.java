package com.trainstation.service.impl;

import com.trainstation.dto.ChiTietHoaDonDTO;
import com.trainstation.dto.CreateChiTietHoaDonRequest;
import com.trainstation.dto.UpdateChiTietHoaDonRequest;
import com.trainstation.mapper.ChiTietHoaDonMapper;
import com.trainstation.mapper.JacksonChiTietHoaDonMapper;
import com.trainstation.model.ChiTietHoaDon;
import com.trainstation.repository.IChiTietHoaDonRepository;
import com.trainstation.repository.impl.ChiTietHoaDonRepositoryImpl;
import com.trainstation.service.ChiTietHoaDonService;
import com.trainstation.service.iface.IChiTietHoaDonService;

import java.util.List;

public class ChiTietHoaDonServiceImpl implements IChiTietHoaDonService {
    private static ChiTietHoaDonServiceImpl instance;
    private final ChiTietHoaDonService legacyChiTietHoaDonService;
    private final IChiTietHoaDonRepository chiTietHoaDonRepository;
    private final ChiTietHoaDonMapper mapper;

    private ChiTietHoaDonServiceImpl() {
        this.legacyChiTietHoaDonService = ChiTietHoaDonService.getInstance();
        this.chiTietHoaDonRepository = ChiTietHoaDonRepositoryImpl.getInstance();
        this.mapper = JacksonChiTietHoaDonMapper.getInstance();
    }

    public static synchronized ChiTietHoaDonServiceImpl getInstance() {
        if (instance == null) {
            instance = new ChiTietHoaDonServiceImpl();
        }
        return instance;
    }

    @Override
    public ChiTietHoaDon themChiTiet(ChiTietHoaDon chiTiet) {
        return legacyChiTietHoaDonService.themChiTiet(chiTiet);
    }

    @Override
    public boolean capNhatChiTiet(ChiTietHoaDon chiTiet) {
        return chiTietHoaDonRepository.update(chiTiet);
    }

    @Override
    public List<ChiTietHoaDon> getByHoaDon(String maHoaDon) {
        return chiTietHoaDonRepository.findByHoaDon(maHoaDon);
    }

    @Override
    public List<ChiTietHoaDon> layTatCa() {
        return chiTietHoaDonRepository.getAll();
    }

    @Override
    public boolean xoaChiTiet(String maHoaDon, String maVe) {
        return legacyChiTietHoaDonService.xoaChiTiet(maHoaDon, maVe);
    }

    @Override
    public ChiTietHoaDonDTO taoChiTietHoaDonTuRequest(CreateChiTietHoaDonRequest request) {
        ChiTietHoaDon chiTiet = mapper.fromCreateRequest(request);
        ChiTietHoaDon created = legacyChiTietHoaDonService.themChiTiet(chiTiet);
        return created != null ? mapper.toDTO(created) : null;
    }

    @Override
    public ChiTietHoaDonDTO capNhatChiTietHoaDonTuRequest(UpdateChiTietHoaDonRequest request) {
        ChiTietHoaDon chiTiet = chiTietHoaDonRepository.findById(request.getMaVe());
        if (chiTiet == null) return null;
        chiTiet.setMaLoaiVe(request.getMaLoaiVe());
        chiTiet.setGiaGoc(request.getGiaGoc());
        chiTiet.setGiaDaKM(request.getGiaDaKM());
        chiTiet.setMoTa(request.getMoTa());
        if (chiTietHoaDonRepository.update(chiTiet)) {
            return mapper.toDTO(chiTiet);
        }
        return null;
    }
}
