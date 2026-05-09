package com.trainstation.service.impl;

import com.trainstation.dto.CreateKhachHangRequest;
import com.trainstation.dto.KhachHangDTO;
import com.trainstation.dto.UpdateKhachHangRequest;
import com.trainstation.mapper.JacksonKhachHangMapper;
import com.trainstation.mapper.KhachHangMapper;
import com.trainstation.model.KhachHang;
import com.trainstation.repository.IKhachHangRepository;
import com.trainstation.repository.impl.KhachHangRepositoryImpl;
import com.trainstation.service.iface.IKhachHangService;

import java.util.List;

public class KhachHangServiceImpl implements IKhachHangService {
    private static KhachHangServiceImpl instance;
    private final IKhachHangRepository khachHangRepository;
    private final KhachHangMapper mapper;

    private KhachHangServiceImpl() {
        this.khachHangRepository = KhachHangRepositoryImpl.getInstance();
        this.mapper = JacksonKhachHangMapper.getInstance();
    }

    public static synchronized KhachHangServiceImpl getInstance() {
        if (instance == null) {
            instance = new KhachHangServiceImpl();
        }
        return instance;
    }

    @Override
    public List<KhachHang> layTatCaKhachHang() {
        return khachHangRepository.getAll();
    }

    @Override
    public KhachHang timKhachHangTheoMa(String maKH) {
        return khachHangRepository.findById(maKH);
    }

    @Override
    public KhachHang timKhachHangTheoSoDienThoai(String soDienThoai) {
        return khachHangRepository.findBySoDienThoai(soDienThoai);
    }

    @Override
    public boolean themKhachHang(KhachHang kh) {
        return khachHangRepository.insert(kh);
    }

    @Override
    public boolean capNhatKhachHang(KhachHang kh) {
        return khachHangRepository.update(kh);
    }

    @Override
    public boolean xoaKhachHang(String maKH) {
        return khachHangRepository.delete(maKH);
    }

    @Override
    public String taoMaKhachHang() {
        return khachHangRepository.generateNextMaKhachHang();
    }

    @Override
    public KhachHangDTO taoKhachHangTuRequest(CreateKhachHangRequest request) {
        String maKH = taoMaKhachHang();
        KhachHang kh = mapper.fromCreateRequest(request, maKH);
        if (khachHangRepository.insert(kh)) {
            return mapper.toDTO(kh);
        }
        return null;
    }

    @Override
    public KhachHangDTO capNhatKhachHangTuRequest(UpdateKhachHangRequest request) {
        KhachHang kh = khachHangRepository.findById(request.getMaKhachHang());
        if (kh == null) return null;
        kh.setTenKhachHang(request.getTenKhachHang());
        kh.setEmail(request.getEmail());
        kh.setSoDienThoai(request.getSoDienThoai());
        if (khachHangRepository.update(kh)) {
            return mapper.toDTO(kh);
        }
        return null;
    }
}
