package com.trainstation.service;

import com.trainstation.model.Ve;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;

/**
 * Test cases for DoiVeService
 */
public class DoiVeServiceTest {
    
    private DoiVeService doiVeService;
    
    @Before
    public void setUp() {
        doiVeService = DoiVeService.getInstance();
    }
    
    @Test
    public void testKiemTraChoPhepDoiVe_VeNull() {
        DoiVeService.KetQuaKiemTra ketQua = doiVeService.kiemTraChoPhepDoiVe(null);
        assertFalse("Vé null không được phép đổi", ketQua.isHopLe());
        assertTrue("Thông báo lỗi phải chứa 'không tồn tại'", 
                   ketQua.getThongBao().contains("không tồn tại"));
    }
    
    @Test
    public void testKiemTraChoPhepDoiVe_TrangThaiDaHuy() {
        Ve ve = new Ve();
        ve.setMaVe("V001");
        ve.setTrangThai("Đã hủy");
        ve.setGioDi(LocalDateTime.now().plusDays(3));
        
        DoiVeService.KetQuaKiemTra ketQua = doiVeService.kiemTraChoPhepDoiVe(ve);
        assertFalse("Vé đã hủy không được phép đổi", ketQua.isHopLe());
        assertTrue("Thông báo lỗi phải chứa thông tin trạng thái", 
                   ketQua.getThongBao().contains("trạng thái"));
    }
    
    @Test
    public void testKiemTraChoPhepDoiVe_TrangThaiDaDat_Truoc72Gio() {
        Ve ve = new Ve();
        ve.setMaVe("V001");
        ve.setTrangThai("Đã đặt");
        ve.setGioDi(LocalDateTime.now().plusHours(80)); // 80 giờ sau
        
        DoiVeService.KetQuaKiemTra ketQua = doiVeService.kiemTraChoPhepDoiVe(ve);
        assertTrue("Vé đã đặt trước 72 giờ được phép đổi", ketQua.isHopLe());
    }
    
    @Test
    public void testKiemTraChoPhepDoiVe_TrangThaiDaThanhToan_Truoc72Gio() {
        Ve ve = new Ve();
        ve.setMaVe("V001");
        ve.setTrangThai("Đã thanh toán");
        ve.setGioDi(LocalDateTime.now().plusHours(80));
        
        DoiVeService.KetQuaKiemTra ketQua = doiVeService.kiemTraChoPhepDoiVe(ve);
        assertTrue("Vé đã thanh toán trước 72 giờ được phép đổi", ketQua.isHopLe());
    }
    
    @Test
    public void testKiemTraChoPhepDoiVe_Duoi24Gio() {
        Ve ve = new Ve();
        ve.setMaVe("V001");
        ve.setTrangThai("Đã thanh toán");
        ve.setGioDi(LocalDateTime.now().plusHours(20)); // 20 giờ sau
        
        DoiVeService.KetQuaKiemTra ketQua = doiVeService.kiemTraChoPhepDoiVe(ve);
        assertFalse("Vé dưới 24 giờ không được phép đổi", ketQua.isHopLe());
        assertTrue("Thông báo lỗi phải chứa thông tin thời hạn", 
                   ketQua.getThongBao().contains("24 giờ"));
    }
    
    @Test
    public void testKiemTraChoPhepDoiVe_KhongCoGioKhoiHanh() {
        Ve ve = new Ve();
        ve.setMaVe("V001");
        ve.setTrangThai("Đã thanh toán");
        ve.setGioDi(null);
        
        DoiVeService.KetQuaKiemTra ketQua = doiVeService.kiemTraChoPhepDoiVe(ve);
        assertFalse("Vé không có giờ khởi hành không được phép đổi", ketQua.isHopLe());
        assertTrue("Thông báo lỗi phải chứa thông tin giờ khởi hành", 
                   ketQua.getThongBao().contains("giờ khởi hành"));
    }
    
