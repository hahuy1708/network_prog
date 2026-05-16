# Yêu Cầu Đề Bài
## Đề Thi Thực Hành Lập Trình Mạng — Phân Công Cán Bộ Coi Thi

---

## 1. Tổng Quan Bài Toán

Xây dựng ứng dụng mạng theo mô hình **Client – Server** (giao thức **TCP**) thực hiện:

- **Client** cho người dùng nhập **m** (số giám thị), **n** (số phòng thi), **browse chọn** 2 file `.xlsx` từ máy client, rồi gửi lên Server
- **Server** xử lý phân công cán bộ coi thi theo các ràng buộc nghiệp vụ
- **Server** trả về 2 file kết quả cho Client và **lưu kết quả phân công vào MySQL**
- **Server** tự động hiển thị **IP LAN** của chính nó trên GUI để Client biết địa chỉ kết nối
- **Client** cho phép người dùng **nhập IP LAN** của Server và **chọn thư mục lưu** file kết quả trên máy client

---

## 2. Tham Số Đầu Vào

| Tham số | Kiểu | Ý nghĩa |
|---------|------|---------|
| **m** | int (nhập tay trên GUI) | Số giám thị muốn sử dụng — server lấy **m cán bộ đầu tiên** từ file xlsx |
| **n** | int (nhập tay trên GUI) | Số phòng thi muốn sử dụng — server lấy **n phòng đầu tiên** từ file xlsx |

> Ràng buộc: `m >= 2`, `n >= 1`, `m <= số dòng trong file cán bộ`, `n <= số dòng trong file phòng`.
> File xlsx là nguồn dữ liệu gốc — m/n chỉ là bộ lọc quy mô cho từng đợt thi.

---

## 3. Dữ Liệu Đầu Vào (File Danh sach can bo coi thi.xlsx)

### 3.1. Danh sách cán bộ coi thi

Sheet: **"Danh sách cán bộ"**

| STT | Mã GV | Họ và Tên | Ngày sinh | Đơn vị công tác |
|-----|-------|-----------|-----------|-----------------|
| 1 | 105150103 | Võ Năm | 09/04/1961 | Trường ĐH Bách Khoa |
| 2 | 101170001 | Nguyễn Văn An | ... | Trường ĐH Sư phạm |
| ... | ... | ... | ... | ... |


### 3.2. Danh sách phòng thi

Sheet: **"DS phong thi"**

| STT | Phòng thi | Địa điểm |
|-----|-----------|----------|
| 1 | 128 | Đà Nẵng |
| 2 | 129 | Đà Nẵng |
| 23 | 150 | Huế |
| ... | ... | ... |


---

## 4. Quy Tắc Nghiệp Vụ (Business Logic)

### 4.1. Các khái niệm

| Thuật ngữ | Ý nghĩa |
|-----------|---------|
| **Giám thị 1 / Giám thị 2** | Hai cán bộ phụ trách một phòng thi trong một ca |
| **Cán bộ giám sát hành lang** | Cán bộ dư ra sau khi điền đủ 2 GT/phòng |
| **Ca thi** | Một lượt thi — hệ thống hỗ trợ nhiều ca (ca 1, ca 2, ...) |

### 4.2. Luật phân công

#### Ca thi đầu tiên (Ca 1)
- Mỗi phòng được phân công **đúng 2 giám thị** (GT1 và GT2).
- Tổng GV cần: `2 × n`. Số GV dư (`m - 2n`) → **giám sát hành lang**.
- Hành lang được **chia đều** theo số phòng (mỗi người quản lý một đoạn phòng liên tiếp).

#### Ca thi tiếp theo (Ca 2, Ca 3, ...)

Ràng buộc bắt buộc khi phân công ca mới:

1. **Không trùng phòng cũ** — GV không được coi lại phòng đã coi ở bất kỳ ca nào trước.
2. **Không trùng cặp** — 2 GV trong cùng phòng không được là cặp đã từng ngồi chung phòng đó ở ca trước.
3. **GV giám sát hành lang vẫn có thể** được xếp làm giám thị phòng thi ở ca sau.

> Đây là bài toán **xoay vòng phân công có ràng buộc** nhằm đảm bảo tính công bằng và chống gian lận.

---

## 5. Dữ Liệu Đầu Ra

### 5.1. File `DANHSACHPHANCONG.XLSX`

| STT | Mã GV | Họ và Tên | Vai trò | Phòng thi |
|-----|-------|-----------|---------|-----------|
| 1 | 105150103 | Võ Năm | Giám thị 1 | 128 |
| 2 | 101170014 | Trần Hưng Đức | Giám thị 2 | 128 |
| 3 | ... | ... | Giám thị 1 | 129 |

### 5.2. File `DANHSACHGIAMSAT.XLSX`

| STT | Mã GV | Họ và Tên | Phòng thi được giám sát |
|-----|-------|-----------|------------------------|
| 1 | GV3 | Nguyễn Bôn | Từ 128 đến 137 |
| 2 | GV7 | Lê Cam | Từ 138 đến 147 |

---

## 6. MySQL — Lưu Kết Quả Phân Công

### 6.1. MySQL lưu gì?

