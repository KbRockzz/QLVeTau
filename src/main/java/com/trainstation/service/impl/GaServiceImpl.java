package com.trainstation.service.impl;

import com.trainstation.dto.CreateGaRequest;
import com.trainstation.dto.GaDTO;
import com.trainstation.dto.UpdateGaRequest;
import com.trainstation.mapper.GaMapper;
import com.trainstation.mapper.JacksonGaMapper;
import com.trainstation.model.Ga;
import com.trainstation.repository.IGaRepository;
import com.trainstation.repository.impl.GaRepositoryImpl;
import com.trainstation.service.iface.IGaService;

import java.util.List;

public class GaServiceImpl implements IGaService {
    private static GaServiceImpl instance;
    private final IGaRepository gaRepository;
    private final GaMapper mapper;

    private GaServiceImpl() {
        this.gaRepository = GaRepositoryImpl.getInstance();
        this.mapper = JacksonGaMapper.getInstance();
    }

    public static synchronized GaServiceImpl getInstance() {
        if (instance == null) {
            instance = new GaServiceImpl();
        }
        return instance;
    }

    @Override
    public List<Ga> layTatCaGa() {
        return gaRepository.getAll();
    }

    @Override
    public Ga timGaTheoMa(String maGa) {
        return gaRepository.findById(maGa);
    }

    @Override
    public boolean themGa(Ga ga) {
        return gaRepository.insert(ga);
    }

    @Override
    public boolean capNhatGa(Ga ga) {
        return gaRepository.update(ga);
    }

    @Override
    public boolean xoaGa(String maGa) {
        return gaRepository.delete(maGa);
    }

    @Override
    public String taoMaGa() {
        return gaRepository.generateNextMaGa();
    }

    @Override
    public List<Ga> layGaDaXoa() {
        return gaRepository.getDeletedStations();
    }

    @Override
    public boolean khoiPhucGa(String maGa) {
        return gaRepository.restoreStation(maGa);
    }

    @Override
    public GaDTO taoGaTuRequest(CreateGaRequest request) {
        String maGa = taoMaGa();
        Ga ga = mapper.fromCreateRequest(request, maGa);
        if (gaRepository.insert(ga)) {
            return mapper.toDTO(ga);
        }
        return null;
    }

    @Override
    public GaDTO capNhatGaTuRequest(UpdateGaRequest request) {
        Ga ga = gaRepository.findById(request.getMaGa());
        if (ga == null) return null;
        ga.setTenGa(request.getTenGa());
        ga.setMoTa(request.getMoTa());
        ga.setTinhTrang(request.getTinhTrang());
        ga.setDiaChi(request.getDiaChi());
        if (gaRepository.update(ga)) {
            return mapper.toDTO(ga);
        }
        return null;
    }
}
