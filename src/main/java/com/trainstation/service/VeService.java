package com.trainstation.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.trainstation.MySQL.ConnectSql;
import com.trainstation.dao.VeDAO;
import com.trainstation.dao.GheDAO;
import com.trainstation.dao.ChuyenTauDAO;
import com.trainstation.dao.BangGiaDAO;
import com.trainstation.dao.ChiTietHoaDonDAO;
import com.trainstation.dao.HoaDonDAO;
import com.trainstation.dao.LichSuDoiVeDAO;
import com.trainstation.model.Ve;
import com.trainstation.model.Ghe;
import com.trainstation.model.ChuyenTau;
import com.trainstation.model.BangGia;
import com.trainstation.model.ChiTietHoaDon;
import com.trainstation.model.HoaDon;
import com.trainstation.model.LichSuDoiVe;
import com.trainstation.dto.DoiVeRequest;
import com.trainstation.dto.DoiVeResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ liên quan đến Vé
 */
public class VeService {
    private static VeService instance;
    private final VeDAO veDAO;
    private final GheDAO gheDAO;
    private final ChuyenTauDAO chuyenTauDAO;
    private final BangGiaDAO bangGiaDAO;
    private final ChiTietHoaDonDAO chiTietHoaDonDAO;
    private final HoaDonDAO hoaDonDAO;
    private final LichSuDoiVeDAO lichSuDoiVeDAO;
    private final TinhGiaService tinhGiaService;

    // Cấu hình thời gian tối thiểu trước giờ đi để được đổi vé (giờ)
    private static final int HOURS_BEFORE_DEPARTURE_TO_EXCHANGE = 2;

    private VeService() {
        this.veDAO = VeDAO.getInstance();
        this.gheDAO = GheDAO.getInstance();
        this.chuyenTauDAO = ChuyenTauDAO.getInstance();
        this.bangGiaDAO = BangGiaDAO.getInstance();
        this.chiTietHoaDonDAO = ChiTietHoaDonDAO.getInstance();
        this.hoaDonDAO = HoaDonDAO.getInstance();
        this.lichSuDoiVeDAO = LichSuDoiVeDAO.getInstance();
        this.tinhGiaService = TinhGiaService.getInstance();
    }

    public static synchronized VeService getInstance() {
        if (instance == null) {
            instance = new VeService();
        }
        return instance;
    }
    
    public Ve taoVe(Ve ve) {
        if (veDAO.insert(ve)) {
            return ve;
        }
        throw new RuntimeException("Không thể tạo vé");
    }
    
    public boolean capNhatVe(Ve ve) {
        return veDAO.update(ve);
    }
    
    public boolean huyVe(String maVe) {
        Ve ve = veDAO.findById(maVe);
        if (ve == null) {
            throw new IllegalArgumentException("Không tìm thấy vé");
        }

        if (!"Đã thanh toán".equals(ve.getTrangThai())) {
            throw new IllegalStateException("Chỉ có thể hủy vé đã thanh toán");
        }

        ve.setTrangThai("Đã hủy");
        boolean result = veDAO.update(ve);

        // Cập nhật trạng thái ghế
        if (result && ve.getMaSoGhe() != null) {
            Ghe ghe = gheDAO.findById(ve.getMaSoGhe());
            if (ghe != null) {
                ghe.setTrangThai("Trống");
                gheDAO.update(ghe);
            }
        }

        return result;
    }
    
    public boolean hoanVe(String maVe) {
        Ve ve = veDAO.findById(maVe);
        if (ve == null) {
            throw new IllegalArgumentException("Không tìm thấy vé");
        }

        if (!"Đã thanh toán".equals(ve.getTrangThai())) {
            throw new IllegalStateException("Chỉ có thể hoàn vé đã thanh toán");
        }

        ve.setTrangThai("Đã hoàn");
        boolean result = veDAO.update(ve);

        // Cập nhật trạng thái ghế
        if (result && ve.getMaSoGhe() != null) {
            Ghe ghe = gheDAO.findById(ve.getMaSoGhe());
            if (ghe != null) {
                ghe.setTrangThai("Trống");
                gheDAO.update(ghe);
            }
        }

        return result;
    }

