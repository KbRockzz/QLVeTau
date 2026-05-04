package com.trainstation.service.impl;

import com.trainstation.dao.VeDAO;
import com.trainstation.dto.CreateVeRequest;
import com.trainstation.dto.UpdateVeRequest;
import com.trainstation.dto.VeDTO;
import com.trainstation.mapper.JacksonVeMapper;
import com.trainstation.mapper.VeMapper;
import com.trainstation.model.Ve;
import com.trainstation.service.VeService;
import com.trainstation.service.iface.IVeService;

import java.util.List;

public class VeServiceImpl implements IVeService {
    private static VeServiceImpl instance;
    private final VeService veService;
    private final VeDAO veDAO;
    private final VeMapper mapper;

    private VeServiceImpl() {
        this.veService = VeService.getInstance();
        this.veDAO = VeDAO.getInstance();
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
        return veDAO.getAll();
    }

    @Override
    public Ve timVeTheoMa(String maVe) {
        return veDAO.findById(maVe);
    }

    @Override
    public List<Ve> layVeTheoChuyen(String maChuyen) {
        return veDAO.getAll().stream()
                .filter(v -> maChuyen.equals(v.getMaChuyen()))
                .toList();
    }

    @Override
    public Ve taoVe(Ve ve) {
        return veService.taoVe(ve);
    }

    @Override
    public boolean capNhatVe(Ve ve) {
        return veDAO.update(ve);
    }

    @Override
    public boolean xoaVe(String maVe) {
        return veDAO.delete(maVe);
    }

    @Override
    public VeDTO taoVeTuRequest(CreateVeRequest request) {
        Ve ve = mapper.fromCreateRequest(request, generateVeId());
        Ve created = veService.taoVe(ve);
        return created != null ? mapper.toDTO(created) : null;
    }

    @Override
    public VeDTO capNhatVeTuRequest(UpdateVeRequest request) {
        Ve ve = veDAO.findById(request.getMaVe());
        if (ve == null) return null;
        if (request.getTrangThai() != null) ve.setTrangThai(request.getTrangThai());
        if (request.getGiaThanhToan() != null) ve.setGiaThanhToan(request.getGiaThanhToan());
        if (veDAO.update(ve)) {
            return mapper.toDTO(ve);
        }
        return null;
    }

    private String generateVeId() {
        List<Ve> all = veDAO.getAll();
        int maxId = all.stream()
                .filter(v -> v.getMaVe() != null && v.getMaVe().startsWith("VE"))
                .mapToInt(v -> {
                    try { return Integer.parseInt(v.getMaVe().substring(2)); }
                    catch (NumberFormatException e) { return 0; }
                }).max().orElse(0);
        return String.format("VE%04d", maxId + 1);
    }
}