MySQL **KHÔNG lưu dữ liệu gốc** từ file `.xlsx`. MySQL chỉ lưu **kết quả SAU KHI phân công**, bao gồm:

| Dữ liệu | Mô tả |
|----------|-------|
| **Phân công giám thị** | Mã GV + Họ tên + Phòng thi + Vai trò (GT1/GT2) + Ca thi |
| **Giám sát hành lang** | Mã GV + Họ tên + Phạm vi phòng giám sát (từ phòng X đến phòng Y) |

> Tức là: Client gửi file gốc → Server cắt theo m/n → chạy thuật toán phân công → **lưu kết quả đã phân công** vào MySQL → gửi file kết quả về Client.

### 6.2. Cấu trúc Database

```sql
CREATE DATABASE IF NOT EXISTS exam_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE exam_db;

-- Bảng lưu kết quả phân công giám thị
CREATE TABLE IF NOT EXISTS phan_cong (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    ma_gv       VARCHAR(50)  NOT NULL,
    ho_ten      VARCHAR(200),
    phong_thi   VARCHAR(50),
    vi_tri      INT,           -- 1 = Giám thị 1, 2 = Giám thị 2
    so_thu_tu_ca INT,
    thoi_gian   DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Bảng lưu kết quả giám sát hành lang
CREATE TABLE IF NOT EXISTS giam_sat (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    ma_gv       VARCHAR(50)  NOT NULL,
    ho_ten      VARCHAR(200),
    tu_phong    VARCHAR(50),
    den_phong   VARCHAR(50),
    thoi_gian   DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

---

## 7. Luồng Hoạt Động Hệ Thống

```
CLIENT (GUI Swing)                          SERVER (GUI Swing + MySQL)
  │                                           │
  │                                           │  Khởi động → hiển thị IP LAN
  │                                           │  trên GUI (vd: 192.168.1.105:9999)
  │                                           │
  │  Người dùng nhập IP LAN của Server        │
  │  Nhập m=80, n=50                          │
  │  Browse chọn CANBOCOITHI.xlsx             │
  │  Browse chọn PHONGTHI.xlsx                │
  │  Bấm "Gửi lên Server"                     │
  │                                           │
  │──[4 bytes: int m]──────────────────────►  │
  │──[4 bytes: int n]──────────────────────►  │
  │──[4B len + bytes CANBOCOITHI.xlsx]─────►  │
  │──[4B len + bytes PHONGTHI.xlsx]────────►  │
  │                                           │ Nhận m, n
  │                                           │ Đọc 2 file xlsx
  │                                           │ Cắt: lấy m cán bộ đầu, n phòng đầu
  │                                           │ Phân công (ca 1 / ca tiếp theo)
  │                                           │ Ghi DANHSACHPHANCONG.xlsx
  │                                           │ Ghi DANHSACHGIAMSAT.xlsx
  │                                           │ Lưu KẾT QUẢ PHÂN CÔNG vào MySQL
  │◄─[4B len + bytes DANHSACHPHANCONG.xlsx]───│
  │◄─[4B len + bytes DANHSACHGIAMSAT.xlsx]────│
  │                                           │
  │  Người dùng chọn thư mục lưu kết quả      │
  │  Lưu 2 file ra đĩa máy client             │
```

---

## 8. Yêu Cầu Kỹ Thuật

| Tiêu chí | Yêu cầu |
|----------|---------|
| Ngôn ngữ | Java |
| Giao thức mạng | TCP (Java `Socket` / `ServerSocket`) |
| Giao diện | Swing GUI (cả Server lẫn Client) |
| Tham số đầu vào | m (số GV), n (số phòng) nhập trên GUI Client |
| File input | 2 file `.xlsx` — Client dùng **JFileChooser browse** chọn từ máy local |
| File output | 2 file `.xlsx` — Client dùng **JFileChooser chọn thư mục** lưu trên máy local |
| Cơ sở dữ liệu | MySQL — Server lưu **kết quả phân công** sau mỗi lần xử lý |
| IP LAN | Server **tự động hiển thị IP LAN** trên GUI; Client nhập IP đó để kết nối |
| Build tool | Maven |
| Package gốc | `com.example` |

---

## 9. Ví Dụ Minh Họa Logic Phân Công

**Giả sử:** m=14, n=6 → dùng 14 GV đầu và 6 phòng đầu (128→133)

**Ca 1:**
```
Phòng 128: GV01(GT1) + GV02(GT2)
Phòng 129: GV03(GT1) + GV04(GT2)
Phòng 130: GV05(GT1) + GV06(GT2)
Phòng 131: GV07(GT1) + GV08(GT2)
Phòng 132: GV09(GT1) + GV10(GT2)
Phòng 133: GV11(GT1) + GV12(GT2)
Hành lang : GV13(P128-P130), GV14(P131-P133)
```

**Ca 2** — áp dụng ràng buộc:
```
Phòng 128: ✗ GV01, GV02 (đã coi ca trước)  →  GV03(GT1) + GV05(GT2) ✓
Phòng 129: ✗ GV03, GV04                     →  GV01(GT1) + GV06(GT2) ✓
...
GV13, GV14 (hành lang ca 1) → đủ điều kiện vào làm giám thị phòng ca 2
```