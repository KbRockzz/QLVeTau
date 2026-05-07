package com.trainstation.service.impl;

import com.trainstation.dto.CreateDauMayRequest;
import com.trainstation.dto.DauMayDTO;
import com.trainstation.dto.UpdateDauMayRequest;
import com.trainstation.mapper.DauMayMapper;
import com.trainstation.mapper.JacksonDauMayMapper;
import com.trainstation.model.DauMay;
import com.trainstation.repository.IDauMayRepository;
import com.trainstation.repository.impl.DauMayRepositoryImpl;
import com.trainstation.service.iface.IDauMayService;

import java.util.List;

public class DauMayServiceImpl implements IDauMayService {
    private static DauMayServiceImpl instance;
    private final IDauMayRepository dauMayRepository;
    private final DauMayMapper mapper;

    private DauMayServiceImpl() {
        this.dauMayRepository = DauMayRepositoryImpl.getInstance();
        this.mapper = JacksonDauMayMapper.getInstance();
    }

    public static synchronized DauMayServiceImpl getInstance() {
        if (instance == null) {
            instance = new DauMayServiceImpl();
        }
        return instance;
    }

    @Override
    public List<DauMay> layTatCaDauMay() {
        return dauMayRepository.getAll();
    }

    @Override
    public DauMay timDauMayTheoMa(String maDauMay) {
        return dauMayRepository.findById(maDauMay);
    }

    @Override
    public boolean themDauMay(DauMay dauMay) {
        return dauMayRepository.insert(dauMay);
    }

    @Override
    public boolean capNhatDauMay(DauMay dauMay) {
        return dauMayRepository.update(dauMay);
    }

    @Override
    public boolean xoaDauMay(String maDauMay) {
        return dauMayRepository.delete(maDauMay);
    }

    @Override
    public String taoMaDauMay() {
        return dauMayRepository.generateNextMaDauMay();
    }

    @Override
    public boolean dungHoatDongDauMay(String maDauMay) {
        return dauMayRepository.dungHoatDongDauMay(maDauMay);
    }

    @Override
    public List<DauMay> layDauMayDangHoatDong() {
        return dauMayRepository.layDauMayHoatDong();
    }

    @Override
    public DauMayDTO taoDauMayTuRequest(CreateDauMayRequest request) {
        String maDauMay = taoMaDauMay();
        DauMay dauMay = mapper.fromCreateRequest(request, maDauMay);
        if (dauMayRepository.insert(dauMay)) {
            return mapper.toDTO(dauMay);
        }
        return null;
    }

    @Override
    public DauMayDTO capNhatDauMayTuRequest(UpdateDauMayRequest request) {
        DauMay dauMay = dauMayRepository.findById(request.getMaDauMay());
        if (dauMay == null) return null;
        dauMay.setLoaiDauMay(request.getLoaiDauMay());
        dauMay.setTenDauMay(request.getTenDauMay());
        dauMay.setNamSX(request.getNamSX());
        dauMay.setLanBaoTriGanNhat(request.getLanBaoTriGanNhat());
        dauMay.setTrangThai(request.getTrangThai());
        if (dauMayRepository.update(dauMay)) {
            return mapper.toDTO(dauMay);
        }
        return null;
    }
}
