package com.trainstation.service.impl;

import com.trainstation.dao.ChuyenTauDAO;
import com.trainstation.dto.ChuyenTauDTO;
import com.trainstation.dto.CreateChuyenTauRequest;
import com.trainstation.dto.UpdateChuyenTauRequest;
import com.trainstation.mapper.ChuyenTauMapper;
import com.trainstation.mapper.JacksonChuyenTauMapper;
import com.trainstation.model.ChuyenTau;
import com.trainstation.service.iface.IChuyenTauService;

import java.util.List;
import java.util.stream.Collectors;

public class ChuyenTauServiceImpl implements IChuyenTauService {
    private static ChuyenTauServiceImpl instance;
    private final ChuyenTauDAO chuyenTauDAO;
    private final ChuyenTauMapper mapper;

    private ChuyenTauServiceImpl() {
        this.chuyenTauDAO = ChuyenTauDAO.getInstance();
        this.mapper = JacksonChuyenTauMapper.getInstance();
    }

    public static synchronized ChuyenTauServiceImpl getInstance() {
        if (instance == null) {
            instance = new ChuyenTauServiceImpl();
        }
        return instance;
    }

    @Override
    public List<ChuyenTau> layTatCaChuyenTau() {
        return chuyenTauDAO.getAll();
    }

    @Override
    public ChuyenTau timChuyenTauTheoMa(String maChuyen) {
        return chuyenTauDAO.findById(maChuyen);
    }

    @Override
    public List<ChuyenTau> timKiemChuyenTau(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return chuyenTauDAO.getAll();
        }
        String lower = keyword.toLowerCase().trim();
        return chuyenTauDAO.getAll().stream()
                .filter(ct -> (ct.getMaChuyen() != null && ct.getMaChuyen().toLowerCase().contains(lower))
                        || (ct.getMaGaDi() != null && ct.getMaGaDi().toLowerCase().contains(lower))
                        || (ct.getMaGaDen() != null && ct.getMaGaDen().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }

    @Override
    public boolean themChuyenTau(ChuyenTau ct) {
        return chuyenTauDAO.insert(ct);
    }

    @Override
    public boolean capNhatChuyenTau(ChuyenTau ct) {
        return chuyenTauDAO.update(ct);
    }

    @Override
    public boolean xoaChuyenTau(String maChuyen) {
        return chuyenTauDAO.delete(maChuyen);
    }

    @Override
    public ChuyenTauDTO taoChuyenTauTuRequest(CreateChuyenTauRequest request) {
        List<ChuyenTau> all = chuyenTauDAO.getAll();
        int maxId = all.stream()
                .filter(ct -> ct.getMaChuyen() != null && ct.getMaChuyen().startsWith("CT"))
                .mapToInt(ct -> {
                    try { return Integer.parseInt(ct.getMaChuyen().substring(2)); }
                    catch (NumberFormatException e) { return 0; }
                }).max().orElse(0);
        String newId = String.format("CT%03d", maxId + 1);
        ChuyenTau ct = mapper.fromCreateRequest(request, newId);
        ct.setTrangThai("active");
        if (chuyenTauDAO.insert(ct)) {
            return mapper.toDTO(ct);
        }
        return null;
    }

    @Override
    public ChuyenTauDTO capNhatChuyenTauTuRequest(UpdateChuyenTauRequest request) {
        ChuyenTau ct = chuyenTauDAO.findById(request.getMaChuyen());
        if (ct == null) return null;
        ct.setMaDauMay(request.getMaDauMay());
        ct.setMaNV(request.getMaNV());
        ct.setMaGaDi(request.getMaGaDi());
        ct.setMaGaDen(request.getMaGaDen());
        ct.setGioDi(request.getGioDi());
        ct.setGioDen(request.getGioDen());
        ct.setSoKm(request.getSoKm());
        ct.setMaChang(request.getMaChang());
        ct.setTrangThai(request.getTrangThai());
        if (chuyenTauDAO.update(ct)) {
            return mapper.toDTO(ct);
        }
        return null;
    }
}
