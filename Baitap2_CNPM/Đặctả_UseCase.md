# Đặc tả Use Case

## 1. UC_04_Export — EXPORT GAME

### Luồng chính (Main Flow)

| Bước   | Đặt tả                                                                 | File                  | Dòng     | Code |
|--------|------------------------------------------------------------------------|-----------------------|----------|------|
| 4.0.1  | Người chơi nhấn vào mục **Export** trong menu Game                     | `BoardView.java`      | ~241     | `exportItem = new JMenuItem("Export");` |
| 4.0.2  | BoardController nhận sự kiện từ menu Export                            | `BoardController.java`| 120      | `bv.getExportItem().addActionListener(...)` |
| 4.0.3  | Lấy trạng thái game hiện tại                                           | `BoardController.java`| 123      | `GameState oldState = b.getGameState();` |
| 4.0.4  | Nếu game đang RUNNING thì tạm chuyển sang PAUSE                        | `BoardController.java`| 125      | `if (oldState == GameState.RUNNING)` |
| 4.0.5  | Gọi hàm `exportData()` trên đối tượng Board                            | `BoardController.java`| 129      | `String data = b.exportData();` |
| 4.0.6  | Khôi phục lại trạng thái game cũ                                       | `BoardController.java`| 131      | `b.setGameState(oldState);` |
| 4.0.7  | Tạo JTextArea chứa chuỗi save                                          | `BoardController.java`| 133      | `JTextArea area = new JTextArea(data);` |
| 4.0.8  | Hiển thị chuỗi save cho người chơi                                     | `BoardController.java`| 137      | `JOptionPane.showMessageDialog(...)` |

### Phương thức chính

| Phương thức          | File             | Dòng   | Vai trò |
|----------------------|------------------|--------|--------|
| `exportData()`       | `Board.java`     | 78     | Serialize Board thành chuỗi Base64 + version |
| ActionListener (Export) | `BoardController.java` | 120 | Xử lý toàn bộ luồng Export Game |

---

## 2. UC_05_Import — IMPORT GAME

### Luồng chính (Main Flow)

| Bước   | Đặt tả                                                                 | File                  | Dòng     | Code |
|--------|------------------------------------------------------------------------|-----------------------|----------|------|
| 5.0.1  | Người chơi nhấn vào mục **Import** trong menu Game                     | `BoardView.java`      | ~240     | `importItem = new JMenuItem("Import");` |
| 5.0.2  | Hệ thống (BoardView) tiếp nhận sự kiện và chuyển cho BoardController   | `BoardController.java`| 148      | `bv.getImportItem().addActionListener(...)` |
| 5.0.3  | Hệ thống hiển thị dialog nhập chuỗi save                               | `BoardController.java`| 153      | `JOptionPane.showInputDialog(...)` |
| 5.0.4  | Người chơi nhập chuỗi save và nhấn OK                                  | `BoardController.java`| 153      | `String data = ...` |
| 5.0.5  | BoardController gọi `Board.importData(String)`                         | `BoardController.java`| 159      | `Board importedBoard = Board.importData(...)` |
| 5.0.6  | Board xử lý deserialize dữ liệu                                        | `Board.java`          | 104      | `public static Board importData(String input)` |
| 5.0.7  | Kiểm tra dữ liệu import hợp lệ                                         | `BoardController.java`| 160      | `if (importedBoard == null)` |
| 5.0.8  | Dừng timer hiện tại                                                    | `BoardController.java`| 166      | `if (timer != null) timer.stop();` |
| 5.0.9  | Đóng BoardView cũ                                                      | `BoardController.java`| 169      | `bv.dispose();` |
| 5.0.10 | Tạo BoardView mới từ Board đã import                                   | `BoardController.java`| 171      | `BoardView newView = new BoardView(importedBoard);` |
| 5.0.11 | Tạo BoardController mới                                                | `BoardController.java`| 173      | `new BoardController(newView, importedBoard);` |
| 5.0.12 | Xử lý giao diện theo trạng thái game được lưu                          | `BoardController.java`| 175-183  | Xử lý PAUSE / Hint button |
| 5.0.13 | Cập nhật UI (board, thời gian, số mìn)                                 | `BoardController.java`| 185-190  | `updateView()` + set label |

### Luồng thay thế (Alternative Flow)

| Bước   | Đặt tả                                      | File                  | Dòng     | Code |
|--------|---------------------------------------------|-----------------------|----------|------|
| 5.1.0  | Dữ liệu save không hợp lệ                   | `BoardController.java`| 160      | `if (importedBoard == null)` |
| 5.1.1  | Hiển thị thông báo lỗi                      | `BoardController.java`| 162      | `JOptionPane.showMessageDialog(...)` |
| 5.1.2  | Use case kết thúc                           | `BoardController.java`| 164      | `return;` |
| 5.2.0  | Người chơi nhấn Cancel hoặc nhập chuỗi trống | `BoardController.java`| 154      | `if (data == null || data.trim().isBlank())` |
| 5.2.1  | Kết thúc use case, không thực hiện import   | `BoardController.java`| 156      | `return;` |

### Phương thức chính

| Phương thức              | File             | Dòng   | Vai trò |
|--------------------------|------------------|--------|--------|
| `importData(String)`     | `Board.java`     | 104    | Deserialize chuỗi save thành đối tượng Board |
| `exportData()`           | `Board.java`     | 78     | Tạo chuỗi save (phương thức đối ứng) |
| ActionListener (Import)  | `BoardController.java` | 148 | Xử lý toàn bộ luồng Import Game |
