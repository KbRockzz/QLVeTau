package com.trainstation.service.impl;

import com.trainstation.dto.BangGiaDTO;
import com.trainstation.dto.CreateBangGiaRequest;
import com.trainstation.dto.UpdateBangGiaRequest;
import com.trainstation.mapper.BangGiaMapper;
import com.trainstation.mapper.JacksonBangGiaMapper;
import com.trainstation.model.BangGia;
import com.trainstation.repository.IBangGiaRepository;
import com.trainstation.repository.impl.BangGiaRepositoryImpl;
import com.trainstation.service.iface.IBangGiaService;

import java.util.List;

public class BangGiaServiceImpl implements IBangGiaService {
    private static BangGiaServiceImpl instance;
    private final IBangGiaRepository bangGiaRepository;
    private final BangGiaMapper mapper;

    private BangGiaServiceImpl() {
        this.bangGiaRepository = BangGiaRepositoryImpl.getInstance();
        this.mapper = JacksonBangGiaMapper.getInstance();
    }

    public static synchronized BangGiaServiceImpl getInstance() {
        if (instance == null) {
            instance = new BangGiaServiceImpl();
        }
        return instance;
    }

    @Override
    public List<BangGia> layTatCaBangGia() {
        return bangGiaRepository.getAll();
    }

    @Override
    public BangGia timBangGiaTheoMa(String maBG) {
        return bangGiaRepository.findById(maBG);
    }

    @Override
    public String taoMaBangGia() {
        return bangGiaRepository.generateNextMaBangGia();
    }

    @Override
    public boolean themBangGia(BangGia bg) {
        return bangGiaRepository.insert(bg);
    }

    @Override
    public boolean capNhatBangGia(BangGia bg) {
        return bangGiaRepository.update(bg);
    }

    @Override
    public boolean xoaBangGia(String maBG) {
        return bangGiaRepository.delete(maBG);
    }

    @Override
    public BangGiaDTO taoBangGiaTuRequest(CreateBangGiaRequest request) {
        BangGia bg = mapper.fromCreateRequest(request, taoMaBangGia());
        if (bangGiaRepository.insert(bg)) {
            return mapper.toDTO(bg);
        }
        return null;
    }

    @Override
    public BangGiaDTO capNhatBangGiaTuRequest(UpdateBangGiaRequest request) {
        BangGia bg = bangGiaRepository.findById(request.getMaBangGia());
        if (bg == null) return null;
        bg.setMaChang(request.getMaChang());
        bg.setLoaiGhe(request.getLoaiGhe());
        bg.setGiaCoBan(request.getGiaCoBan());
        bg.setNgayBatDau(request.getNgayBatDau());
        bg.setNgayKetThuc(request.getNgayKetThuc());
        if (bangGiaRepository.update(bg)) {
            return mapper.toDTO(bg);
        }
        return null;
    }
}
