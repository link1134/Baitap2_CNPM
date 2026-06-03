# 🗺️ Mapping Đặc tả Use Case ↔ Code nguồn

> File này đối chiếu từng bước trong tài liệu đặc tả Use Case với vị trí code tương ứng.
> Dùng để dễ dàng kiểm tra, review và bảo trì.

---

## 1. UC_04_DC — ĐẶT CỜ (Place Flag)

### Luồng chính (Main Flow)

| Bước | Đặc tả | File | Dòng | Code |
|------|--------|------|------|------|
| 4.0.1 | Người chơi nhấp chuột phải vào một ô | `BoardController.java` | 221 | `// 4.0.1 / 8.1.1 Người chơi thực hiện...` |
| 4.0.2 | BoardView tiếp nhận sự kiện → gửi đến Controller | `BoardController.java` | 222-223 | `// 4.0.2 / 8.1.2 Hệ thống (BoardView)...` |
| 4.0.3 | Controller gọi Board.xử lý sự kiện | `BoardController.java` | 224-225 | `// 4.0.3 / 8.1.3 ...` → `b.toggleFlag(r, c)` |
| 4.0.4 | Board kiểm tra trạng thái ô đã có cờ chưa | `Board.java` | 232 | `// 4.0.4 ...` → `if (cell.isFlagged())` |
| 4.0.5 | Xác nhận ô chưa có cờ | `Board.java` | 191 | Header comment của `handlePlaceFlag()` |
| 4.0.6 | Đặt cờ vào ô | `Board.java` | 198 | `cell.setFlagged(true)` |
| 4.0.7 | Tăng số lượng cờ đã đặt | `Board.java` | 200 | `flagCount++` |
| 4.0.8 | Kiểm tra số cờ còn lại | `Board.java` | 194, 202 | Header + inline comment |
| 4.0.9 | Hoàn tất, trả kết quả về Controller | `Board.java` | 195, 202 | Header + inline comment |
| 4.0.10 | Controller yêu cầu cập nhật giao diện | `BoardController.java` | 228-229 | `// 4.0.10 ...` → `bv.refreshBoard()` |

### Luồng thay thế (Alternative Flow)

| Bước | Đặc tả | File | Dòng | Code |
|------|--------|------|------|------|
| **4.1.0** | Ô được nhấp là ô **đã mở** | `Board.java` | 221, 224 | `// 4.1.0 ...` → `if (...isRevealed())` |
| 4.1.1 | Bỏ qua sự kiện, không làm gì | `Board.java` | 226 | `return;` |
| **4.2.0** | Ô được nhấp là ô **đã có cờ** | `Board.java` | 222, 234 | `// 4.2.0 ...` → `if (cell.isFlagged())` |
| 4.2.1 | Chuyển sang Use Case **Gỡ cờ** | `Board.java` | 235-236 | `// 4.2.1 ...` → `handleRemoveFlag()` |

### Phương thức chính

| Phương thức | File | Dòng | Vai trò |
|-------------|------|------|---------|
| `toggleFlag()` | `Board.java` | 223 | Dispatcher: kiểm tra + điều hướng sang Đặt cờ / Gỡ cờ |
| `handlePlaceFlag()` | `Board.java` | 196 | Xử lý logic Đặt cờ (UC_04_DC) |

---

## 2. UC_04_GC — GỠ CỜ (Remove Flag)

### Luồng chính (Main Flow)

