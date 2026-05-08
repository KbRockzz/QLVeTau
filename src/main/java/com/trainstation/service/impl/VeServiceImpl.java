package com.trainstation.service.impl;

import com.trainstation.dto.CreateVeRequest;
import com.trainstation.dto.UpdateVeRequest;
import com.trainstation.dto.VeDTO;
import com.trainstation.mapper.JacksonVeMapper;
import com.trainstation.mapper.VeMapper;
import com.trainstation.model.Ve;
import com.trainstation.repository.IVeRepository;
import com.trainstation.repository.impl.VeRepositoryImpl;
import com.trainstation.service.iface.IVeService;

import java.util.List;

public class VeServiceImpl implements IVeService {
    private static VeServiceImpl instance;
    private final IVeRepository veRepository;
    private final VeMapper mapper;

    private VeServiceImpl() {
        this.veRepository = VeRepositoryImpl.getInstance();
        this.mapper = JacksonVeMapper.getInstance();
    }

    public static synchronized VeServiceImpl getInstance() {
        if (instance == null) {
            instance = new VeServiceImpl();
        }
        return instance;
    }

    @Override
    public List<Ve> layTatCaVe() {
        return veRepository.getAll();
    }

    @Override
    public Ve timVeTheoMa(String maVe) {
        return veRepository.findById(maVe);
    }

    @Override
    public List<Ve> layVeTheoChuyen(String maChuyen) {
        return veRepository.findByChuyen(maChuyen);
    }

    public List<Ve> layVeTheoTrangThai(String trangThai) {
        return veRepository.findByTrangThai(trangThai);
    }

    @Override
    public Ve taoVe(Ve ve) {
        return veRepository.insert(ve) ? ve : null;
    }

    @Override
    public boolean capNhatVe(Ve ve) {
        return veRepository.update(ve);
    }

    @Override
    public boolean xoaVe(String maVe) {
        return veRepository.delete(maVe);
    }

    @Override
    public VeDTO taoVeTuRequest(CreateVeRequest request) {
        Ve ve = mapper.fromCreateRequest(request, generateVeId());
        return veRepository.insert(ve) ? mapper.toDTO(ve) : null;
    }

    @Override
    public VeDTO capNhatVeTuRequest(UpdateVeRequest request) {
        Ve ve = veRepository.findById(request.getMaVe());
        if (ve == null) return null;
        if (request.getTrangThai() != null) ve.setTrangThai(request.getTrangThai());
        if (request.getGiaThanhToan() != null) ve.setGiaThanhToan(request.getGiaThanhToan());
        if (veRepository.update(ve)) {
            return mapper.toDTO(ve);
        }
        return null;
    }

    private String generateVeId() {
        List<Ve> all = veRepository.getAll();
        int maxId = all.stream()
                .filter(v -> v.getMaVe() != null && v.getMaVe().startsWith("VE"))
                .mapToInt(v -> {
                    try { return Integer.parseInt(v.getMaVe().substring(2)); }
                    catch (NumberFormatException e) { return 0; }
                }).max().orElse(0);
        return String.format("VE%04d", maxId + 1);
    }
}