    /**
     * Đổi vé (phát triển trong tương lai)
     */
    public boolean doiVe(String maVeCu, Ve veMoi) {
        Ve veCu = veDAO.findById(maVeCu);
        if (veCu == null) {
            throw new IllegalArgumentException("Không tìm thấy vé cũ");
        }

        if (!"Đã thanh toán".equals(veCu.getTrangThai())) {
            throw new IllegalStateException("Chỉ có thể đổi vé đã thanh toán");
        }

        // Giải phóng ghế cũ
        if (veCu.getMaSoGhe() != null) {
            Ghe gheCu = gheDAO.findById(veCu.getMaSoGhe());
            if (gheCu != null) {
                gheCu.setTrangThai("Trống");
                gheDAO.update(gheCu);
            }
        }

        // Cập nhật ghế mới
        if (veMoi.getMaSoGhe() != null) {
            Ghe gheMoi = gheDAO.findById(veMoi.getMaSoGhe());
            if (gheMoi != null) {
                if (!"Trống".equals(gheMoi.getTrangThai())) {
                    throw new IllegalStateException("Ghế mới đã được đặt");
                }
                gheMoi.setTrangThai("Bận");
                gheDAO.update(gheMoi);
            }
        }

        // Cập nhật thông tin vé
        veCu.setMaChuyen(veMoi.getMaChuyen());
        veCu.setMaSoGhe(veMoi.getMaSoGhe());
        veCu.setGaDi(veMoi.getGaDi());
        veCu.setGaDen(veMoi.getGaDen());
        veCu.setGioDi(veMoi.getGioDi());
        veCu.setSoToa(veMoi.getSoToa());
        veCu.setLoaiCho(veMoi.getLoaiCho());
        veCu.setMaBangGia(veMoi.getMaBangGia());

        return veDAO.update(veCu);
    }
    
    public List<Ve> layTatCaVe() {
        return veDAO.getAll();
    }
    
    public Ve timVeTheoMa(String maVe) {
        return veDAO.findById(maVe);
    }