    @Test
    public void testTinhPhiDoiVe_Truoc72Gio() {
        Ve veCu = taoVeMau("V001", 100000, LocalDateTime.now().plusHours(80));
        Ve veMoi = taoVeMau("V002", 120000, LocalDateTime.now().plusHours(80));
        
        DoiVeService.ThongTinPhiDoiVe phi = doiVeService.tinhPhiDoiVe(veCu, veMoi);
        
        assertNotNull("Thông tin phí không được null", phi);
        assertEquals("Giá vé cũ", 100000, phi.getGiaVeCu(), 0.01);
        assertEquals("Giá vé mới", 120000, phi.getGiaVeMoi(), 0.01);
        assertEquals("Chênh lệch giá", 20000, phi.getChenhLechGia(), 0.01);
        assertTrue("Phí đổi vé phải > 0", phi.getPhiDoiVe() > 0);
        assertTrue("Phí đổi vé trước 72h phải >= 20000", phi.getPhiDoiVe() >= 20000);
        assertEquals("Mức phí", "Trước 72 giờ", phi.getMucPhi());
    }
    
    @Test
    public void testTinhPhiDoiVe_24_72Gio() {
        Ve veCu = taoVeMau("V001", 100000, LocalDateTime.now().plusHours(48));
        Ve veMoi = taoVeMau("V002", 120000, LocalDateTime.now().plusHours(48));
        
        DoiVeService.ThongTinPhiDoiVe phi = doiVeService.tinhPhiDoiVe(veCu, veMoi);
        
        assertNotNull("Thông tin phí không được null", phi);
        assertTrue("Phí đổi vé 24-72h phải >= 50000", phi.getPhiDoiVe() >= 50000);
        assertEquals("Mức phí", "24-72 giờ", phi.getMucPhi());
    }
    
    @Test
    public void testTinhPhiDoiVe_GiaVeMoiCaoHon() {
        Ve veCu = taoVeMau("V001", 100000, LocalDateTime.now().plusHours(80));
        Ve veMoi = taoVeMau("V002", 150000, LocalDateTime.now().plusHours(80));
        
        DoiVeService.ThongTinPhiDoiVe phi = doiVeService.tinhPhiDoiVe(veCu, veMoi);
        
        assertTrue("Số tiền thu phải > 0 khi giá vé mới cao hơn", phi.getSoTienThu() > 0);
        assertEquals("Số tiền hoàn phải = 0", 0, phi.getSoTienHoan(), 0.01);
    }
    
    @Test
    public void testTinhPhiDoiVe_GiaVeMoiThapHon() {
        Ve veCu = taoVeMau("V001", 150000, LocalDateTime.now().plusHours(80));
        Ve veMoi = taoVeMau("V002", 100000, LocalDateTime.now().plusHours(80));
        
        DoiVeService.ThongTinPhiDoiVe phi = doiVeService.tinhPhiDoiVe(veCu, veMoi);
        
        // Vẫn phải trả phí đổi vé, nhưng có hoàn lại chênh lệch
        float tongTien = phi.getPhiDoiVe() + phi.getChenhLechGia();
        if (tongTien > 0) {
            assertTrue("Số tiền thu phải > 0", phi.getSoTienThu() > 0);
        } else {
            assertTrue("Số tiền hoàn phải > 0", phi.getSoTienHoan() > 0);
        }
    }
    
    /**
     * Tạo vé mẫu cho test
     */
    private Ve taoVeMau(String maVe, float gia, LocalDateTime gioDi) {
        Ve ve = new Ve();
        ve.setMaVe(maVe);
        ve.setGioDi(gioDi);
        ve.setTrangThai("Đã thanh toán");
        ve.setMaChuyen("CT001");
        ve.setMaSoGhe("A01");
        ve.setGaDi("Hà Nội");
        ve.setGaDen("Sài Gòn");
        ve.setMaLoaiVe("LV01");
        ve.setLoaiCho("Ghế ngồi");
        
        // Note: getDisplayPrice() would need actual pricing calculation
        // For test purposes, we assume the price is set correctly
        
        return ve;
    }
}
