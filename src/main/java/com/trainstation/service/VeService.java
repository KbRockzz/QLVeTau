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
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.trainstation.MySQL.ConnectSql;
import com.trainstation.dao.VeDAO;
import com.trainstation.dao.GheDAO;
import com.trainstation.dao.ChuyenTauDAO;
import com.trainstation.dao.BangGiaDAO;
import com.trainstation.dao.ChiTietHoaDonDAO;
import com.trainstation.dao.HoaDonDAO;
import com.trainstation.model.Ve;
import com.trainstation.model.Ghe;
import com.trainstation.model.ChuyenTau;
import com.trainstation.model.BangGia;
import com.trainstation.model.ChiTietHoaDon;
import com.trainstation.model.HoaDon;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

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
    
    private static final int MINIMUM_MINUTES_BEFORE_DEPARTURE = 30;



    private VeService() {
        this.veDAO = VeDAO.getInstance();
        this.gheDAO = GheDAO.getInstance();
        this.chuyenTauDAO = ChuyenTauDAO.getInstance();
        this.bangGiaDAO = BangGiaDAO.getInstance();
        this.chiTietHoaDonDAO = ChiTietHoaDonDAO.getInstance();
        this.hoaDonDAO = HoaDonDAO.getInstance();
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
        veCu.setMaGaDi(veMoi.getMaGaDi());
        veCu.setMaGaDen(veMoi.getMaGaDen());
        veCu.setTenGaDi(veMoi.getTenGaDi());
        veCu.setTenGaDen(veMoi.getTenGaDen());
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

        // Điều kiện 2: Kiểm tra vé chưa bị hoàn hoặc đổi trước đó
        String trangThai = ve.getTrangThai();
        if ("Đã hoàn".equals(trangThai)) {
            throw new IllegalStateException("Vé đã được hoàn trước đó. Không thể hoàn lại.");
        }
        if ("Đã đổi".equals(trangThai)) {
            throw new IllegalStateException("Vé đã được đổi trước đó. Không thể hoàn.");
        }
        
        // Kiểm tra trạng thái hợp lệ để hoàn vé
        if (!"Đã thanh toán".equals(trangThai) && !"Đã đặt".equals(trangThai)) {
            throw new IllegalStateException("Chỉ có thể hoàn vé đã đặt hoặc đã thanh toán");
        }

        // Điều kiện 3: Kiểm tra vé thuộc về hóa đơn hợp lệ
        ChiTietHoaDon chiTiet = chiTietHoaDonDAO.findById(maVe);
        if (chiTiet == null) {
            throw new IllegalStateException("Vé không thuộc về hóa đơn nào. Không thể hoàn.");
        }
        
        String maHoaDon = chiTiet.getMaHoaDon();
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            throw new IllegalStateException("Vé không có mã hóa đơn hợp lệ. Không thể hoàn.");
        }
        
        HoaDon hoaDon = hoaDonDAO.findById(maHoaDon);
        if (hoaDon == null) {
            throw new IllegalStateException("Không tìm thấy hóa đơn liên kết với vé. Không thể hoàn.");
        }
        
        String trangThaiHoaDon = hoaDon.getTrangThai();
        if ("Đã hủy".equals(trangThaiHoaDon) || "Vô hiệu".equals(trangThaiHoaDon)) {
            throw new IllegalStateException("Hóa đơn đã bị hủy hoặc vô hiệu. Không thể hoàn vé.");
        }

        // Điều kiện 1: Kiểm tra chuyến tàu chưa khởi hành
        String maChuyen = ve.getMaChuyen();
        if (maChuyen == null || maChuyen.trim().isEmpty()) {
            throw new IllegalStateException("Vé không có thông tin chuyến tàu. Không thể hoàn.");
        }
        
        ChuyenTau chuyenTau = chuyenTauDAO.findById(maChuyen);
        if (chuyenTau == null) {
            throw new IllegalStateException("Không tìm thấy chuyến tàu liên kết với vé. Không thể hoàn.");
        }
        
        LocalDateTime thoiGianKhoiHanh = chuyenTau.getGioDi();
        if (thoiGianKhoiHanh == null) {
            throw new IllegalStateException("Chuyến tàu không có thông tin giờ khởi hành. Không thể hoàn.");
        }
        
        LocalDateTime thoiGianHienTai = LocalDateTime.now();
        if (thoiGianHienTai.isAfter(thoiGianKhoiHanh) || thoiGianHienTai.isEqual(thoiGianKhoiHanh)) {
            throw new IllegalStateException("Chuyến tàu đã khởi hành. Không thể hoàn vé.");
        }

        // Điều kiện 4: Kiểm tra thời gian tối thiểu trước khi tàu chạy
        LocalDateTime thoiGianToiThieu = thoiGianKhoiHanh.minusMinutes(MINIMUM_MINUTES_BEFORE_DEPARTURE);
        if (thoiGianHienTai.isAfter(thoiGianToiThieu)) {
            throw new IllegalStateException("Chỉ còn dưới " + MINIMUM_MINUTES_BEFORE_DEPARTURE + 
                " phút trước khi tàu chạy. Không thể hoàn vé.");
        }

        // Tất cả điều kiện đều đạt, chuyển trạng thái sang "Chờ duyệt"
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
            PdfFont font = PdfFontFactory.createFont("fonts/Tinos-Regular.ttf", PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            document.setFont(font);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

            Paragraph header = new Paragraph("CÔNG TY CỔ PHẦN VẬN TẢI ĐƯỜNG SẮT SÀI GÒN")
                    .setFont(font)
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(header);

            Paragraph subHeader = new Paragraph("THẺ LÊN TÀU HỎA / BOARDING PASS")
                    .setFont(font)
                    .setFontSize(12)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(subHeader);

            Paragraph qrTitle = new Paragraph("MÃ QUÉT")
                    .setFont(font)
                    .setFontSize(13)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(qrTitle);

            String qrCodePath = "tickets/QrCode_" + ve.getMaVe() + ".png";
            generateQRCodeImage(ve.getMaVe(), 100, 100, qrCodePath);

            com.itextpdf.layout.element.Image qrImage = new com.itextpdf.layout.element.Image(
                    com.itextpdf.io.image.ImageDataFactory.create(qrCodePath));
            qrImage.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(qrImage);

            Paragraph ticketId = new Paragraph("Mã vé: " + ve.getMaVe())
                    .setFont(font)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(ticketId);

            document.add(new Paragraph("\n"));

            Table gaTable = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();

            gaTable.addCell(new Cell()
                    .add(new Paragraph("Ga đi").setFont(font).setBold())
                    .setTextAlignment(TextAlignment.CENTER));
            gaTable.addCell(new Cell()
                    .add(new Paragraph("Ga đến").setFont(font).setBold())
                    .setTextAlignment(TextAlignment.CENTER));

            gaTable.addCell(new Cell()
                    .add(new Paragraph(ve.getTenGaDi() != null ? ve.getTenGaDi() : "N/A").setFont(font))
                    .setTextAlignment(TextAlignment.CENTER));
            gaTable.addCell(new Cell()
                    .add(new Paragraph(ve.getTenGaDen() != null ? ve.getTenGaDen() : "N/A").setFont(font))
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(gaTable);
            document.add(new Paragraph("\n"));

            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{2, 3})).useAllAvailableWidth();

            infoTable.addCell(new Cell()
                    .add(new Paragraph("Tàu/Train:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getMaChuyen() != null ? ve.getMaChuyen() : "N/A").setFont(font)));

            infoTable.addCell(new Cell()
                    .add(new Paragraph("Ngày đi/Date:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getGioDi() != null ? ve.getGioDi().format(dateFormatter) : "N/A").setFont(font)));

            infoTable.addCell(new Cell()
                    .add(new Paragraph("Giờ đi/Time:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getGioDi() != null ? ve.getGioDi().format(timeFormatter) : "N/A").setFont(font)));

            infoTable.addCell(new Cell()
                    .add(new Paragraph("Toa/Coach:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getSoToa() != null ? String.valueOf(ve.getSoToa()) : "N/A").setFont(font)));

            infoTable.addCell(new Cell()
                    .add(new Paragraph("Chỗ/Seat:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getMaSoGhe() != null ? ve.getMaSoGhe() : "N/A").setFont(font)));

            infoTable.addCell(new Cell()
                    .add(new Paragraph("Loại chỗ/Class:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getLoaiCho() != null ? ve.getLoaiCho() : "N/A").setFont(font)));

            infoTable.addCell(new Cell()
                    .add(new Paragraph("Loại vé/Type:").setFont(font).setBold()));
            infoTable.addCell(new Cell()
                    .add(new Paragraph(ve.getLoaiVe() != null ? ve.getLoaiVe() : "N/A").setFont(font)));

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

    private void generateQRCodeImage(String data, int width, int height, String filePath) throws IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", new File(filePath).toPath());
        } catch (WriterException e) {
            throw new IOException("Không thể tạo mã QR Code", e);
        }
    }


    public Ve thucHienDoiVe(String maVeCu, String maGheMoi, String lyDo) {
        Connection conn = null;
        try {
            conn = ConnectSql.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Lấy vé cũ trên cùng connection (không dùng veDAO.findById vì nó tự mở connection khác)
            Ve veCu = null;
            String sqlSelectVeCu = "SELECT maVe, maChuyen, maLoaiVe, maSoGhe, maGaDi, maGaDen, tenGaDi, tenGaDen, " +
                    "ngayIn, trangThai, gioDi, gioDenDuKien, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan " +
                    "FROM Ve WHERE maVe = ? AND isActive = 1";
            try (PreparedStatement pst = conn.prepareStatement(sqlSelectVeCu)) {
                pst.setString(1, maVeCu);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        LocalDateTime ngayIn = null, gioDi = null, gioDenDuKien = null;
                        Timestamp ts1 = rs.getTimestamp("ngayIn");
                        if (ts1 != null) ngayIn = ts1.toLocalDateTime();
                        Timestamp ts2 = rs.getTimestamp("gioDi");
                        if (ts2 != null) gioDi = ts2.toLocalDateTime();
                        Timestamp ts3 = rs.getTimestamp("gioDenDuKien");
                        if (ts3 != null) gioDenDuKien = ts3.toLocalDateTime();

                        veCu = new Ve(
                                rs.getString("maVe"),
                                rs.getString("maChuyen"),
                                rs.getString("maLoaiVe"),
                                rs.getString("maSoGhe"),
                                rs.getString("maGaDi"),
                                rs.getString("maGaDen"),
                                rs.getString("tenGaDi"),
                                rs.getString("tenGaDen"),
                                ngayIn,
                                rs.getString("trangThai"),
                                gioDi,
                                gioDenDuKien,
                                rs.getObject("soToa", Integer.class),
                                rs.getString("loaiCho"),
                                rs.getString("loaiVe"),
                                rs.getString("maBangGia"),
                                rs.getObject("giaThanhToan", Float.class)
                        );
                    }
                }
            }

            if (veCu == null) {
                throw new IllegalArgumentException("Không tìm thấy vé cũ");
            }

            // 2. Validate trạng thái vé
            String trangThai = veCu.getTrangThai();
            if (!"Đã thanh toán".equals(trangThai) && !"Đã đặt".equals(trangThai)) {
                throw new IllegalStateException("Chỉ có thể đổi vé đã đặt hoặc đã thanh toán");
            }

            // 3. Validate thời hạn đổi (phải đổi trước 2 giờ so với gioDi)
            if (veCu.getGioDi() != null) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime deadline = veCu.getGioDi().minusHours(2);
                if (now.isAfter(deadline)) {
                    throw new IllegalStateException("Đã quá thời hạn đổi vé. Vé phải được đổi trước 2 giờ so với giờ khởi hành");
                }
            }

            if (veCu.getMaSoGhe() == null) {
                throw new IllegalStateException("Vé không có ghế được chỉ định");
            }

            // 4. Lấy thông tin ghế cũ và ghế mới trên cùng connection
            Ghe gheCu = null;
            Ghe gheMoi = null;

            String sqlSelectGhe = "SELECT maGhe, maToa, trangThai FROM Ghe WHERE maGhe = ?";

            try (PreparedStatement pst = conn.prepareStatement(sqlSelectGhe)) {
                pst.setString(1, veCu.getMaSoGhe());
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        gheCu = new Ghe();
                        gheCu.setMaGhe(rs.getString("maGhe"));
                        gheCu.setMaToa(rs.getString("maToa"));
                        gheCu.setTrangThai(rs.getString("trangThai"));
                    }
                }
            }
            if (gheCu == null) {
                throw new IllegalArgumentException("Không tìm thấy ghế cũ");
            }

            try (PreparedStatement pst = conn.prepareStatement(sqlSelectGhe)) {
                pst.setString(1, maGheMoi);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        gheMoi = new Ghe();
                        gheMoi.setMaGhe(rs.getString("maGhe"));
                        gheMoi.setMaToa(rs.getString("maToa"));
                        gheMoi.setTrangThai(rs.getString("trangThai"));
                    }
                }
            }
            if (gheMoi == null) {
                throw new IllegalArgumentException("Không tìm thấy ghế mới");
            }

            // 5. Ghế mới phải cùng toa với ghế cũ
            if (!gheCu.getMaToa().equals(gheMoi.getMaToa())) {
                throw new IllegalStateException("Chỉ được đổi ghế trong cùng một toa. Không thể đổi sang toa khác");
            }

            // 6. Ghế mới phải trống (Rảnh/Trống)
            String trangThaiGheMoi = gheMoi.getTrangThai();
            if (!"Rảnh".equalsIgnoreCase(trangThaiGheMoi) && !"Trống".equalsIgnoreCase(trangThaiGheMoi)) {
                throw new IllegalStateException("Ghế đã bị đặt");
            }

            // 7. Cập nhật trạng thái ghế cũ -> Rảnh
            String sqlUpdateGhe = "UPDATE Ghe SET trangThai = ? WHERE maGhe = ?";
            try (PreparedStatement pst = conn.prepareStatement(sqlUpdateGhe)) {
                pst.setString(1, "Rảnh");
                pst.setString(2, gheCu.getMaGhe());
                pst.executeUpdate();
            }

            // 8. Cập nhật trạng thái ghế mới -> Đã đặt
            try (PreparedStatement pst = conn.prepareStatement(sqlUpdateGhe)) {
                pst.setString(1, "Đã đặt");
                pst.setString(2, gheMoi.getMaGhe());
                pst.executeUpdate();
            }

            // 9. Tạo mã vé mới
            String maVeMoi = "VE_" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

            // 10. Tạo đối tượng Ve mới (trong bộ nhớ)
            Ve veMoi = new Ve(
                    maVeMoi,
                    veCu.getMaChuyen(),
                    veCu.getMaLoaiVe(),
                    maGheMoi,
                    veCu.getMaGaDi(),
                    veCu.getMaGaDen(),
                    veCu.getTenGaDi(),
                    veCu.getTenGaDen(),
                    LocalDateTime.now(),
                    veCu.getTrangThai(),
                    veCu.getGioDi(),
                    veCu.getGioDenDuKien(),
                    veCu.getSoToa(),
                    veCu.getLoaiCho(),
                    veCu.getLoaiVe(),
                    veCu.getMaBangGia(),
                    veCu.getGiaThanhToan()
            );

            // 11. Insert vé mới bằng conn hiện tại (không dùng veDAO.insert để tránh mở connection mới)
            String sqlInsertVe = "INSERT INTO Ve (maVe, maChuyen, maLoaiVe, maSoGhe, maGaDi, maGaDen, tenGaDi, tenGaDen, " +
                    "ngayIn, trangThai, gioDi, gioDenDuKien, soToa, loaiCho, loaiVe, maBangGia, giaThanhToan, isActive) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(sqlInsertVe)) {
                pst.setString(1, veMoi.getMaVe());
                pst.setString(2, veMoi.getMaChuyen());
                pst.setString(3, veMoi.getMaLoaiVe());
                pst.setString(4, veMoi.getMaSoGhe());
                pst.setString(5, veMoi.getMaGaDi());
                pst.setString(6, veMoi.getMaGaDen());
                pst.setString(7, veMoi.getTenGaDi());
                pst.setString(8, veMoi.getTenGaDen());
                pst.setTimestamp(9, Timestamp.valueOf(veMoi.getNgayIn()));
                pst.setString(10, veMoi.getTrangThai());
                if (veMoi.getGioDi() != null) pst.setTimestamp(11, Timestamp.valueOf(veMoi.getGioDi()));
                else pst.setNull(11, Types.TIMESTAMP);
                if (veMoi.getGioDenDuKien() != null) pst.setTimestamp(12, Timestamp.valueOf(veMoi.getGioDenDuKien()));
                else pst.setNull(12, Types.TIMESTAMP);
                if (veMoi.getSoToa() != null) pst.setInt(13, veMoi.getSoToa());
                else pst.setNull(13, Types.INTEGER);
                pst.setString(14, veMoi.getLoaiCho());
                pst.setString(15, veMoi.getLoaiVe());
                if (veMoi.getMaBangGia() != null && !veMoi.getMaBangGia().trim().isEmpty())
                    pst.setString(16, veMoi.getMaBangGia());
                else
                    pst.setNull(16, Types.VARCHAR);
                if (veMoi.getGiaThanhToan() != null)
                    pst.setFloat(17, veMoi.getGiaThanhToan());
                else
                    pst.setNull(17, Types.FLOAT);
                pst.setBoolean(18, true);

                System.out.println("DEBUG VeService.thucHienDoiVe: inserting maVeMoi=" + veMoi.getMaVe() +
                        " maBangGia=" + veMoi.getMaBangGia());
                pst.executeUpdate();
            }

            // 12. Cập nhật vé cũ -> Đã đổi (cũng trên conn)
            String sqlUpdateVeCu = "UPDATE Ve SET trangThai = ? WHERE maVe = ?";
            try (PreparedStatement pst = conn.prepareStatement(sqlUpdateVeCu)) {
                pst.setString(1, "Đã đổi");
                pst.setString(2, veCu.getMaVe());
                pst.executeUpdate();
            }

            // 13. Cập nhật ChiTietHoaDon trên cùng connection (dùng overload có conn nếu có)
            ChiTietHoaDon chiTietCu = chiTietHoaDonDAO.findById(maVeCu);
            if (chiTietCu != null) {
                String moTaCu = "Đã đổi sang " + maVeMoi;
                chiTietHoaDonDAO.updateMoTa(chiTietCu.getMaHoaDon(), maVeCu, moTaCu, conn);

                ChiTietHoaDon chiTietMoi = new ChiTietHoaDon();
                chiTietMoi.setMaHoaDon(chiTietCu.getMaHoaDon());
                chiTietMoi.setMaVe(maVeMoi);
                chiTietMoi.setMaLoaiVe(chiTietCu.getMaLoaiVe());
                chiTietMoi.setGiaGoc(chiTietCu.getGiaGoc());
                chiTietMoi.setGiaDaKM(chiTietCu.getGiaDaKM());

                String moTaMoi = "Đổi từ " + maVeCu;
                if (lyDo != null && !lyDo.trim().isEmpty()) {
                    moTaMoi += "; lý do: " + lyDo;
                }
                chiTietMoi.setMoTa(moTaMoi);

                chiTietHoaDonDAO.insert(chiTietMoi, conn);
            }

            conn.commit();
            return veMoi;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Lỗi khi đổi vé: " + e.getMessage(), e);
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
