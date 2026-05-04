package com.trainstation.service.impl;

import com.trainstation.dao.KhachHangDAO;
import com.trainstation.dto.CreateKhachHangRequest;
import com.trainstation.dto.KhachHangDTO;
import com.trainstation.dto.UpdateKhachHangRequest;
import com.trainstation.mapper.JacksonKhachHangMapper;
import com.trainstation.mapper.KhachHangMapper;
import com.trainstation.model.KhachHang;
import com.trainstation.service.iface.IKhachHangService;

import java.util.List;

public class KhachHangServiceImpl implements IKhachHangService {
    private static KhachHangServiceImpl instance;
    private final KhachHangDAO khachHangDAO;
    private final KhachHangMapper mapper;

    private KhachHangServiceImpl() {
        this.khachHangDAO = KhachHangDAO.getInstance();
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
        return khachHangDAO.getAll();
    }

    @Override
    public KhachHang timKhachHangTheoMa(String maKH) {
        return khachHangDAO.findById(maKH);
    }

    @Override
    public KhachHang timKhachHangTheoSoDienThoai(String soDienThoai) {
        return khachHangDAO.timTheoSoDienThoai(soDienThoai);
    }

    @Override
    public boolean themKhachHang(KhachHang kh) {
        return khachHangDAO.insert(kh);
    }

    @Override
    public boolean capNhatKhachHang(KhachHang kh) {
        return khachHangDAO.update(kh);
    }

    @Override
    public boolean xoaKhachHang(String maKH) {
        return khachHangDAO.delete(maKH);
    }

    @Override
    public String taoMaKhachHang() {
        List<KhachHang> danhSach = khachHangDAO.getAll();
        int maxId = 0;
        for (KhachHang kh : danhSach) {
            String maKH = kh.getMaKhachHang();
            if (maKH != null && maKH.startsWith("KH")) {
                try {
                    int id = Integer.parseInt(maKH.substring(2));
                    if (id > maxId) maxId = id;
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("KH%02d", maxId + 1);
    }

    @Override
    public KhachHangDTO taoKhachHangTuRequest(CreateKhachHangRequest request) {
        String maKH = taoMaKhachHang();
        KhachHang kh = mapper.fromCreateRequest(request, maKH);
        if (khachHangDAO.insert(kh)) {
            return mapper.toDTO(kh);
        }
        return null;
    }

    @Override
    public KhachHangDTO capNhatKhachHangTuRequest(UpdateKhachHangRequest request) {
        KhachHang kh = khachHangDAO.findById(request.getMaKhachHang());
        if (kh == null) return null;
        kh.setTenKhachHang(request.getTenKhachHang());
        kh.setEmail(request.getEmail());
        kh.setSoDienThoai(request.getSoDienThoai());
        if (khachHangDAO.update(kh)) {
            return mapper.toDTO(kh);
        }
        return null;
    }
}
