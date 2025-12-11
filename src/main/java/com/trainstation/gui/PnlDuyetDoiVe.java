package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.model.*;
import com.trainstation.service.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel duyệt yêu cầu đổi vé - cho quản lý
 */
public class PnlDuyetDoiVe extends JPanel {
    private TaiKhoan taiKhoanHienTai;
    private VeService veService;
    
    private JTable tblYeuCau;
    private DefaultTableModel modelYeuCau;
    private JButton btnDuyet;
    private JButton btnTuChoi;
    private JButton btnRefresh;
    private JTextArea txtChiTiet;

    public PnlDuyetDoiVe(TaiKhoan taiKhoan) {
        this.taiKhoanHienTai = taiKhoan;
        this.veService = VeService.getInstance();
        initComponents();
        loadDanhSachChoDuyet();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel lblTieuDe = new JLabel("DUYỆT YÊU CẦU ĐỔI VÉ", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        // Center panel - split
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);

        // Top - Table
        String[] columns = {
            "Mã lịch sử", "Mã vé", "Nhân viên", "Thời gian", 
            "Chênh lệch giá", "Trạng thái"
        };
        modelYeuCau = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblYeuCau = new JTable(modelYeuCau);
        tblYeuCau.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiChiTiet();
            }
        });
        
        JScrollPane scrollTable = new JScrollPane(tblYeuCau);
        scrollTable.setBorder(BorderFactory.createTitledBorder("Danh sách yêu cầu chờ duyệt"));
        splitPane.setTopComponent(scrollTable);

        // Bottom - Details
        JPanel pnlChiTiet = new JPanel(new BorderLayout(5, 5));
        pnlChiTiet.setBorder(BorderFactory.createTitledBorder("Chi tiết yêu cầu"));
        
        txtChiTiet = new JTextArea();
        txtChiTiet.setEditable(false);
        txtChiTiet.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtChiTiet.setLineWrap(true);
        txtChiTiet.setWrapStyleWord(true);
        
        JScrollPane scrollChiTiet = new JScrollPane(txtChiTiet);
        pnlChiTiet.add(scrollChiTiet, BorderLayout.CENTER);
        
        splitPane.setBottomComponent(pnlChiTiet);
        add(splitPane, BorderLayout.CENTER);

        // Button panel
        JPanel pnlButtons = MaterialInitializer.createButtonPanel();
        
        btnDuyet = new JButton("Duyệt");
        btnDuyet.addActionListener(e -> duyetYeuCau(true));
        MaterialInitializer.styleButton(btnDuyet);
        pnlButtons.add(btnDuyet);
        
        btnTuChoi = new JButton("Từ chối");
        btnTuChoi.addActionListener(e -> duyetYeuCau(false));
        MaterialInitializer.styleButton(btnTuChoi);
        pnlButtons.add(btnTuChoi);
        
        btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadDanhSachChoDuyet());
        MaterialInitializer.styleButton(btnRefresh);
        pnlButtons.add(btnRefresh);
        
        add(pnlButtons, BorderLayout.SOUTH);
    }

    private void loadDanhSachChoDuyet() {
        modelYeuCau.setRowCount(0);
        
        List<LichSuDoiVe> danhSach = veService.layDanhSachChoDuyet();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (LichSuDoiVe lichSu : danhSach) {
            modelYeuCau.addRow(new Object[]{
                lichSu.getMaLichSu(),
                lichSu.getMaVe(),
                lichSu.getMaNV(),
                lichSu.getThoiGian() != null ? lichSu.getThoiGian().format(formatter) : "N/A",
                String.format("%+,.0f VNĐ", lichSu.getChenhLechGia()),
                lichSu.getTrangThai()
            });
        }
        
        txtChiTiet.setText("");
    }

    private void hienThiChiTiet() {
        int row = tblYeuCau.getSelectedRow();
        if (row < 0) {
            txtChiTiet.setText("");
            return;
        }
        
        String maLichSu = (String) modelYeuCau.getValueAt(row, 0);
        String maVe = (String) modelYeuCau.getValueAt(row, 1);
        String maNV = (String) modelYeuCau.getValueAt(row, 2);
        String thoiGian = (String) modelYeuCau.getValueAt(row, 3);
        String chenhLech = (String) modelYeuCau.getValueAt(row, 4);
        
        List<LichSuDoiVe> danhSach = veService.layLichSuDoiVe(maVe);
        LichSuDoiVe lichSu = danhSach.stream()
            .filter(ls -> ls.getMaLichSu().equals(maLichSu))
            .findFirst()
            .orElse(null);
        
        if (lichSu == null) {
            txtChiTiet.setText("Không tìm thấy chi tiết yêu cầu.");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("MÃ LỊCH SỬ: ").append(lichSu.getMaLichSu()).append("\n");
        sb.append("MÃ VÉ: ").append(lichSu.getMaVe()).append("\n");
        sb.append("NHÂN VIÊN: ").append(lichSu.getMaNV()).append("\n");
        sb.append("THỜI GIAN: ").append(thoiGian).append("\n");
        sb.append("CHÊNH LỆCH GIÁ: ").append(chenhLech).append("\n");
        sb.append("\n");
        sb.append("THÔNG TIN VÉ CŨ:\n");
        sb.append(lichSu.getChiTietCu() != null ? lichSu.getChiTietCu() : "N/A").append("\n");
        sb.append("\n");
        sb.append("THÔNG TIN VÉ MỚI:\n");
        sb.append(lichSu.getChiTietMoi() != null ? lichSu.getChiTietMoi() : "N/A").append("\n");
        sb.append("\n");
        sb.append("LÝ DO:\n");
        sb.append(lichSu.getLyDo() != null && !lichSu.getLyDo().trim().isEmpty() 
            ? lichSu.getLyDo() : "Không có lý do").append("\n");
        
        txtChiTiet.setText(sb.toString());
        txtChiTiet.setCaretPosition(0);
    }

    private void duyetYeuCau(boolean chapNhan) {
        int row = tblYeuCau.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn yêu cầu cần duyệt!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String maLichSu = (String) modelYeuCau.getValueAt(row, 0);
        String action = chapNhan ? "duyệt" : "từ chối";
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn " + action + " yêu cầu này?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        boolean success = veService.approveDoiVe(maLichSu, taiKhoanHienTai.getMaNV(), chapNhan);
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Đã " + action + " yêu cầu thành công!", 
                "Thành công", 
                JOptionPane.INFORMATION_MESSAGE);
            loadDanhSachChoDuyet();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Không thể " + action + " yêu cầu!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
