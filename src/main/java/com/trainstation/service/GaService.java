package com.trainstation.service;

import com.trainstation.dao.GaDAO;
import com.trainstation.model.Ga;
import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Ga (Station)
 */
public class GaService {
    private static GaService instance;
    private final GaDAO gaDAO;

    private GaService() {
        this.gaDAO = GaDAO.getInstance();
    }

    public static synchronized GaService getInstance() {
        if (instance == null) {
            instance = new GaService();
        }
        return instance;
    }

    public List<Ga> layTatCaGa() {
        return gaDAO.getAll();
    }

    public Ga timGaTheoMa(String maGa) {
        return gaDAO.findById(maGa);
    }

    public String taoMaGa() {
        List<Ga> danhSach = gaDAO.getAll();
        int maxId = 0;
        for (Ga ga : danhSach) {
            String maGa = ga.getMaGa();
            if (maGa != null && maGa.startsWith("GA")) {
                try {
                    int id = Integer.parseInt(maGa.substring(2));
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs
                }
            }
        }
        return String.format("GA%03d", maxId + 1);
    }

    public boolean themGa(Ga ga) {
        return gaDAO.insert(ga);
    }

    public boolean capNhatGa(Ga ga) {
        return gaDAO.update(ga);
    }

    public boolean xoaGa(String maGa) {
        return gaDAO.delete(maGa);
    }
}
