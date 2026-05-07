package com.trainstation.service.impl;

import com.trainstation.dto.CreateTaiKhoanRequest;
import com.trainstation.dto.TaiKhoanDTO;
import com.trainstation.dto.UpdateTaiKhoanRequest;
import com.trainstation.mapper.JacksonTaiKhoanMapper;
import com.trainstation.mapper.TaiKhoanMapper;
import com.trainstation.model.TaiKhoan;
import com.trainstation.repository.ITaiKhoanRepository;
import com.trainstation.repository.impl.TaiKhoanRepositoryImpl;
import com.trainstation.service.iface.ITaiKhoanService;
import com.trainstation.util.PasswordUtil;

import java.util.List;

public class TaiKhoanServiceImpl implements ITaiKhoanService {
    private static TaiKhoanServiceImpl instance;
    private final ITaiKhoanRepository taiKhoanRepository;
    private final TaiKhoanMapper mapper;

    private TaiKhoanServiceImpl() {
        this.taiKhoanRepository = TaiKhoanRepositoryImpl.getInstance();
        this.mapper = JacksonTaiKhoanMapper.getInstance();
    }

    public static synchronized TaiKhoanServiceImpl getInstance() {
        if (instance == null) {
            instance = new TaiKhoanServiceImpl();
        }
        return instance;
    }

    @Override
    public TaiKhoan xacThuc(String tenTaiKhoan, String matKhau) {
        TaiKhoan tk = taiKhoanRepository.findByTenTaiKhoan(tenTaiKhoan);
        boolean matched = tk != null && PasswordUtil.verifyPassword(matKhau, tk.getMatKhau());
        return matched ? tk : null;
    }

    @Override
    public String taoMaTaiKhoan() {
        return taiKhoanRepository.generateNextMaTK();
    }

    @Override
    public List<TaiKhoan> layTatCaTaiKhoan() {
        return taiKhoanRepository.getAll();
    }

    @Override
    public TaiKhoan timTaiKhoanTheoMa(String maTK) {
        return taiKhoanRepository.findById(maTK);
    }

    @Override
    public boolean themTaiKhoan(TaiKhoan tk) {
        if (tk != null) {
            tk.setMatKhau(PasswordUtil.hashIfNeeded(tk.getMatKhau()));
        }
        return taiKhoanRepository.insert(tk);
    }

    @Override
    public boolean capNhatTaiKhoan(TaiKhoan tk) {
        if (tk != null) {
            tk.setMatKhau(PasswordUtil.hashIfNeeded(tk.getMatKhau()));
        }
        return taiKhoanRepository.update(tk);
    }

    @Override
    public boolean doiMatKhau(String maTK, String matKhauMoi) {
        TaiKhoan tk = taiKhoanRepository.findById(maTK);
        if (tk == null) return false;
        tk.setMatKhau(PasswordUtil.hashIfNeeded(matKhauMoi));
        return taiKhoanRepository.update(tk);
    }

    @Override
    public boolean xoaTaiKhoan(String maTK) {
        return taiKhoanRepository.delete(maTK);
    }

    @Override
    public TaiKhoanDTO taoTaiKhoanTuRequest(CreateTaiKhoanRequest request) {
        String maTK = taoMaTaiKhoan();
        TaiKhoan tk = mapper.fromCreateRequest(request, maTK);
        tk.setMatKhau(PasswordUtil.hashIfNeeded(tk.getMatKhau()));
        if (taiKhoanRepository.insert(tk)) {
            return mapper.toDTO(tk);
        }
        return null;
    }

    @Override
    public TaiKhoanDTO capNhatTaiKhoanTuRequest(UpdateTaiKhoanRequest request) {
        TaiKhoan tk = taiKhoanRepository.findById(request.getMaTK());
        if (tk == null) return null;
        tk.setMaNV(request.getMaNV());
        tk.setTenTaiKhoan(request.getTenTaiKhoan());
        tk.setMatKhau(PasswordUtil.hashIfNeeded(request.getMatKhau()));
        tk.setTrangThai(request.getTrangThai());
        if (taiKhoanRepository.update(tk)) {
            return mapper.toDTO(tk);
        }
        return null;
    }
}
