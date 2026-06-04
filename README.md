# Quản Lý Nhà Hàng (Java Swing)

Ứng dụng desktop quản lý nhà hàng: thực đơn, bàn ăn, nhân viên, order & thanh toán, báo cáo doanh thu.
Giao diện Java Swing + FlatLaf, dữ liệu lưu file (Java Serialization).

## Yêu cầu
- JDK 17 trở lên (đã test với JDK 21)
- Maven (hoặc mở bằng IntelliJ / NetBeans / Eclipse có sẵn Maven)

## Tài khoản mặc định
| Username | Mật khẩu | Vai trò            |
|----------|----------|--------------------|
| admin    | admin    | Quản lý            |
| nv01     | 123      | Nhân viên phục vụ  |
| tn01     | 123      | Thu ngân           |

Hệ thống có **3 tác nhân** với chức năng tách bạch:
- **Quản lý (admin)**: quản lý dữ liệu nền — Thực đơn, Sơ đồ bàn (thêm/sửa/đổi trạng thái/xóa bàn),
  Nhân viên, Tài khoản; Tra cứu hóa đơn và Báo cáo doanh thu. *Không* tạo order, *không* thanh toán.
- **Nhân viên phục vụ (nv01)**: Sơ đồ bàn + Tạo/Cập nhật order (chọn bàn, chọn món, nhập/đổi số lượng,
  xóa món, tạm tính). *Không* thanh toán.
- **Thu ngân (tn01)**: Sơ đồ bàn + Thanh toán (chiết khấu, tiền khách đưa, tiền thừa, xác nhận,
  lưu hóa đơn, giải phóng bàn) + Tra cứu/In hóa đơn.

## Cách chạy

### Cách 1 — Maven
```bash
mvn clean compile
mvn exec:java
```
Hoặc đóng gói chạy độc lập:
```bash
mvn clean package
java -jar target/quan-ly-nha-hang.jar
```

### Cách 2 — Mở bằng IDE
Import dự án dạng Maven project, chạy class `com.restaurant.Main`.

### Cách 3 — javac thủ công (không cần Maven)
```bash
# Tải sẵn lib/flatlaf-3.4.1.jar
javac -encoding UTF-8 -cp "lib/flatlaf-3.4.1.jar" -d out $(find src/main/java -name "*.java")
java -cp "out;lib/flatlaf-3.4.1.jar" com.restaurant.Main    # Windows dùng dấu ;
```

## Cấu trúc
```
src/main/java/com/restaurant/
├── Main.java              # Khởi động: set FlatLaf, seed data, mở LoginFrame
├── model/                 # MonAn, Ban, NhanVien (có luongTheoCa), HoaDon (có chiếtKhấu/tiềnThừa), ...
├── dao/                   # Đọc/ghi file .dat (Serialization)
├── service/               # MonAnService (validate đơn giá >0), NhanVienService (validate lương >0),
│                          # HoaDonService (thanh toán có chiết khấu/tiền thừa, thống kê, top món)
├── ui/                    # LoginFrame, MainFrame, UITheme
│   └── panel/             # ThucDon, Ban, NhanVien, Order, TaiKhoan, HoaDon (tra cứu), ThongKe (báo cáo)
└── util/                  # SessionManager, DataSeeder, IDGenerator, Formatter
```

## Dữ liệu
Lưu trong thư mục `data/` (tự tạo lần chạy đầu): `monan.dat`, `ban.dat`,
`nhanvien.dat`, `hoadon.dat`, `taikhoan.dat`. Xóa thư mục này để reset về dữ liệu mẫu.

## Chức năng chính
1. **Đăng nhập** phân quyền 3 vai trò: Quản lý / Nhân viên phục vụ / Thu ngân.
2. **Thực đơn** (Quản lý) — thêm/sửa/xóa món (đơn giá >0), lọc theo danh mục, tìm kiếm.
3. **Sơ đồ bàn** — lưới bàn màu (xanh: trống, đỏ: đang phục vụ). Quản lý click để quản lý bàn
   (sửa tên / đổi trạng thái / xóa); Nhân viên phục vụ click để mở order; Thu ngân click để thanh toán.
   Có ô tìm kiếm bàn cho mọi vai trò.
4. **Tạo / Cập nhật order** (Nhân viên phục vụ) — chọn bàn, thêm món (cộng dồn SL trùng),
   đổi số lượng, xóa món, hủy order; hiển thị tạm tính.
5. **Thanh toán** (Thu ngân) — chọn order đang mở, nhập % chiết khấu + tiền khách đưa,
   tính tiền thừa (chặn khi tiền đưa < tổng thanh toán), lưu hóa đơn, giải phóng bàn, xem/in hóa đơn.
6. **Nhân viên** (Quản lý) — quản lý thông tin nhân viên (có lương theo ca >0).
7. **Tài khoản** (Quản lý) — quản lý tài khoản đăng nhập.
8. **Tra cứu hóa đơn** (Quản lý, Thu ngân) — tìm theo mã hóa đơn / mã bàn, xem chi tiết món.
9. **Báo cáo doanh thu** (Quản lý) — lọc theo: Hôm nay / Tháng này / Năm này / Khoảng ngày tùy chỉnh,
   hiển thị tổng số hóa đơn, tổng doanh thu, **Top 10 món bán chạy** (dùng HashMap).

## Kiểm tra (Smoke Test)
```bash
# Xóa data/ để tạo dữ liệu mẫu mới, biên dịch, chạy test
rm -rf data && javac -encoding UTF-8 -cp "lib/flatlaf-3.4.1.jar" -d out $(find src/main/java -name "*.java")
# Test: lương 150k, validate >0, chiết khấu 10% + tiền thừa, tìm hóa đơn, thống kê + top món
```

Kết quả: Login OK · Lương NV001: 150.000đ · Đơn giá/lương =0 bị chặn · Thanh toán có chiết khấu 10% → 
tiền thừa 19.000đ · Tìm hóa đơn OK · Thống kê hôm nay: 1 HĐ, 81.000đ · Top món: Gỏi cuốn tôm thịt x2.
