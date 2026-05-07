package com.trainstation.repository.impl;

import com.trainstation.dao.ChuyenTauDAO;
import com.trainstation.model.ChuyenTau;
import com.trainstation.repository.IChuyenTauRepository;

import java.util.List;
import java.util.stream.Collectors;

public class ChuyenTauRepositoryImpl implements IChuyenTauRepository {
    private static ChuyenTauRepositoryImpl instance;
    private final ChuyenTauDAO chuyenTauDAO;

    private ChuyenTauRepositoryImpl() {
        this.chuyenTauDAO = ChuyenTauDAO.getInstance();
    }

    public static synchronized ChuyenTauRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new ChuyenTauRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<ChuyenTau> getAll() {
        return chuyenTauDAO.getAll();
    }

    @Override
    public ChuyenTau findById(String id) {
        return chuyenTauDAO.findById(id);
    }

    @Override
    public List<ChuyenTau> search(String keyword) {
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
    public boolean insert(ChuyenTau entity) {
        return chuyenTauDAO.insert(entity);
    }

    @Override
    public boolean update(ChuyenTau entity) {
        return chuyenTauDAO.update(entity);
    }

    @Override
    public boolean delete(String id) {
        return chuyenTauDAO.delete(id);
    }
}
