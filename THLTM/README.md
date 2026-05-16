# README — Phân Công Cán Bộ Coi Thi
## Java + Swing GUI + TCP Client-Server + MySQL
### Package: `com.example` | Build: Maven

---

## 1. Cấu Trúc Thư Mục

Từ project Maven hiện tại của bạn (`com.example`), tạo thêm các package và file sau:

```
THLTM/exam-assignment/
│
├── .mvn/                          ← Maven wrapper (giữ nguyên)
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── example/
│                   │
│                   ├── model/                    ← Tạo package này
│                   │   ├── CanBo.java
│                   │   ├── PhongThi.java
│                   │   ├── PhanCong.java
│                   │   └── AssignmentResult.java
│                   │
│                   ├── logic/                    ← Tạo package này
│                   │   └── AssignmentLogic.java
│                   │
│                   ├── util/                     ← Tạo package này
│                   │   ├── ExcelHandler.java
│                   │   └── DatabaseManager.java
│                   │
│                   ├── server/                   ← Tạo package này
│                   │   ├── ServerMain.java
│                   │   ├── ServerGUI.java
│                   │   └── ClientHandler.java
│                   │
│                   └── client/                   ← Tạo package này
│                       ├── ClientMain.java
│                       ├── ClientGUI.java
│                       └── NetworkClient.java
│
│   (App.java mặc định — có thể xóa hoặc giữ, không ảnh hưởng)
│
├── target/                        ← Maven tự tạo khi build
| 
|
│
└── pom.xml                        ← Cập nhật theo hướng dẫn bên dưới
```

---

## 2. Configuration

### 2.1. Tạo Database

> **MySQL lưu gì?** Chỉ lưu **kết quả phân công** (sau khi server xử lý xong),
> KHÔNG lưu dữ liệu gốc từ file `.xlsx`. Cụ thể:
> - Bảng `phan_cong`: GV nào coi phòng nào, vai trò GT1/GT2, ca thi nào
> - Bảng `giam_sat`: GV nào giám sát hành lang, phạm vi phòng nào

```sql
-- Tạo database
CREATE DATABASE IF NOT EXISTS exam_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE exam_db;

-- Bảng lưu kết quả phân công giám thị (sau khi server xử lý)
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

### 2.2. Tải thư viện từ `pom.xml`

Chạy lệnh sau để tải dependencies:
```bash
cd exam-assignment
mvn dependency:resolve
```

---

## 3. Giao Thức Truyền TCP (Protocol)

```
CLIENT → SERVER:
  [4 bytes writeInt: m]
  [4 bytes writeInt: n]
  [4 bytes writeInt: độ dài file canbo] [n bytes: nội dung file canbo.xlsx]
  [4 bytes writeInt: độ dài file phong] [n bytes: nội dung file phong.xlsx]

SERVER → CLIENT:
  [4 bytes writeInt: độ dài file phancong] [n bytes: nội dung DANHSACHPHANCONG.xlsx]
  [4 bytes writeInt: độ dài file giamsat]  [n bytes: nội dung DANHSACHGIAMSAT.xlsx]
```

---

## 4. Cách Chạy Trên 1 Máy

**Terminal 1 — Chạy Server:**
```bash
mvn exec:java -Dexec.mainClass="com.example.server.ServerMain"
```

**Terminal 2 — Chạy Client**:
```bash
mvn exec:java -Dexec.mainClass="com.example.client.ClientMain"
```

---

## 5. Chạy Với 2 Máy

**Máy Server (máy A):**
```bash
mvn exec:java -Dexec.mainClass="com.example.server.ServerMain"
```

> Server GUI sẽ **tự động hiển thị IP LAN** ở thanh trên cùng (nổi bật, chữ trắng nền xanh).
> Ví dụ: `IP LAN: 192.168.1.105 | Port: 9999`
> Không cần chạy `ipconfig` thủ công nữa.

**Máy Client (máy B):**
1. Chạy ClientGUI
2. Ô **Host**: nhập IP hiển thị trên Server GUI (ví dụ `192.168.1.105`)
3. Nhập m, n → browse chọn 2 file `.xlsx` trên máy client
4. Bấm **"Gửi lên Server và Nhận kết quả"**
5. Chọn thư mục lưu file kết quả trên máy client

> **Lưu ý:** Hai máy phải cùng mạng LAN. Nếu bị lỗi kết nối, tắt Firewall trên máy Server:
> Windows: Settings → Windows Defender Firewall → Turn off
