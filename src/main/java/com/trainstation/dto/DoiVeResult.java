package com.trainstation.dto;

import com.trainstation.model.Ve;
import java.io.Serializable;

/**
 * DTO cho kết quả đổi vé
 */
public class DoiVeResult implements Serializable {
    private boolean thanhCong;
    private String thongBao;
    private Ve veCu;
    private Ve veMoi;
    private float chenhLechGia;
    private boolean canThanhToan;  // true nếu khách phải trả thêm tiền
    private boolean canHoanTien;   // true nếu khách được hoàn tiền
    private String maLichSu;       // ID của bản ghi lịch sử đổi vé
    
    public DoiVeResult() {
    }

    public DoiVeResult(boolean thanhCong, String thongBao) {
        this.thanhCong = thanhCong;
        this.thongBao = thongBao;
    }

    public boolean isThanhCong() {
        return thanhCong;
    }

    public void setThanhCong(boolean thanhCong) {
        this.thanhCong = thanhCong;
    }

    public String getThongBao() {
        return thongBao;
    }

    public void setThongBao(String thongBao) {
        this.thongBao = thongBao;
    }

    public Ve getVeCu() {
        return veCu;
    }

    public void setVeCu(Ve veCu) {
        this.veCu = veCu;
    }

    public Ve getVeMoi() {
        return veMoi;
    }

    public void setVeMoi(Ve veMoi) {
        this.veMoi = veMoi;
    }

    public float getChenhLechGia() {
        return chenhLechGia;
    }

    public void setChenhLechGia(float chenhLechGia) {
        this.chenhLechGia = chenhLechGia;
    }

    public boolean isCanThanhToan() {
        return canThanhToan;
    }

    public void setCanThanhToan(boolean canThanhToan) {
        this.canThanhToan = canThanhToan;
    }

    public boolean isCanHoanTien() {
        return canHoanTien;
    }

    public void setCanHoanTien(boolean canHoanTien) {
        this.canHoanTien = canHoanTien;
    }

    public String getMaLichSu() {
        return maLichSu;
    }

    public void setMaLichSu(String maLichSu) {
        this.maLichSu = maLichSu;
    }

    @Override
    public String toString() {
        return "DoiVeResult{" +
                "thanhCong=" + thanhCong +
                ", thongBao='" + thongBao + '\'' +
                ", chenhLechGia=" + chenhLechGia +
                ", canThanhToan=" + canThanhToan +
                ", canHoanTien=" + canHoanTien +
                ", maLichSu='" + maLichSu + '\'' +
                '}';
    }
}
