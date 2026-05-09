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
import com.trainstation.dao.GaDAO;
import com.trainstation.dao.ChiTietChuyenTauDAO;
import com.trainstation.model.Ve;
import com.trainstation.model.Ghe;
import com.trainstation.model.ChuyenTau;
import com.trainstation.model.BangGia;
import com.trainstation.model.ChiTietHoaDon;
import com.trainstation.model.HoaDon;
import com.trainstation.model.Ga;
import com.trainstation.model.KhachHang;
import com.trainstation.model.ToaTau;
import com.trainstation.model.LoaiVe;
import com.trainstation.model.ChiTietChuyenTau;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.trainstation.network.AppClient;
import com.trainstation.network.AppRequest;
import com.trainstation.network.AppResponse;
import com.trainstation.network.NetworkConfig;
import com.trainstation.network.RequestType;
import com.trainstation.repository.impl.VeRepositoryImpl;
import com.trainstation.service.impl.VeServiceImpl;

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
    private final GaDAO gaDAO;
    private final ChiTietChuyenTauDAO chiTietChuyenTauDAO;
    private final VeServiceImpl veServiceImpl;
    private final ConcurrentMap<String, Object> seatLocks = new ConcurrentHashMap<>();
    
    // Thời gian tối thiểu trước khi tàu chạy (phút)
    private static final int MINIMUM_MINUTES_BEFORE_DEPARTURE = 30;



    private VeService() {
        this.veDAO = VeDAO.getInstance();
        this.gheDAO = GheDAO.getInstance();
        this.chuyenTauDAO = ChuyenTauDAO.getInstance();
        this.bangGiaDAO = BangGiaDAO.getInstance();
        this.chiTietHoaDonDAO = ChiTietHoaDonDAO.getInstance();
        this.hoaDonDAO = HoaDonDAO.getInstance();
        this.gaDAO = GaDAO.getInstance();
        this.chiTietChuyenTauDAO = ChiTietChuyenTauDAO.getInstance();
        this.veServiceImpl = VeServiceImpl.getInstance();
    }

    public static synchronized VeService getInstance() {
        if (instance == null) {
            instance = new VeService();
        }
        return instance;
    }
    
    public Ve taoVe(Ve ve) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.INSERT_VE, ve));
            if (resp.isSuccess()) return (Ve) resp.getData();
            throw new RuntimeException("Không thể tạo vé: " + resp.getMessage());
        }
        Ve created = veServiceImpl.taoVe(ve);
        if (created != null) return created;
        throw new RuntimeException("Không thể tạo vé");
    }
    
    public boolean capNhatVe(Ve ve) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.UPDATE_VE, ve));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return veServiceImpl.capNhatVe(ve);
    }

    public Ve datVeChinhThuc(KhachHang khachHang, ChuyenTau chuyenTau, ToaTau toaTau, Ghe ghe, LoaiVe loaiVe) {
        if (khachHang == null) throw new IllegalArgumentException("Chưa chọn khách hàng");
        if (chuyenTau == null || chuyenTau.getMaChuyen() == null || chuyenTau.getMaChuyen().trim().isEmpty()) {
            throw new IllegalArgumentException("Chuyến tàu không hợp lệ");
        }
        if (toaTau == null || toaTau.getMaToa() == null || toaTau.getMaToa().trim().isEmpty()) {
            throw new IllegalArgumentException("Toa tàu không hợp lệ");
        }
        if (ghe == null || ghe.getMaGhe() == null || ghe.getMaGhe().trim().isEmpty()) {
            throw new IllegalArgumentException("Ghế không hợp lệ");
        }
        if (loaiVe == null || loaiVe.getMaLoaiVe() == null || loaiVe.getMaLoaiVe().trim().isEmpty()) {
            throw new IllegalArgumentException("Loại vé không hợp lệ");
        }

        String maGhe = ghe.getMaGhe().trim();
        Object lock = seatLocks.computeIfAbsent(maGhe, k -> new Object());

        synchronized (lock) {
            LocalDateTime bookingTime = LocalDateTime.now();

            ChuyenTau chuyenTauDb = chuyenTauDAO.findById(chuyenTau.getMaChuyen());
            if (chuyenTauDb == null) {
                throw new IllegalStateException("Không tìm thấy chuyến tàu");
            }
            if (chuyenTauDb.getGioDi() == null) {
                throw new IllegalStateException("Chuyến tàu không có thời gian khởi hành");
            }
            if (!chuyenTauDb.getGioDi().isAfter(bookingTime)) {
                throw new IllegalStateException("Không thể đặt vé cho chuyến đã khởi hành");
            }

            Ghe gheDb = gheDAO.findById(maGhe);
            if (gheDb == null) {
                throw new IllegalStateException("Không tìm thấy ghế trên hệ thống");
            }
            if (!isAvailableSeatStatus(gheDb.getTrangThai())) {
                throw new IllegalStateException("Ghế này đã không còn trống");
            }

            boolean tonTaiVeDangHoatDong = veDAO.getAll().stream()
                    .anyMatch(v -> maGhe.equals(v.getMaSoGhe())
                            && chuyenTauDb.getMaChuyen().equals(v.getMaChuyen())
                            && !isClosedTicketStatus(v.getTrangThai()));
            if (tonTaiVeDangHoatDong) {
                throw new IllegalStateException("Ghế đã có vé hợp lệ trên chuyến này");
            }

            String maChang = chuyenTauDb.getMaChang();
            if (maChang == null || maChang.trim().isEmpty()) {
                maChang = chuyenTauDb.getMaChuyen();
            }
            String loaiGheKey = gheDb.getLoaiGhe();
            if (loaiGheKey == null || loaiGheKey.trim().isEmpty()) {
                loaiGheKey = toaTau.getLoaiToa();
            }
            if (loaiGheKey == null || loaiGheKey.trim().isEmpty()) {
                throw new IllegalStateException("Không xác định được loại ghế để áp dụng bảng giá");
            }

            BangGia bangGia = bangGiaDAO.findApplicable(maChang, loaiGheKey.trim(), bookingTime);
            if (bangGia == null) {
                throw new IllegalStateException("Không tìm thấy bảng giá phù hợp");
            }

            String maVeMoi = generateTicketId();
            Ve ve = new Ve();
            ve.setMaVe(maVeMoi);
            ve.setMaChuyen(chuyenTauDb.getMaChuyen());
            ve.setMaLoaiVe(loaiVe.getMaLoaiVe());
            ve.setMaSoGhe(maGhe);
            ve.setMaGaDi(chuyenTauDb.getMaGaDi());
            ve.setMaGaDen(chuyenTauDb.getMaGaDen());
            ve.setTenGaDi(resolveTenGa(chuyenTauDb.getMaGaDi()));
            ve.setTenGaDen(resolveTenGa(chuyenTauDb.getMaGaDen()));
            ve.setNgayIn(bookingTime);
            ve.setTrangThai("Đã đặt");
            ve.setGioDi(chuyenTauDb.getGioDi());
            ve.setGioDenDuKien(chuyenTauDb.getGioDen());
            ChiTietChuyenTau chiTietChuyenTau = chiTietChuyenTauDAO.findById(chuyenTauDb.getMaChuyen(), toaTau.getMaToa());
            ve.setSoToa(chiTietChuyenTau != null ? chiTietChuyenTau.getSoThuTuToa() : null);
            ve.setLoaiCho(toaTau.getLoaiToa());
            ve.setLoaiVe(loaiVe.getTenLoai());
            ve.setMaBangGia(bangGia.getMaBangGia());

            TinhGiaService.KetQuaGia ketQuaGia = TinhGiaService.getInstance().tinhGiaChoVe(ve);
            if (ketQuaGia != null) {
                if (ketQuaGia.maBangGia != null && !ketQuaGia.maBangGia.trim().isEmpty()) {
                    ve.setMaBangGia(ketQuaGia.maBangGia);
                }
                ve.setGiaThanhToan(ketQuaGia.giaDaKM);
            }

            if (!veDAO.insert(ve)) {
                throw new RuntimeException("Không thể lưu vé");
            }

            gheDb.setTrangThai("Đã đặt");
            if (!gheDAO.update(gheDb)) {
                throw new RuntimeException("Không thể cập nhật trạng thái ghế");
            }

            return ve;
        }
    }

    private boolean isAvailableSeatStatus(String trangThai) {
        if (trangThai == null) return false;
        return "Rảnh".equalsIgnoreCase(trangThai)
                || "Trống".equalsIgnoreCase(trangThai)
                || "RANH".equalsIgnoreCase(trangThai);
    }

    private boolean isClosedTicketStatus(String trangThai) {
        if (trangThai == null) return false;
        return "Đã hủy".equalsIgnoreCase(trangThai)
                || "Đã hoàn".equalsIgnoreCase(trangThai)
                || "Đã đổi".equalsIgnoreCase(trangThai);
    }

    private String resolveTenGa(String maGa) {
        if (maGa == null || maGa.trim().isEmpty()) return null;
        Ga ga = gaDAO.findById(maGa);
        return ga != null ? ga.getTenGa() : maGa;
    }

    private String generateTicketId() {
        return "VE_" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
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
                gheMoi.setTrangThai("Đã đặt");
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
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.GET_ALL_VE));
            if (resp.isSuccess() && resp.getData() != null) return (List<Ve>) resp.getData();
            return java.util.Collections.emptyList();
        }
        return veServiceImpl.layTatCaVe();
    }
    
    public Ve timVeTheoMa(String maVe) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.FIND_VE_BY_ID, maVe));
            if (resp.isSuccess()) return (Ve) resp.getData();
            return null;
        }
        return veServiceImpl.timVeTheoMa(maVe);
    }

    /**
     * Lấy danh sách vé theo chuyến tàu
     */
    public List<Ve> layVeTheoChuyenTau(String maChuyen) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.GET_VE_BY_CHUYEN, maChuyen));
            if (resp.isSuccess() && resp.getData() != null) return (List<Ve>) resp.getData();
            return java.util.Collections.emptyList();
        }
        return veServiceImpl.layVeTheoChuyen(maChuyen);
    }

    /**
     * Lấy danh sách vé theo trạng thái
     */
    public List<Ve> layVeTheoTrangThai(String trangThai) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.GET_VE_BY_TRANGTHAI, trangThai));
            if (resp.isSuccess() && resp.getData() != null) return (List<Ve>) resp.getData();
            return java.util.Collections.emptyList();
        }
        return veServiceImpl.layVeTheoTrangThai(trangThai);
    }

    /**
     * Đếm số vé theo trạng thái
     */
    public int demVeTheoTrangThai(String trangThai) {
        return layVeTheoTrangThai(trangThai).size();
    }
    
    public boolean xoaVe(String maVe) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.DELETE_VE, maVe));
            if (resp.isSuccess() && resp.getData() != null) return (Boolean) resp.getData();
            return false;
        }
        return veServiceImpl.xoaVe(maVe);
    }
    
    public List<Ve> layVeTheoKhachHang(String maKH) {
        if (NetworkConfig.isClientMode()) {
            AppResponse resp = AppClient.getInstance().sendRequest(new AppRequest(RequestType.GET_VE_BY_KHACHHANG, maKH));
            if (resp.isSuccess() && resp.getData() != null) return (List<Ve>) resp.getData();
            return java.util.Collections.emptyList();
        }
        return VeRepositoryImpl.getInstance().findByKhachHang(maKH);
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

            // Mã vạch QR Code
            Paragraph qrTitle = new Paragraph("MÃ QUÉT")
                    .setFont(font)
                    .setFontSize(13)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(qrTitle);

            // Tạo mã vạch QR Code từ `maVe`
            String qrCodePath = "tickets/QrCode_" + ve.getMaVe() + ".png";
            generateQRCodeImage(ve.getMaVe(), 100, 100, qrCodePath);

            // Thêm mã vạch QR Code vào PDF
            com.itextpdf.layout.element.Image qrImage = new com.itextpdf.layout.element.Image(
                    com.itextpdf.io.image.ImageDataFactory.create(qrCodePath));
            qrImage.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(qrImage);

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
                    .add(new Paragraph(ve.getTenGaDi() != null ? ve.getTenGaDi() : "N/A").setFont(font))
                    .setTextAlignment(TextAlignment.CENTER));
            gaTable.addCell(new Cell()
                    .add(new Paragraph(ve.getTenGaDen() != null ? ve.getTenGaDen() : "N/A").setFont(font))
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
                    .add(new Paragraph(ve.getSoToa() != null ? String.valueOf(ve.getSoToa()) : "N/A").setFont(font)));

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

            Ve veCu = veDAO.findById(maVeCu, conn);
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

            Ghe gheCu = gheDAO.findById(veCu.getMaSoGhe(), conn);
            if (gheCu == null) {
                throw new IllegalArgumentException("Không tìm thấy ghế cũ");
            }

            Ghe gheMoi = gheDAO.findById(maGheMoi, conn);
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

            gheCu.setTrangThai("Rảnh");
            if (!gheDAO.update(gheCu, conn)) {
                throw new RuntimeException("Không thể cập nhật ghế cũ: " + gheCu.getMaGhe());
            }

            gheMoi.setTrangThai("Đã đặt");
            if (!gheDAO.update(gheMoi, conn)) {
                throw new RuntimeException("Không thể cập nhật ghế mới: " + gheMoi.getMaGhe());
            }

            // 9. Tạo mã vé mới
            String maVeMoi = generateTicketId();

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

            if (!veDAO.insert(veMoi, conn)) {
                throw new RuntimeException("Không thể tạo vé mới với mã: " + veMoi.getMaVe());
            }

            veCu.setTrangThai("Đã đổi");
            if (!veDAO.update(veCu, conn)) {
                throw new RuntimeException("Không thể cập nhật trạng thái vé cũ: " + veCu.getMaVe());
            }

            ChiTietHoaDon chiTietCu = chiTietHoaDonDAO.findById(maVeCu, conn);
            if (chiTietCu != null) {
                String moTaCu = "Đã đổi sang " + maVeMoi;
                if (!chiTietHoaDonDAO.updateMoTa(chiTietCu.getMaHoaDon(), maVeCu, moTaCu, conn)) {
                    throw new RuntimeException("Không thể cập nhật chi tiết hóa đơn vé cũ");
                }

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

                if (!chiTietHoaDonDAO.insert(chiTietMoi, conn)) {
                    throw new RuntimeException("Không thể tạo chi tiết hóa đơn vé mới");
                }
            }

            conn.commit();
            return veMoi;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    throw new RuntimeException("Lỗi rollback khi đổi vé: " + rollbackEx.getMessage(), rollbackEx);
                }
            }
            throw new RuntimeException("Lỗi khi đổi vé: " + e.getMessage(), e);
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    throw new RuntimeException("Lỗi rollback khi đổi vé: " + rollbackEx.getMessage(), rollbackEx);
                }
            }
            throw new RuntimeException("Lỗi khi đổi vé: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    throw new RuntimeException("Lỗi đóng kết nối sau đổi vé: " + closeEx.getMessage(), closeEx);
                }
            }
        }
    }
}