| Bước | Đặc tả | File | Dòng | Code |
|------|--------|------|------|------|
| 8.1.1 | Người chơi nhấp chuột phải vào ô **đang có cờ** | `BoardController.java` | 221 | `// 4.0.1 / 8.1.1 ...` |
| 8.1.2 | BoardView tiếp nhận sự kiện → gửi đến Controller | `BoardController.java` | 222-223 | `// 4.0.2 / 8.1.2 ...` |
| 8.1.3 | Controller gọi Board.xử lý sự kiện | `BoardController.java` | 224-225 | `// 4.0.3 / 8.1.3 ...` → `b.toggleFlag()` |
| 8.1.4 | Board kiểm tra trạng thái ô đã có cờ chưa | `Board.java` | 232 | `// 4.0.4 ...` → `if (cell.isFlagged())` |
| 8.1.5 | Cell xác nhận đang có cờ | `Board.java` | 207 | Header comment của `handleRemoveFlag()` |
| 8.1.6 | Gỡ bỏ cờ khỏi ô | `Board.java` | 213 | `cell.setFlagged(false)` |
| 8.1.7 | Giảm số cờ đã sử dụng (tăng cờ còn lại) | `Board.java` | 215 | `flagCount--` |
| 8.1.8 | Hoàn tất, trả kết quả về Controller | `Board.java` | 210, 217 | Header + inline comment |
| 8.1.9 | Controller yêu cầu cập nhật giao diện | `BoardController.java` | 230-231 | `// 8.1.9 ...` → `bv.refreshBoard()` |

### Luồng thay thế (Alternative Flow)

| Bước | Đặc tả | File | Dòng | Code |
|------|--------|------|------|------|
| **8.2.0** | Ván chơi đã kết thúc (thắng / thua) | `BoardController.java` | 206 | `// 8.2.0 ...` → `if (gameState != RUNNING)` |
| 8.2.1 | Chặn sự kiện, giữ nguyên hiện trạng | `BoardController.java` | 208 | `// 8.2.1 ...` → `return;` |

### Phương thức chính

| Phương thức | File | Dòng | Vai trò |
|-------------|------|------|---------|
| `handleRemoveFlag()` | `Board.java` | 211 | Xử lý logic Gỡ cờ (UC_04_GC) |

---

## 3. UC_09_GH — GỢI Ý (Hint)

### Luồng chính (Main Flow)

| Bước | Đặc tả | File | Dòng | Code |
|------|--------|------|------|------|
| 9.0.1 | Người chơi nhấn nút **Hint** | `BoardController.java` | 82 | `// 9.0.1 ...` |
| 9.0.2 | BoardView tiếp nhận → gửi đến Controller | `BoardController.java` | 83-84 | `// 9.0.2 ...` |
| 9.0.3 | Controller gọi Board.xử lý gợi ý | `BoardController.java` | 91-92 | `// 9.0.3 ...` → `b.giveHint()` |
| 9.0.4 | Board chạy thuật toán suy luận tìm ô an toàn | `Board.java` | 397, 424 | `// 9.0.4 ...` → `findSafeHintPosition()` |
| 9.0.5 | Tìm thấy ô an toàn, trả tọa độ về Controller | `Board.java` | 404, 444 | `// 9.0.5 ...` → `return new int[]{nr, nc}` |
| 9.0.6 | Controller tiếp nhận tọa độ ô gợi ý | `BoardController.java` | 93 | `// 9.0.6 ...` |
| 9.0.7 | Controller kích hoạt reveal để mở ô an toàn | `Board.java` | 408 | `// 9.0.7 ...` → `reveal(r, c)` |
| 9.0.8 | Cập nhật trạng thái ô, flood fill, check Win/Lose | `Board.java` | 410-411 | `// 9.0.8 ...` |
| 9.0.9 | Hoàn tất, cập nhật toàn bộ giao diện | `BoardController.java` | 95-97 | `// 9.0.9 ...` → `bv.refreshBoard()` + `updateBombUI()` |

### Luồng thay thế (Alternative Flow)

