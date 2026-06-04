# ERD – Quản Lý Nhà Hàng

Sơ đồ dưới đây dùng cú pháp Mermaid (render được trên GitHub, VS Code Markdown Preview, Obsidian…).

```mermaid
erDiagram
    MONAN {
        string maMon PK
        string tenMon
        string danhMuc
        double donGia
    }
    BAN {
        string maBan PK
        string tenBan
        enum   trangThai
    }
    NHANVIEN {
        string  maNV PK
        string  tenNV
        enum    caLamViec
        string  sdt
        double  luongTheoCa
    }
    TAIKHOAN {
        string username PK
        string password
        enum   vaiTro
        string maNV FK
        string tenHienThi
    }
    HOADON {
        string   maHD PK
        string   maBan FK
        string   tenBan
        string   maNV FK
        string   tenNV
        datetime thoiGianTao
        datetime thoiGianThanhToan
        enum     trangThai
        double   chietKhau
        double   tienKhachDua
        double   tienThua
    }
    CHITIETHOADON {
        string maMon FK
        string tenMon
        double donGia
        int    soLuong
    }

    HOADON       ||--o{ CHITIETHOADON : "co chi tiet"
    BAN          ||--o{ HOADON        : "duoc phuc vu boi"
    NHANVIEN     ||--o{ HOADON        : "lap hoa don"
    NHANVIEN     |o--o| TAIKHOAN      : "co tai khoan (0..1 - 1)"
    MONAN        ||--o{ CHITIETHOADON : "xuat hien trong"
```

## Quan hệ tóm tắt

| # | Bảng A | Cardinality | Bảng B | Mô tả |
|---|---|---|---|---|
| 1 | `BAN` | 1 → 0..N | `HOADON` | Một bàn có thể phát sinh nhiều hóa đơn theo thời gian |
| 2 | `NHANVIEN` | 1 → 0..N | `HOADON` | Một nhân viên lập được nhiều hóa đơn |
| 3 | `HOADON` | 1 → 1..N | `CHITIETHOADON` | Mỗi hóa đơn có ít nhất một dòng chi tiết khi thanh toán |
| 4 | `MONAN` | 1 → 0..N | `CHITIETHOADON` | Một món ăn có thể xuất hiện trong nhiều hóa đơn |
| 5 | `NHANVIEN` | 1 → 0..1 | `TAIKHOAN` | Một nhân viên có thể có một tài khoản đăng nhập (admin có `maNV = ""`) |
