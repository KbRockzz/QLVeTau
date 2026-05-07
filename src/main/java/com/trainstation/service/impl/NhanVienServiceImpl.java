package com.trainstation.service.impl;

import com.trainstation.dto.CreateNhanVienRequest;
import com.trainstation.dto.NhanVienDTO;
import com.trainstation.dto.UpdateNhanVienRequest;
import com.trainstation.mapper.JacksonNhanVienMapper;
import com.trainstation.mapper.NhanVienMapper;
import com.trainstation.model.NhanVien;
import com.trainstation.repository.INhanVienRepository;
import com.trainstation.repository.impl.NhanVienRepositoryImpl;
import com.trainstation.service.iface.INhanVienService;

import java.util.List;

public class NhanVienServiceImpl implements INhanVienService {
    private static NhanVienServiceImpl instance;
    private final INhanVienRepository nhanVienRepository;
    private final NhanVienMapper mapper;

    private NhanVienServiceImpl() {
        this.nhanVienRepository = NhanVienRepositoryImpl.getInstance();
        this.mapper = JacksonNhanVienMapper.getInstance();
    }

    public static synchronized NhanVienServiceImpl getInstance() {
        if (instance == null) {
            instance = new NhanVienServiceImpl();
        }
        return instance;
    }

    @Override
    public List<NhanVien> layTatCaNhanVien() {
        return nhanVienRepository.getAll();
    }

    @Override
    public NhanVien timNhanVienTheoMa(String maNV) {
        return nhanVienRepository.findById(maNV);
    }

    @Override
    public boolean themNhanVien(NhanVien nv) {
        return nhanVienRepository.insert(nv);
    }

    @Override
    public boolean capNhatNhanVien(NhanVien nv) {
        return nhanVienRepository.update(nv);
    }

    @Override
    public boolean xoaNhanVien(String maNV) {
        return nhanVienRepository.delete(maNV);
    }

    @Override
    public String taoMaNhanVien() {
        List<NhanVien> danhSach = nhanVienRepository.getAll();
        int maxId = 0;
        for (NhanVien nv : danhSach) {
            String maNV = nv.getMaNV();
            if (maNV != null && maNV.startsWith("NV")) {
                try {
                    int id = Integer.parseInt(maNV.substring(2));
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("NV%02d", maxId + 1);
    }

    @Override
    public NhanVienDTO taoNhanVienTuRequest(CreateNhanVienRequest request) {
        String maNV = taoMaNhanVien();
        NhanVien nv = mapper.fromCreateRequest(request, maNV);
        if (nhanVienRepository.insert(nv)) {
            return mapper.toDTO(nv);
        }
        return null;
    }

    @Override
    public NhanVienDTO capNhatNhanVienTuRequest(UpdateNhanVienRequest request) {
        NhanVien nv = nhanVienRepository.findById(request.getMaNV());
        if (nv == null) return null;
        nv.setTenNV(request.getTenNV());
        nv.setSoDienThoai(request.getSoDienThoai());
        nv.setDiaChi(request.getDiaChi());
        nv.setNgaySinh(request.getNgaySinh());
        nv.setMaLoaiNV(request.getMaLoaiNV());
        nv.setTrangThai(request.getTrangThai());
        if (nhanVienRepository.update(nv)) {
            return mapper.toDTO(nv);
        }
        return null;
    }
}
