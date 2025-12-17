# Cập nhật Sơ Đồ Ghế trong Dialog Đổi Vé

## Thay đổi

Đã cập nhật `DlgDoiVe` để hiển thị sơ đồ ghế giống như trong `PnlDatVe` (panel đặt vé).

### Trước khi thay đổi

- Sơ đồ ghế đơn giản: 4 cột, không có lối đi
- Layout: GridLayout(0, 4, 5, 5)
- Hiển thị: [Ghế][Ghế][Ghế][Ghế]

### Sau khi thay đổi

- Sơ đồ ghế giống PnlDatVe: 2-2 với lối đi ở giữa
- Layout: GridLayout(soHang, 5, 5, 5)
- Hiển thị:
  ```
  [Ghế] [Ghế]  [Lối đi]  [Ghế] [Ghế]
  [Ghế] [Ghế]  [Lối đi]  [Ghế] [Ghế]
  [Ghế] [Ghế]  [Lối đi]  [Ghế] [Ghế]
  ```

## Chi tiết kỹ thuật

### 1. Cấu trúc layout mới

```java
// Tính số hàng: 4 ghế mỗi hàng (2+2)
int soGhe = danhSachGhe.size();
int soHang = (int) Math.ceil(soGhe / 4.0);

// GridLayout với 5 cột: 2 ghế | lối đi | 2 ghế
pnlSeatMap.setLayout(new GridLayout(soHang, 5, 5, 5));
```

### 2. Thêm lối đi (aisle)

```java
// Lối đi (aisle) - màu xám
JPanel pnlLoiDi = new JPanel();
pnlLoiDi.setBackground(new Color(200, 200, 200));
pnlLoiDi.setPreferredSize(new Dimension(30, 40));
pnlSeatMap.add(pnlLoiDi);
```

### 3. Cải thiện hiển thị ghế

- Thêm border đen cho mỗi ghế: `BorderFactory.createLineBorder(Color.BLACK, 2)`
- Thêm font in đậm: `Font("Arial", Font.BOLD, 12)`
- Màu chữ đen thay vì trắng cho dễ đọc: `setForeground(Color.BLACK)`
- Tooltip chi tiết cho từng ghế

### 4. Tương thích trạng thái ghế

Cập nhật để hỗ trợ cả "Rảnh" (database) và "Trống" (code cũ):

```java
if ("Rảnh".equalsIgnoreCase(ghe.getTrangThai()) || 
    "Trống".equalsIgnoreCase(ghe.getTrangThai())) {
    // Ghế có thể chọn
}
```

## Màu sắc ghế

- 🟢 **Xanh lá (34, 139, 34)**: Ghế trống, có thể chọn
- 🔴 **Đỏ**: Ghế đã đặt, không thể chọn
- 🟡 **Cam**: Ghế hiện tại của vé
- 🔵 **Xanh dương**: Ghế đang được chọn

## Files thay đổi

1. **DlgDoiVe.java**:
   - `initComponents()`: Thay đổi layout của pnlSeatMap
   - `loadSeatMap()`: Thêm logic hiển thị 2-2 với lối đi
   - `taoNutGhe()`: Method mới để tạo nút ghế với style giống PnlDatVe
   - `updateSeatColors()`: Cập nhật để tương thích

2. **VeService.java**:
   - Cập nhật validation và update status sử dụng "Rảnh" thay vì "Trống"
   - Hỗ trợ cả hai giá trị cho tương thích ngược

## Kết quả

✅ Sơ đồ ghế trong dialog đổi vé giờ đây giống hệt như trong panel đặt vé
✅ Layout 2-2 với lối đi ở giữa
✅ Border và font style nhất quán
✅ Tooltip thông tin rõ ràng
✅ Tương thích với database (sử dụng "Rảnh")
