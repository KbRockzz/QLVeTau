package com.trainstation.service;

import com.trainstation.dao.BangGiaDAO;
import com.trainstation.dao.ChuyenTauDAO;
import com.trainstation.dao.LoaiVeDAO;
import com.trainstation.model.BangGia;
import com.trainstation.model.ChuyenTau;
import com.trainstation.model.LoaiVe;
import com.trainstation.model.Ve;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Dựa trên lớp bạn gửi — làm lại cho:
 *  - logic tìm BangGia linh hoạt hơn (dựa trên maBangGia nếu có, nếu không tìm theo maChang + loaiGhe + thời điểm tham chiếu)
 *  - fallback an toàn (nếu không tìm được BangGia thì lấy bản ghi đầu tiên nếu có)
 *  - trả về KetQuaGia có thêm trường maBangGia được áp dụng (khi cần persist vào Ve)
 *  - sử dụng ngayIn của vé làm thời điểm tham chiếu nếu có, ngược lại dùng LocalDateTime.now()
 */
public class TinhGiaService {
    private static TinhGiaService instance;
    private final BangGiaDAO bangGiaDAO;
    private final LoaiVeDAO loaiVeDAO;
    private final ChuyenTauDAO chuyenTauDAO;

    private TinhGiaService() {
        this.bangGiaDAO = BangGiaDAO.getInstance();
        this.loaiVeDAO = LoaiVeDAO.getInstance();
        this.chuyenTauDAO = ChuyenTauDAO.getInstance();
    }

    public static synchronized TinhGiaService getInstance() {
        if (instance == null) instance = new TinhGiaService();
        return instance;
    }

    public static class KetQuaGia {
        public final float giaGoc;
        public final float giaDaKM;
        public final String ghiChu;
        public final String maBangGia;

        public KetQuaGia(float giaGoc, float giaDaKM, String ghiChu, String maBangGia) {
            this.giaGoc = giaGoc;
            this.giaDaKM = giaDaKM;
            this.ghiChu = ghiChu;
            this.maBangGia = maBangGia;
        }
    }

    /**
     * Tính giá cho vé:
     *  - Nếu ve.getMaBangGia() tồn tại sẽ dùng record đó;
     *  - Nếu không, cố tìm BangGia áp dụng theo chặng (maChang) và loại ghế (loaiGheKey) tại thời điểm tham chiếu.
     *    - Thời điểm tham chiếu: nếu ve.getNgayIn() != null thì dùng ngayIn, ngược lại dùng LocalDateTime.now()
     *  - He so tính theo LoaiVe (nếu có maLoaiVe), fallback = 1.0
     *  - Trả về giaGoc (giaCoBan) và giaDaKM = ROUND(giaCoBan * heSoLoaiVe)
     *  - Trả về maBangGia dùng để persist vào Ve nếu cần
     */
    public KetQuaGia tinhGiaChoVe(Ve ve) {
        try {
            LocalDateTime refDate = (ve.getNgayIn() != null) ? ve.getNgayIn() : LocalDateTime.now();

            // Tìm bảng giá
            BangGia bangGia = null;
            String appliedMaBangGia = null;
            if (ve.getMaBangGia() != null && !ve.getMaBangGia().trim().isEmpty()) {
                bangGia = bangGiaDAO.findById(ve.getMaBangGia());
                if (bangGia != null) appliedMaBangGia = bangGia.getMaBangGia();
            }

            // Nếu không tìm thấy, tìm theo chặng và loại ghế
            if (bangGia == null) {
                String maChang = null;
                try {
                    ChuyenTau ct = null;
                    if (ve.getMaChuyen() != null) ct = chuyenTauDAO.findById(ve.getMaChuyen());
                    if (ct != null && ct.getMaChang() != null && !ct.getMaChang().trim().isEmpty()) {
                        maChang = ct.getMaChang();
                    } else {
                        maChang = ve.getMaChuyen();
                    }
                } catch (Throwable ignored) {
                }

                String loaiGheKey = null;
                if (ve.getLoaiCho() != null && !ve.getLoaiCho().trim().isEmpty()) loaiGheKey = ve.getLoaiCho().trim();
                if ((loaiGheKey == null || loaiGheKey.isEmpty()) && ve.getLoaiVe() != null) loaiGheKey = ve.getLoaiVe().trim();

                if (maChang != null && loaiGheKey != null) {
                    bangGia = bangGiaDAO.findApplicable(maChang, loaiGheKey, refDate);
                    if (bangGia != null) appliedMaBangGia = bangGia.getMaBangGia();
                }
            }

            // Fallback: lấy bảng giá đầu tiên
            if (bangGia == null) {
                List<BangGia> all = bangGiaDAO.getAll();
                if (!all.isEmpty()) {
                    bangGia = all.get(0);
                    appliedMaBangGia = bangGia.getMaBangGia();
                }
            }

            float giaCoBan = (bangGia != null) ? bangGia.getGiaCoBan() : 0f;

            // Tìm hệ số loại vé
            float heSoGia = 1.0f;
            try {
                if (ve.getMaLoaiVe() != null && !ve.getMaLoaiVe().trim().isEmpty()) {
                    LoaiVe lv = loaiVeDAO.findById(ve.getMaLoaiVe());
                    if (lv != null && lv.getHeSoGia() != null) heSoGia = lv.getHeSoGia().floatValue();
                } else {
                    Optional<LoaiVe> possible = loaiVeDAO.getAll().stream().findFirst();
                    if (possible.isPresent() && possible.get().getHeSoGia() != null) heSoGia = possible.get().getHeSoGia().floatValue();
                }
            } catch (Throwable ignored) { }

            // Tính kết quả
            float giaGoc = lamTronGia(giaCoBan);
            float giaDaKM = lamTronGia(giaCoBan * heSoGia);

            String ghiChu = String.format("giaDaKM = ROUND(giaCoBan (%.2f) * heSoLoaiVe (%.3f))", giaCoBan, heSoGia);
            return new KetQuaGia(giaGoc, giaDaKM, ghiChu, appliedMaBangGia);
        } catch (Exception ex) {
            return new KetQuaGia(0f, 0f, "Lỗi khi tính giá: " + ex.getMessage(), null);
        }
    }

    private float lamTronGia(double value) {
        BigDecimal bd = BigDecimal.valueOf(value).setScale(0, RoundingMode.HALF_UP);
        return bd.floatValue();
    }
}