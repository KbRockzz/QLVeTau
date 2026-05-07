package com.trainstation.service.impl;

import com.trainstation.dto.ChuyenTauDTO;
import com.trainstation.dto.CreateChuyenTauRequest;
import com.trainstation.dto.UpdateChuyenTauRequest;
import com.trainstation.mapper.ChuyenTauMapper;
import com.trainstation.mapper.JacksonChuyenTauMapper;
import com.trainstation.model.ChuyenTau;
import com.trainstation.repository.IChuyenTauRepository;
import com.trainstation.repository.impl.ChuyenTauRepositoryImpl;
import com.trainstation.service.iface.IChuyenTauService;

import java.util.List;

public class ChuyenTauServiceImpl implements IChuyenTauService {
    private static ChuyenTauServiceImpl instance;
    private final IChuyenTauRepository chuyenTauRepository;
    private final ChuyenTauMapper mapper;

    private ChuyenTauServiceImpl() {
        this.chuyenTauRepository = ChuyenTauRepositoryImpl.getInstance();
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
        return chuyenTauRepository.getAll();
    }

    @Override
    public ChuyenTau timChuyenTauTheoMa(String maChuyen) {
        return chuyenTauRepository.findById(maChuyen);
    }

    @Override
    public List<ChuyenTau> timKiemChuyenTau(String keyword) {
        return chuyenTauRepository.search(keyword);
    }

    @Override
    public boolean themChuyenTau(ChuyenTau ct) {
        return chuyenTauRepository.insert(ct);
    }

    @Override
    public boolean capNhatChuyenTau(ChuyenTau ct) {
        return chuyenTauRepository.update(ct);
    }

    @Override
    public boolean xoaChuyenTau(String maChuyen) {
        return chuyenTauRepository.delete(maChuyen);
    }

    @Override
    public ChuyenTauDTO taoChuyenTauTuRequest(CreateChuyenTauRequest request) {
        List<ChuyenTau> all = chuyenTauRepository.getAll();
        int maxId = all.stream()
                .filter(ct -> ct.getMaChuyen() != null && ct.getMaChuyen().startsWith("CT"))
                .mapToInt(ct -> {
                    try { return Integer.parseInt(ct.getMaChuyen().substring(2)); }
                    catch (NumberFormatException e) { return 0; }
                }).max().orElse(0);
        String newId = String.format("CT%03d", maxId + 1);
        ChuyenTau ct = mapper.fromCreateRequest(request, newId);
        ct.setTrangThai("Chưa khởi hành");
        if (chuyenTauRepository.insert(ct)) {
            return mapper.toDTO(ct);
        }
        return null;
    }

    @Override
    public ChuyenTauDTO capNhatChuyenTauTuRequest(UpdateChuyenTauRequest request) {
        ChuyenTau ct = chuyenTauRepository.findById(request.getMaChuyen());
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
        if (chuyenTauRepository.update(ct)) {
            return mapper.toDTO(ct);
        }
        return null;
    }
}
