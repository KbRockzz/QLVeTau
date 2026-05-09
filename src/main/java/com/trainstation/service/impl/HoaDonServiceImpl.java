package com.trainstation.service.impl;

import com.trainstation.dto.CreateHoaDonRequest;
import com.trainstation.dto.HoaDonDTO;
import com.trainstation.dto.UpdateHoaDonRequest;
import com.trainstation.mapper.HoaDonMapper;
import com.trainstation.mapper.JacksonHoaDonMapper;
import com.trainstation.model.HoaDon;
import com.trainstation.repository.IHoaDonRepository;
import com.trainstation.repository.impl.HoaDonRepositoryImpl;
import com.trainstation.service.iface.IHoaDonService;

import java.util.List;

public class HoaDonServiceImpl implements IHoaDonService {
    private static HoaDonServiceImpl instance;
    private final IHoaDonRepository hoaDonRepository;
    private final HoaDonMapper mapper;

    private HoaDonServiceImpl() {
        this.hoaDonRepository = HoaDonRepositoryImpl.getInstance();
        this.mapper = JacksonHoaDonMapper.getInstance();
    }

    public static synchronized HoaDonServiceImpl getInstance() {
        if (instance == null) {
            instance = new HoaDonServiceImpl();
        }
        return instance;
    }

    @Override
    public HoaDon taoHoaDon(HoaDon hoaDon) {
        return hoaDonRepository.insert(hoaDon) ? hoaDon : null;
    }

    @Override
    public boolean capNhatHoaDon(HoaDon hoaDon) {
        return hoaDonRepository.update(hoaDon);
    }

    @Override
    public HoaDon timHoaDonTheoMa(String maHoaDon) {
        return hoaDonRepository.findById(maHoaDon);
    }

    @Override
    public List<HoaDon> layTatCaHoaDon() {
        return hoaDonRepository.getAll();
    }

    @Override
    public boolean xoaHoaDon(String maHoaDon) {
        return hoaDonRepository.delete(maHoaDon);
    }

    @Override
    public HoaDonDTO taoHoaDonTuRequest(CreateHoaDonRequest request) {
        HoaDon hoaDon = mapper.fromCreateRequest(request, generateHoaDonId());
        return hoaDonRepository.insert(hoaDon) ? mapper.toDTO(hoaDon) : null;
    }

    @Override
    public HoaDonDTO capNhatHoaDonTuRequest(UpdateHoaDonRequest request) {
        HoaDon hoaDon = hoaDonRepository.findById(request.getMaHoaDon());
        if (hoaDon == null) return null;
        hoaDon.setMaNV(request.getMaNV());
        hoaDon.setMaKH(request.getMaKH());
        hoaDon.setTenKH(request.getTenKH());
        hoaDon.setSoDienThoai(request.getSoDienThoai());
        hoaDon.setNgayLap(request.getNgayLap());
        hoaDon.setPhuongThucThanhToan(request.getPhuongThucThanhToan());
        hoaDon.setTrangThai(request.getTrangThai());
        if (hoaDonRepository.update(hoaDon)) {
            return mapper.toDTO(hoaDon);
        }
        return null;
    }

    private String generateHoaDonId() {
        List<HoaDon> all = hoaDonRepository.getAll();
        int maxId = all.stream()
                .filter(hd -> hd.getMaHoaDon() != null && hd.getMaHoaDon().startsWith("HD"))
                .mapToInt(hd -> {
                    try {
                        return Integer.parseInt(hd.getMaHoaDon().substring(2));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }).max().orElse(0);
        return String.format("HD%03d", maxId + 1);
    }
}