    /**
     * Lấy danh sách vé theo chuyến tàu
     */
    public List<Ve> layVeTheoChuyenTau(String maChuyen) {
        return veDAO.getAll().stream()
                .filter(v -> v.getMaChuyen() != null && v.getMaChuyen().equals(maChuyen))
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách vé theo trạng thái
     */
    public List<Ve> layVeTheoTrangThai(String trangThai) {
        return veDAO.getAll().stream()
                .filter(v -> v.getTrangThai() != null && v.getTrangThai().equals(trangThai))
                .collect(Collectors.toList());
    }

    /**
     * Đếm số vé theo trạng thái
     */
    public int demVeTheoTrangThai(String trangThai) {
        return layVeTheoTrangThai(trangThai).size();
    }
    
    public boolean xoaVe(String maVe) {
        return veDAO.delete(maVe);
    }
    
    public List<Ve> layVeTheoKhachHang(String maKH) {
        return veDAO.getByKhachHang(maKH);
    }

    /**
     * Gửi yêu cầu hoàn vé (chuyển trạng thái thành 'Chờ duyệt')
     */
    public boolean guiYeuCauHoanVe(String maVe) {
        Ve ve = veDAO.findById(maVe);
        if (ve == null) {
            throw new IllegalArgumentException("Không tìm thấy vé");
        }

        if (!"Đã thanh toán".equals(ve.getTrangThai()) && !"Đã đặt".equals(ve.getTrangThai())) {
            throw new IllegalStateException("Chỉ có thể hoàn vé đã đặt hoặc đã thanh toán");
        }

        ve.setTrangThai("Chờ duyệt");
        return veDAO.update(ve);
    }

    /**
     * Duyệt yêu cầu hoàn vé
     */
    public boolean duyetHoanVe(String maVe, boolean chapNhan) {
        Ve ve = veDAO.findById(maVe);
        if (ve == null) {
            throw new IllegalArgumentException("Không tìm thấy vé");
        }

        if (!"Chờ duyệt".equals(ve.getTrangThai())) {
            throw new IllegalStateException("Vé không trong trạng thái chờ duyệt");
        }

        if (chapNhan) {
            // Chấp nhận hoàn vé
            ve.setTrangThai("Đã hoàn");
            boolean result = veDAO.update(ve);

            // Cập nhật trạng thái ghế
            if (result && ve.getMaSoGhe() != null) {
                Ghe ghe = gheDAO.findById(ve.getMaSoGhe());
                if (ghe != null) {
                    ghe.setTrangThai("Trống");
                    gheDAO.update(ghe);
                }
            }
            return result;
        } else {
            // Từ chối hoàn vé
            ve.setTrangThai("Đã thanh toán");
            return veDAO.update(ve);
        }
    }

    /**
     * In vé ra file PDF với font tiếng Việt
     */
    public String inVePDF(Ve ve) throws FileNotFoundException, IOException {
        if (ve == null) {
            throw new IllegalArgumentException("Vé không hợp lệ");
        }

        // Tạo folder tickets
        File ticketsDir = new File("tickets");
        if (!ticketsDir.exists()) {
            ticketsDir.mkdirs();
        }

        String fileName = "tickets/Ve_" + ve.getMaVe() + ".pdf";
        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        
        // Chỉnh kích cỡ trang thành A5
        Document document = new Document(pdf, PageSize.A5);

        try {
            // Font Tiếng Việt
            PdfFont font = PdfFontFactory.createFont("fonts/Tinos-Regular.ttf", PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            document.setFont(font);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

            // Header
            Paragraph header = new Paragraph("CÔNG TY CỔ PHẦN VẬN TẢI ĐƯỜNG SẮT SÀI GÒN")
                    .setFont(font)
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(header);

            // Subtitle
            Paragraph subHeader = new Paragraph("THẺ LÊN TÀU HỎA / BOARDING PASS")
                    .setFont(font)
                    .setFontSize(12)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(subHeader);

            document.add(new Paragraph("\n"));

            // Mã vạch
            Paragraph qrTitle = new Paragraph("MÃ QUÉT")
                    .setFont(font)
                    .setFontSize(13)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(qrTitle);

            Paragraph ticketId = new Paragraph("Mã vé: " + ve.getMaVe())
                    .setFont(font)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(ticketId);

            document.add(new Paragraph("\n"));

            // Bảng ga đi ga đến
            Table gaTable = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();

            gaTable.addCell(new Cell()
                    .add(new Paragraph("Ga đi").setFont(font).setBold())
                    .setTextAlignment(TextAlignment.CENTER));
            gaTable.addCell(new Cell()
                    .add(new Paragraph("Ga đến").setFont(font).setBold())
                    .setTextAlignment(TextAlignment.CENTER));

            gaTable.addCell(new Cell()
                    .add(new Paragraph(ve.getGaDi() != null ? ve.getGaDi() : "N/A").setFont(font))
                    .setTextAlignment(TextAlignment.CENTER));
            gaTable.addCell(new Cell()
                    .add(new Paragraph(ve.getGaDen() != null ? ve.getGaDen() : "N/A").setFont(font))
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(gaTable);
            document.add(new Paragraph("\n"));

            // Bảng thông tin chi tiết
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{2, 3})).useAllAvailableWidth();

            // Mã chuyến tàu
            infoTable.addCell(new Cell()
                    .add(new Paragraph("Tàu/Train:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getMaChuyen() != null ? ve.getMaChuyen() : "N/A").setFont(font)));

            // Ngày đi
            infoTable.addCell(new Cell()
                    .add(new Paragraph("Ngày đi/Date:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getGioDi() != null ? ve.getGioDi().format(dateFormatter) : "N/A").setFont(font)));

            // Giờ đi
            infoTable.addCell(new Cell()
                    .add(new Paragraph("Giờ đi/Time:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getGioDi() != null ? ve.getGioDi().format(timeFormatter) : "N/A").setFont(font)));

            // Toa
            infoTable.addCell(new Cell()
                    .add(new Paragraph("Toa/Coach:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getSoToa() != null ? ve.getSoToa() : "N/A").setFont(font)));

            // Ghế
            infoTable.addCell(new Cell()
                    .add(new Paragraph("Chỗ/Seat:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getMaSoGhe() != null ? ve.getMaSoGhe() : "N/A").setFont(font)));

            // Loại ghế
            infoTable.addCell(new Cell()
                    .add(new Paragraph("Loại chỗ/Class:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getLoaiCho() != null ? ve.getLoaiCho() : "N/A").setFont(font)));

            // Loại vé
            infoTable.addCell(new Cell()
                    .add(new Paragraph("Loại vé/Type:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getLoaiVe() != null ? ve.getLoaiVe() : "N/A").setFont(font)));

            // Giá cơ bản
            String priceStr = "N/A";
            try {
                float priceToShow = ve.getDisplayPrice();
                priceStr = String.format("%,.0f VNĐ", priceToShow);
            } catch (Exception e) {
                priceStr = "N/A";
            }
            infoTable.addCell(new Cell()
                    .add(new Paragraph("Giá/Price:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(priceStr).setFont(font)));

            document.add(infoTable);

            // Footer
            document.add(new Paragraph("\nCảm ơn quý khách đã sử dụng dịch vụ!")
                    .setFont(font)
                    .setItalic()
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));

        } finally {
            document.close();
        }

        return fileName;
    }

    /**
     * Kiểm tra xem vé có được phép đổi không
     */
    public DoiVeResult validateDoiVe(String maVe) {
        Ve ve = veDAO.findById(maVe);
        if (ve == null) {
            return new DoiVeResult(false, "Không tìm thấy vé");
        }

        // Kiểm tra trạng thái vé
        if (!"Đã thanh toán".equals(ve.getTrangThai()) && !"Đã đặt".equals(ve.getTrangThai())) {
            return new DoiVeResult(false, "Chỉ có thể đổi vé đã đặt hoặc đã thanh toán. Trạng thái hiện tại: " + ve.getTrangThai());
        }

        // Kiểm tra thời hạn đổi vé
        if (ve.getGioDi() != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime cutoffTime = ve.getGioDi().minusHours(HOURS_BEFORE_DEPARTURE_TO_EXCHANGE);
            if (now.isAfter(cutoffTime)) {
                return new DoiVeResult(false, "Đã quá thời hạn đổi vé. Phải đổi trước " + HOURS_BEFORE_DEPARTURE_TO_EXCHANGE + " giờ so với giờ đi");
            }
        }

        return new DoiVeResult(true, "Vé hợp lệ để đổi");
    }

    /**
     * Yêu cầu đổi vé - phương thức chính để xử lý đổi vé
     */
    public DoiVeResult yeuCauDoiVe(DoiVeRequest request) {
        if (request == null || request.getMaVeCu() == null || request.getMaGheMoi() == null) {
            return new DoiVeResult(false, "Thông tin yêu cầu không đầy đủ");
        }

        // Validate vé cũ
        DoiVeResult validation = validateDoiVe(request.getMaVeCu());
        if (!validation.isThanhCong()) {
            return validation;
        }

        Ve veCu = veDAO.findById(request.getMaVeCu());
        if (veCu == null) {
            return new DoiVeResult(false, "Không tìm thấy vé cũ");
        }

        // Kiểm tra ghế mới
        Ghe gheMoi = gheDAO.findById(request.getMaGheMoi());
        if (gheMoi == null) {
            return new DoiVeResult(false, "Không tìm thấy ghế mới");
        }

        if (!"Trống".equals(gheMoi.getTrangThai()) && !"Rảnh".equals(gheMoi.getTrangThai())) {
            return new DoiVeResult(false, "Ghế đã được đặt");
        }

        // Kiểm tra chuyến tàu mới
        ChuyenTau chuyenMoi = null;
        if (request.getMaChuyenMoi() != null) {
            chuyenMoi = chuyenTauDAO.findById(request.getMaChuyenMoi());
            if (chuyenMoi == null) {
                return new DoiVeResult(false, "Không tìm thấy chuyến tàu mới");
            }
        }

        // Thực hiện đổi vé trong transaction
        return thucHienDoiVe(veCu, request, chuyenMoi, gheMoi);
    }

    /**
     * Thực hiện đổi vé (atomic transaction)
     */
    private DoiVeResult thucHienDoiVe(Ve veCu, DoiVeRequest request, ChuyenTau chuyenMoi, Ghe gheMoi) {
        Connection conn = null;
        boolean originalAutoCommit = true;
        
        try {
            conn = ConnectSql.getInstance().getConnection();
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            // 1. Khóa ghế mới để kiểm tra và đảm bảo tính nhất quán
            Ghe gheMoiLocked = gheDAO.findByIdForUpdate(request.getMaGheMoi(), conn);
            if (gheMoiLocked == null || (!"Trống".equals(gheMoiLocked.getTrangThai()) && !"Rảnh".equals(gheMoiLocked.getTrangThai()))) {
                conn.rollback();
                return new DoiVeResult(false, "Ghế mới đã được đặt bởi người khác");
            }

            // 2. Lưu thông tin vé cũ để audit
            String chiTietCu = String.format("Chuyến: %s, Ghế: %s, Ga: %s -> %s, Giờ: %s", 
                veCu.getMaChuyen(), veCu.getMaSoGhe(), veCu.getGaDi(), veCu.getGaDen(), 
                veCu.getGioDi() != null ? veCu.getGioDi().toString() : "N/A");

            // 3. Giải phóng ghế cũ
            if (veCu.getMaSoGhe() != null) {
                gheDAO.setTrangThai(veCu.getMaSoGhe(), "Trống", conn);
            }

            // 4. Cập nhật thông tin vé
            Ve veMoi = new Ve();
            veMoi.setMaVe(veCu.getMaVe());
            veMoi.setMaChuyen(request.getMaChuyenMoi() != null ? request.getMaChuyenMoi() : veCu.getMaChuyen());
            veMoi.setMaSoGhe(request.getMaGheMoi());
            veMoi.setMaLoaiVe(request.getMaLoaiVeMoi() != null ? request.getMaLoaiVeMoi() : veCu.getMaLoaiVe());
            veMoi.setNgayIn(veCu.getNgayIn());
            veMoi.setTrangThai(veCu.getTrangThai());
            
            if (chuyenMoi != null) {
                veMoi.setGaDi(chuyenMoi.getGaDi());
                veMoi.setGaDen(chuyenMoi.getGaDen());
                veMoi.setGioDi(chuyenMoi.getGioDi());
            } else {
                veMoi.setGaDi(veCu.getGaDi());
                veMoi.setGaDen(veCu.getGaDen());
                veMoi.setGioDi(veCu.getGioDi());
            }
            
            veMoi.setSoToa(request.getMaToaMoi() != null ? request.getMaToaMoi() : veCu.getSoToa());
            veMoi.setLoaiCho(gheMoi.getLoaiGhe() != null ? gheMoi.getLoaiGhe() : veCu.getLoaiCho());
            veMoi.setLoaiVe(veCu.getLoaiVe());
            veMoi.setMaBangGia(veCu.getMaBangGia());

            // 5. Tính giá vé mới
            TinhGiaService.KetQuaGia giaVeMoi = tinhGiaService.tinhGiaChoVe(veMoi);
            TinhGiaService.KetQuaGia giaVeCu = tinhGiaService.tinhGiaChoVe(veCu);
            
            float chenhLechGia = giaVeMoi.giaDaKM - giaVeCu.giaDaKM;

            // 6. Cập nhật vé
            if (!veDAO.update(veMoi)) {
                conn.rollback();
                return new DoiVeResult(false, "Không thể cập nhật thông tin vé");
            }

            // 7. Đặt ghế mới
            gheDAO.setTrangThai(request.getMaGheMoi(), "Đã đặt", conn);

            // 8. Cập nhật ChiTietHoaDon nếu có
            ChiTietHoaDon chiTiet = chiTietHoaDonDAO.findByMaVe(veCu.getMaVe());
            if (chiTiet != null) {
                chiTietHoaDonDAO.updateDonGia(chiTiet.getMaHoaDon(), veCu.getMaVe(), 
                    giaVeMoi.giaGoc, giaVeMoi.giaDaKM, conn);
            }

            // 9. Lưu lịch sử đổi vé
            String chiTietMoi = String.format("Chuyến: %s, Ghế: %s, Ga: %s -> %s, Giờ: %s", 
                veMoi.getMaChuyen(), veMoi.getMaSoGhe(), veMoi.getGaDi(), veMoi.getGaDen(), 
                veMoi.getGioDi() != null ? veMoi.getGioDi().toString() : "N/A");
            
            LichSuDoiVe lichSu = new LichSuDoiVe();
            lichSu.setMaLichSu(lichSuDoiVeDAO.generateMaLichSu());
            lichSu.setMaVe(veCu.getMaVe());
            lichSu.setMaNV(request.getMaNV());
            lichSu.setThoiGian(LocalDateTime.now());
            lichSu.setChiTietCu(chiTietCu);
            lichSu.setChiTietMoi(chiTietMoi);
            lichSu.setLyDo(request.getLyDo());
            lichSu.setTrangThai("Đã duyệt");
            lichSu.setChenhLechGia(chenhLechGia);

            if (!lichSuDoiVeDAO.insert(lichSu, conn)) {
                conn.rollback();
                return new DoiVeResult(false, "Không thể lưu lịch sử đổi vé");
            }

            // 10. Commit transaction
            conn.commit();

            // Tạo kết quả
            DoiVeResult result = new DoiVeResult(true, "Đổi vé thành công");
            result.setVeCu(veCu);
            result.setVeMoi(veMoi);
            result.setChenhLechGia(chenhLechGia);
            result.setCanThanhToan(chenhLechGia > 0);
            result.setCanHoanTien(chenhLechGia < 0);
            result.setMaLichSu(lichSu.getMaLichSu());

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return new DoiVeResult(false, "Lỗi khi đổi vé: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(originalAutoCommit);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    /**
     * Phê duyệt yêu cầu đổi vé (cho trường hợp cần quản lý duyệt)
     */
    public boolean approveDoiVe(String maLichSu, String maNV, boolean chapNhan) {
        LichSuDoiVe lichSu = lichSuDoiVeDAO.findById(maLichSu);
        if (lichSu == null) {
            return false;
        }

        if (!"Chờ duyệt".equals(lichSu.getTrangThai())) {
            return false;
        }

        if (chapNhan) {
            lichSu.setTrangThai("Đã duyệt");
        } else {
            lichSu.setTrangThai("Từ chối");
        }

        return lichSuDoiVeDAO.update(lichSu);
    }

    /**
     * Lấy danh sách yêu cầu đổi vé chờ duyệt
     */
    public List<LichSuDoiVe> layDanhSachChoDuyet() {
        return lichSuDoiVeDAO.findByTrangThai("Chờ duyệt");
    }

    /**
     * Lấy lịch sử đổi vé theo mã vé
     */
    public List<LichSuDoiVe> layLichSuDoiVe(String maVe) {
        return lichSuDoiVeDAO.findByMaVe(maVe);
    }

}
