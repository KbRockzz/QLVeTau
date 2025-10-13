package com.trainstation.model;

import java.io.Serializable;

public class ChiTietHoaDon implements Serializable {
    private String maChiTiet;
    private String maHoaDon;
    private String ticketId;
    private double donGia;
    private int soLuong;
    private double thanhTien;
    
    private HoaDon hoaDon;
    private Ticket ticket;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(String maChiTiet, String maHoaDon, String ticketId, double donGia, 
                         int soLuong, double thanhTien) {
        this.maChiTiet = maChiTiet;
        this.maHoaDon = maHoaDon;
        this.ticketId = ticketId;
        this.donGia = donGia;
        this.soLuong = soLuong;
        this.thanhTien = thanhTien;
    }

    public String getMaChiTiet() {
        return maChiTiet;
    }

    public void setMaChiTiet(String maChiTiet) {
        this.maChiTiet = maChiTiet;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
        if (hoaDon != null) {
            this.maHoaDon = hoaDon.getMaHoaDon();
        }
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        if (ticket != null) {
            this.ticketId = ticket.getTicketId();
        }
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "maChiTiet='" + maChiTiet + '\'' +
                ", maHoaDon='" + maHoaDon + '\'' +
                ", ticketId='" + ticketId + '\'' +
                ", donGia=" + donGia +
                ", soLuong=" + soLuong +
                ", thanhTien=" + thanhTien +
                '}';
    }
}
