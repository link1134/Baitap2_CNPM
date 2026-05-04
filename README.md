# BUSINESS REQUIREMENT DOCUMENT (BRD)
## Dự án: Trò chơi gỡ mìn (Minesweeper)

---

## 1. Giới thiệu (Introduction)
### 1.1 Mục đích
Tài liệu này mô tả các yêu cầu nghiệp vụ chi tiết cho việc phát triển ứng dụng trò chơi Gỡ mìn (Minesweeper). Đây là cơ sở để nhóm phát triển xây dựng logic trò chơi và giao diện người dùng.

### 1.2 Phạm vi
Hệ thống cho phép người dùng:
- Chơi trò chơi gỡ mìn trên bàn cờ dạng lưới.
- Chọn các mức độ khó khác nhau hoặc tùy chỉnh kích thước.
- Theo dõi thời gian thực và lưu trữ thành tích cá nhân.

---

## 2. Mục tiêu nghiệp vụ (Business Objectives)
- **Giải trí:** Cung cấp trải nghiệm chơi game mượt mà, trực quan.
- **Tư duy:** Giúp người chơi rèn luyện khả năng suy luận logic.
- **Kỹ thuật:** Minh họa việc áp dụng lập trình hướng đối tượng (OOP) và xử lý sự kiện trong Java Swing.

---

## 3. Phạm vi hệ thống (Scope)

### 3.1 Trong phạm vi (In Scope)
- Khởi tạo bàn cờ và rải mìn ngẫu nhiên.
- Các mức độ khó: Easy, Medium, Hard và Custom.
- Cơ chế mở ô, cắm cờ và mở nhanh (Chording).
- Hiển thị bộ đếm thời gian và số mìn còn lại.
- Lưu trữ Bảng xếp hạng (Highscores) cục bộ.

### 3.2 Ngoài phạm vi (Out of Scope)
- Chế độ chơi Online/Multiplayer.
- Hệ thống tài khoản người dùng trực tuyến.
- Hiệu ứng âm thanh và nhạc nền nâng cao.

---

## 4. Yêu cầu nghiệp vụ chi tiết (Business Requirements)

| ID | Yêu cầu | Mô tả |
| :--- | :--- | :--- |
| **BR-01** | Khởi tạo game | Hệ thống cho phép bắt đầu ván mới qua nút Reset (Mặt cười) bất cứ lúc nào. |
| **BR-02** | Chọn độ khó | Hệ thống cung cấp các mức độ: Easy, Medium, Hard và Custom (tự nhập dòng/cột). |
| **BR-03** | Mở ô | Click chuột trái để mở ô. Nếu trúng mìn -> Thua; Nếu không -> Hiện số mìn lân cận. |
| **BR-04** | Đặt cờ | Click chuột phải để đánh dấu ô nghi ngờ có mìn. |
| **BR-05** | Hiển thị thông tin | Hiển thị thời gian chơi (Timer) và số mìn còn lại (Mine Counter). |
| **BR-06** | Kết thúc | Thắng khi mở hết ô không chứa mìn. Thua khi click trúng mìn. |
| **BR-07** | Lưu kỷ lục | Hệ thống lưu tên và thời gian của Top 10 người chơi nhanh nhất. |

---

## 5. Thông số độ khó (Difficulty Specifications)

| Cấp độ | Kích thước (R x C) | Số lượng mìn |
| :--- | :--- | :--- |
| **Easy** | 9 x 9 | 10 |
| **Medium** | 16 x 16 | 40 |
| **Hard** | 16 x 30 | 99 |
| **Custom** | Tùy chỉnh (Max 24x30) | Tối thiểu 10 |

---

## 6. Luật nghiệp vụ (Business Rules)

1.  **Quy tắc click đầu tiên an toàn:** Ô đầu tiên người chơi click mở luôn phải là ô trống (không có mìn). Hệ thống sẽ rải mìn sau khi người chơi thực hiện thao tác click đầu tiên.
2.  **Cơ chế mở lan (Flood Fill):** Nếu ô mở ra có 0 mìn xung quanh, hệ thống tự động mở tất cả các ô lân cận cho đến khi chạm vào ô có số.
3.  **Tính năng Chording:** Khi click đúp vào một ô số đã được cắm đủ số cờ xung quanh tương ứng, hệ thống tự động mở các ô còn lại xung quanh ô đó.
4.  **Timer:** Bộ đếm thời gian bắt đầu chạy từ giây thứ 1 ngay sau cú click đầu tiên và dừng lại khi kết thúc ván đấu.

---

## 7. Yêu cầu phi chức năng (Non-functional Requirements)

- **Hiệu năng:** Thời gian phản hồi sau mỗi thao tác click phải nhỏ hơn 0.1 giây.
- **Giao diện (UI):** Sử dụng các biểu tượng (Icon) trực quan cho mìn và cờ. Màu sắc các con số phải phân biệt rõ ràng (1: Blue, 2: Green, 3: Red...).
- **Ổn định:** Không xảy ra lỗi treo ứng dụng khi thực hiện thuật toán mở lan trên bàn cờ kích thước lớn.

---

## 8. Dữ liệu hệ thống (Data Requirements)

### 8.1 Cấu trúc Ô (Cell)
- `isMine` (boolean): Trạng thái có mìn.
- `isRevealed` (boolean): Đã lật mở hay chưa.
- `isFlagged` (boolean): Đã cắm cờ hay chưa.
- `adjacentMines` (int): Số lượng mìn xung quanh (0-8).

### 8.2 Bảng xếp hạng (Highscore)
- `playerName` (String).
- `completionTime` (int - giây).
- `difficulty` (String).

---

## 9. Tiêu chí chấp nhận (Acceptance Criteria)
- Ứng dụng khởi động bình thường trên các máy tính có cài Java JRE 8+.
- Thực hiện đúng các luật chơi (Mở lan, An toàn click đầu, Chording).
- Ghi nhận và hiển thị đúng thông tin bảng xếp hạng sau khi thắng cuộc.