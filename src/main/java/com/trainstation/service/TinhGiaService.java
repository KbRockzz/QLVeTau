package com.trainstation.service;

import com.trainstation.dao.BangGiaDAO;
import com.trainstation.dao.LoaiVeDAO;
import com.trainstation.model.BangGia;
import com.trainstation.model.LoaiVe;
import com.trainstation.model.Ve;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TinhGiaService {
    private static TinhGiaService instance;
    private final BangGiaDAO bangGiaDAO;
    private final LoaiVeDAO loaiVeDAO;

    private TinhGiaService() {
        this.bangGiaDAO = BangGiaDAO.getInstance();
        this.loaiVeDAO = LoaiVeDAO.getInstance();
    }

    public static synchronized TinhGiaService getInstance() {
        if (instance == null) instance = new TinhGiaService();
        return instance;
    }

    public static class KetQuaGia {
        public final float giaGoc;
        public final float giaDaKM;
        public final String ghiChu;

        public KetQuaGia(float giaGoc, float giaDaKM, String ghiChu) {
            this.giaGoc = giaGoc;
            this.giaDaKM = giaDaKM;
            this.ghiChu = ghiChu;
        }
    }

    /**
     * Tinh gia cho ve:
     *  - giaGoc = giaCoBan (tu BangGia)
     *  - giaDaKM = ROUND(giaCoBan * heSoLoaiVe)
     */
    public KetQuaGia tinhGiaChoVe(Ve ve) {
        try {
            BangGia bangGia = null;
            if (ve.getMaBangGia() != null) {
                bangGia = bangGiaDAO.findById(ve.getMaBangGia());
            }
            if (bangGia == null) {
                // fallback: lay bang gia mac dinh neu co
                bangGia = bangGiaDAO.getAll().get(0); // neu DAO co method nay, neu khong thi thay bang getAll().get(0)
            }

            LoaiVe loaiVe = null;
            if (ve.getMaLoaiVe() != null) {
                loaiVe = loaiVeDAO.findById(ve.getMaLoaiVe());
            }
            if (loaiVe == null) {
                loaiVe = loaiVeDAO.getAll().get(0);
            }

            float giaCoBan = bangGia != null ? bangGia.getGiaCoBan() : 0f;
            float heSoGia = loaiVe != null ? loaiVe.getHeSoGia().floatValue() : 1.0f;


            float giaGoc = lamTronGia(giaCoBan); // luu gia co ban
            float giaDaKM = lamTronGia(giaCoBan * heSoGia); // gia sau nhan he so (gia khuyen mai theo yeu cau)

            String ghiChu = "giaDaKM = giaCoBan * heSoLoaiVe";
            return new KetQuaGia(giaGoc, giaDaKM, ghiChu);
        } catch (Exception ex) {
            return new KetQuaGia(0f, 0f, "Loi khi tinh gia");
        }
    }

    private float lamTronGia(double value) {
        BigDecimal bd = BigDecimal.valueOf(value).setScale(0, RoundingMode.HALF_UP);
        return bd.floatValue();
    }
}