| Bước | Đặc tả | File | Dòng | Code |
|------|--------|------|------|------|
| **9.1.0** | Không tìm ra ô an toàn (phải đoán mò) | `Board.java` | 452 | `// 9.1.0 ...` (fallback) |
| 9.1.1 | Chuyển sang thuật toán phụ quét ô an toàn ngẫu nhiên | `Board.java` | 454-455 | `// 9.1.1 ...` |
| 9.1.2 | Trả tọa độ ô an toàn ngẫu nhiên | `Board.java` | 474, 492 | `// 9.1.2 ...` → `return new int[]{...}` |
| **9.2.0** | Ván chơi không RUNNING / không còn ô an toàn | `Board.java` + `BoardController.java` | 386, 400, 496; 85 | `// 9.2.0 ...` |
| 9.2.1 | Chặn yêu cầu, bỏ qua | `Board.java` | 388; `BoardController.java` | 87 | `// 9.2.1 ...` |
| 9.2.2 | Use case kết thúc | `Board.java` | 389; `BoardController.java` | 88 | `// 9.2.2 ...` |

### Phương thức chính

| Phương thức | File | Dòng | Vai trò |
|-------------|------|------|---------|
| `giveHint()` | `Board.java` | 384 | Entry point xử lý gợi ý |
| `findSafeHintPosition()` | `Board.java` | 423 | Thuật toán tìm ô an toàn (suy luận → fallback) |
| `countFlaggedNeighbors()` | `Board.java` | 502 | Đếm cờ lân cận (hỗ trợ suy luận) |

---

## 4. Sơ đồ luồng gọi (Call flow)

```
┌─────────────────────────────────────────────────────┐
│                   BoardView                         │
│  (JButton + MouseAdapter/ActionListener)             │
└──────────┬──────────────────────────┬───────────────┘
           │ mousePressed (right)     │ actionPerformed (Hint)
           ▼                          ▼
┌──────────────────────┐  ┌───────────────────────────┐
│   BoardController    │  │     BoardController        │
│  .mousePressed()     │  │  .actionPerformed(Hint)    │
│  dòng 205             │  │  dòng 80                   │
└──────────┬───────────┘  └──────────┬────────────────┘
           │ b.toggleFlag(r,c)       │ b.giveHint()
           ▼                          ▼
┌──────────────────────┐  ┌───────────────────────────┐
│   Board.toggleFlag() │  │   Board.giveHint()         │
│   dòng 223             │  │   dòng 384                 │
│                        │  │                            │
│  ├ 4.1.0: .isRevealed()│  │  ├ 9.2.0: check RUNNING  │
│  ├ 4.0.4: .isFlagged()│  │  ├ 9.0.4: findSafeHint()  │
│  │  ├ true → handle..│  │  ├ 9.0.7: reveal()        │
│  │  └ false → handle │  │  └ return true/false       │
│  └──                   │  └───────────────────────────┘
│                        │
│  ┌──────────────┐      │
│  │handlePlace() │      │
│  │dòng 196       │      │
│  │4.0.5→4.0.9   │      │
│  └──────────────┘      │
│  ┌──────────────┐      │
│  │handleRemove()│      │
│  │dòng 211       │      │
│  │8.1.5→8.1.8   │      │
│  └──────────────┘      │
└─────────────────────────┘
```

---

## 5. Thống kê

| Use Case | Số bước MF | Số bước AF | File chính | SLOC comments |
|----------|-----------|-----------|------------|--------------|
| UC_04_DC (Đặt cờ) | 10 | 2+2 | `Board.java` + `BoardController.java` | ~18 dòng |
| UC_04_GC (Gỡ cờ) | 9 | 2 | `Board.java` + `BoardController.java` | ~15 dòng |
| UC_09_GH (Gợi ý) | 9 | 5+3 | `Board.java` + `BoardController.java` | ~25 dòng |
| **Tổng** | **28** | **14** | 2 files | ~58 dòng |

> **Ghi chú:**
> - MF = Main Flow (luồng chính), AF = Alternative Flow (luồng thay thế)
> - Các bước 4.0.1→4.0.3 và 8.1.1→8.1.3 dùng chung code (cùng hành động nhấp chuột phải)
> - File `BoardView.java` chịu trách nhiệm giao diện (không có comment use case riêng)
