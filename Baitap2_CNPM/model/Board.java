package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 * 5/6/2026 - Mai Vũ Thành Hiển: Thêm thuộc tính config, xóa thuộc tính
 * difficulity cũng như các get, set của nó
 **/
public class Board implements Serializable {
	private Cell[][] grid;
	private int rows;
	private int cols;
	private int mineCount;
	private int flagCount;
	private boolean firstMove = true;
	private GameState gameState;
	private int elapsedTime;
	private transient GameStatistics statistics;

	private GameConfig config;
	private static final int SAVE_VERSION = 1;

	// Constructor board, dùng cho [1.1.2] cả "Tạo màn chơi khi đang trong ván game"
	// và "Tạo màn chơi khi đang ở start menu"
	public Board(GameConfig config) {
		this.config = config;
		this.rows = config.getRows();
		this.cols = config.getCols();
		this.mineCount = config.getMineCount();

		this.gameState = GameState.RUNNING;
		this.elapsedTime = 0;
		this.statistics = new GameStatistics();

		createBoard();
	}

	private void createBoard() {
		grid = new Cell[rows][cols];
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				grid[row][col] = new Cell();
			}
		}
	}
	public void chord(int r, int c) {
		// 3.5.1 Người chơi click chuột giữa vào ô đã mở có số
		if (gameState != GameState.RUNNING) {
			return;
		}
		
		if (r < 0 || r >= rows || c < 0 || c >= cols) {
			return;
		}

		Cell cell = grid[r][c];
		// 3.5.2 Kiểm tra điều kiện chording
		if (!cell.isRevealed() || cell.getNearbyMines() == 0 || cell.isMine()) {
			return;
		}

		int flaggedCount = countFlaggedNeighbors(r, c);
		// 3.4.3 Nếu số cờ đúng bằng số mìn lân cận → thực hiện chording
		if (flaggedCount == cell.getNearbyMines()) {
			int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
			int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};

			for (int i = 0; i < 8; i++) {
				int nr = r + dr[i];
				int nc = c + dc[i];
				if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
					continue;
				}
				Cell neighbor = grid[nr][nc];
				if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
					// 3.4.4 Gọi reveal cho từng ô lân cận
					reveal(nr, nc);
				}
			}
		}
	}


	public String exportData() {
		try {
			// 4.1.1. Tạo stream output
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// 4.1.2. Tạo ObjectOutputStream
			ObjectOutputStream oos = new ObjectOutputStream(baos);

			// 4.1.3. Serialize toàn bộ bảng
			oos.writeObject(this);
			oos.close();

			// 4.1.4. Encode Base64
			String data = Base64.getEncoder().encodeToString(baos.toByteArray());

			// 4.1.5. Thêm version
			return SAVE_VERSION + "," + data;

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Lỗi khi xuất dữ liệu game!\n" + e.getMessage(),
					"Lỗi Export", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	// 5.0.6. Board xử lý dữ liệu
	public static Board importData(String input) {
		// 5.2.1 Người chơi nhấn Cancel hoặc nhập chuỗi trống
		if (input == null || input.trim().isEmpty()) {
			return null;
		}
		try {

			String data = input.trim();
			int version = 1;
			// Hỗ trợ version
			if (data.contains(",")) {
				String[] parts = data.split(",", 2);
				version = Integer.parseInt(parts[0]);
				data = parts[1];
			}
			byte[] bytes = Base64.getDecoder().decode(data);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
			Board board = (Board) ois.readObject();
			ois.close();
			// 5.3.1. Fix trạng thái nếu bị null sau khi deserialize
			if (board.getGameState() == null) {
				board.setGameState(GameState.RUNNING);
			}
			return board;
			// Bắt ngoại lệ
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Lỗi khi nhập dữ liệu save!\n" + e.getMessage(),
					"Lỗi Import", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	public void increaseTime() {
		elapsedTime++;
	}

	public int getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(int elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public int getFlagCount() {
		return flagCount;
	}

	public void setFlagCount(int flagCount) {
		this.flagCount = flagCount;
	}

	public boolean isFirstMove() {
		return firstMove;
	}

	public void setFirstMove(boolean firstMove) {
		this.firstMove = firstMove;
	}

	public Cell[][] getGrid() {
		return grid;
	}

	public void setGrid(Cell[][] grid) {
		this.grid = grid;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public int getMineCount() {
		return mineCount;
	}

	public void setMineCount(int mineCount) {
		this.mineCount = mineCount;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
	/**
	 * UC_08_TS - Trả về đối tượng thống kê
	 */
	public GameStatistics getStatistics() {
		return statistics;
	}

	public void reveal(int r, int c) {

		Cell cell = grid[r][c];

		if (cell.isRevealed() || cell.isFlagged()) {
			return;
		}
		// 3.3 đến 3.4: Xử lí trong trường hợp ô nhấn là ô đầu tiên mở.
		if (firstMove) {
			// 3.3: Đặt mìn vào bàn chơi
			placeMines(r, c);
			// 3.4: Tính toán số mìn ở xung quanh
			calculateNearbyMines();
			firstMove = false;
		}
		// 3.5: Goi class Cell, đặt trạng thái được mở của ô thành true, tức ô được mở
		// và cho thấy nó là gì.
		cell.setRevealed(true);
		// :3.6 đến 3.7: Xử lí trong trường hợp ô là ô có mìn
		if (cell.isMine()) {
			cell.setExploded(true);
			// 3.6: Hiển thị tất cả mìn trong bàn chơi
			revealAllMines();
			// 3.7: Chuyển trạng thái của bàn chơi hiện tại thành LOSE, tức thua cuộc.
			gameState = GameState.LOSE;
			statistics.recordLoss();  // 8.3.1 Hệ thống gọi phương thức recordLoss()
			return;
		}
		// 3.8 đến 3.10: Xử lí trường hợp nếu ô rỗng và những ô xung quanh không có mìn
		if (!cell.isMine() && cell.getNearbyMines() == 0) {
			// 3.8: Thực hiện mở càng nhiều ô trống xung quanh càng tốt.
			floodFill(r, c);
		}
		// 3.9: Kiểm tra game đã thắng chưa
		if (checkWin()) {
			// 3.10: Nếu game đã thắng, đặt trạng thái của bàn chơi thành WIN, tức thắng
			// cuộc
			gameState = GameState.WIN;
			statistics.recordWin(); // 8.2.1 Hệ thống gọi phương thức recordWin()
		}
	}

	// ─── 4. ĐẶT CỜ (UC_04_DC) ─────────────────────────────────────────────
	public void handlePlaceFlag(int r, int c) {
		Cell cell = grid[r][c];
		// 4.0.5 Guard: chỉ đặt nếu ô chưa có cờ (tránh flagCount sai)
		if (cell.isFlagged()) return;
		// Giới hạn đặt cờ không vượt quá số mìn
		if (flagCount >= mineCount) return;
		// 4.0.6 Hệ thống thực hiện hành động đặt cờ vào ô đó.
		cell.setFlagged(true);
		// 4.0.7 Hệ thống tiến hành tăng số lượng cờ đã đặt trên bàn chơi.
		flagCount++;
		// 4.0.8 Hệ thống kiểm tra số lượng cờ còn lại.
		// 4.0.9 Hệ thống hoàn tất quá trình xử lý logic và trả kết quả về cho bộ điều
		// khiển.
	}

	// ─── 8. GỠ CỜ (UC_04_GC) ─────────────────────────────────────────────
	public void handleRemoveFlag(int r, int c) {
		Cell cell = grid[r][c];
		// 8.1.5 Guard: chỉ gỡ nếu ô đã có cờ (tránh flagCount âm)
		if (!cell.isFlagged()) return;
		// 8.1.6 Hệ thống thực hiện hành động gỡ bỏ cờ khỏi ô (Cell) đó.
		cell.setFlagged(false);
		// 8.1.7 Hệ thống tiến hành giảm số lượng cờ đã sử dụng xuống (tương đương với
		// việc tăng số lượng cờ còn lại).
		flagCount--;
		// 8.1.8 Hệ thống hoàn tất quá trình cập nhật trạng thái logic và trả kết quả về
		// cho bộ điều khiển.
	}

	// ─── 4. ĐẶT CỜ / 8. GỠ CỜ (UC_04_DC / UC_04_GC) ──────────────────────
	public void toggleFlag(int r, int c) {
		// 4.1.0 Tại bước 4.0.4, nếu ô được nhấp chuột phải là ô đã mở.
		if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c].isRevealed()) {
			// 4.1.1 Hệ thống bỏ qua sự kiện, không thực hiện bất kỳ hành động nào.
			return;
		}

		Cell cell = grid[r][c];

		// 4.0.4 Hệ thống kiểm tra trạng thái của ô (Cell) tương ứng để xem ô này đã có
		// cờ hay chưa.
		if (cell.isFlagged()) {
			// 4.2.0 Tại bước 4.0.4, nếu ô được nhấp chuột phải là ô đã có sẵn cờ.
			// 4.2.1 Hệ thống chuyển sang thực hiện Use Case "Gỡ cờ".
			handleRemoveFlag(r, c);
		} else {
			// 4.0.5 Hệ thống xác nhận trạng thái ô là chưa được đặt cờ.
			handlePlaceFlag(r, c);
		}
	}

	private void revealAllMines() {

		for (int row = 0; row < rows; row++) {

			for (int col = 0; col < cols; col++) {

				if (grid[row][col].isMine()) {

					grid[row][col].setRevealed(true);
				}
			}
		}
	}

	private boolean checkWin() {

		for (int row = 0; row < rows; row++) {

			for (int col = 0; col < cols; col++) {

				Cell cell = grid[row][col];

				if (!cell.isMine() && !cell.isRevealed()) {

					return false;
				}
			}
		}

		return true;
	}

	private void floodFill(int r, int c) {

		int[] dr = { -1, -1, -1, 0, 0, 1, 1, 1 };
		int[] dc = { -1, 0, 1, -1, 1, -1, 0, 1 };

		for (int i = 0; i < 8; i++) {

			int nr = r + dr[i];
			int nc = c + dc[i];

			if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {

				continue;
			}

			Cell neighbor = grid[nr][nc];

			if (neighbor.isRevealed() || neighbor.isMine() || neighbor.isFlagged()) {

				continue;
			}

			neighbor.setRevealed(true);

			if (neighbor.getNearbyMines() == 0) {

				floodFill(nr, nc);
			}
		}
	}

	private void calculateNearbyMines() {

		int[] dr = { -1, -1, -1, 0, 0, 1, 1, 1 };
		int[] dc = { -1, 0, 1, -1, 1, -1, 0, 1 };

		for (int row = 0; row < rows; row++) {

			for (int col = 0; col < cols; col++) {

				if (grid[row][col].isMine()) {
					continue;
				}

				int count = 0;

				for (int i = 0; i < 8; i++) {

					int nr = row + dr[i];
					int nc = col + dc[i];

					if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {

						continue;
					}

					if (grid[nr][nc].isMine()) {
						count++;
					}
				}

				grid[row][col].setNearbyMines(count);
			}
		}
	}

	private void placeMines(int safeRow, int safeCol) {
		// TODO Auto-generated method stub
		Random random = new Random();

		int placed = 0;

		while (placed < mineCount) {

			int row = random.nextInt(rows);
			int col = random.nextInt(cols);

			if (grid[row][col].isMine()) {
				continue;
			}

			if (Math.abs(row - safeRow) <= 1 && Math.abs(col - safeCol) <= 1) {

				continue;
			}

			grid[row][col].setMine(true);

			placed++;
		}
	}

	public int getRemainingMines() {
		if (mineCount - flagCount < 0 || firstMove) {
			return 0;
		} else
			return mineCount - flagCount;
	}

	public GameConfig getConfig() {
		return config;
	}

	/**
	 * Cung cấp gợi ý (Hint) cho người chơi bằng cách tự động mở một ô an toàn. Sử
	 * dụng logic suy luận cơ bản trước (nếu cờ xung quanh == số mìn của ô đã mở →
	 * các ô còn lại an toàn), sau đó fallback chọn ô an toàn bất kỳ (ưu tiên ô có
	 * số nhỏ).
	 */
	// ─── 9. GỢI Ý (UC_09_GH) ─────────────────────────────────────────────
	public boolean giveHint() {
		// 9.0.3 Bộ điều khiển gọi hàm xử lý tính toán gợi ý trên đối tượng bàn chơi
		// (Board).

		// 9.2.0 Tại bước 9.0.3, nếu ván chơi không ở trạng thái RUNNING hoặc bàn cờ
		// không còn ô an toàn nào khác để gợi ý.
		if (gameState != GameState.RUNNING) {
			// 9.2.1 Hệ thống chặn yêu cầu gợi ý, không thực hiện hành động và bỏ qua sự
			// kiện.
			// 9.2.2 Use case kết thúc.
			return false;
		}

		if (firstMove) {
			return false;
		}

		// 9.0.4 Hệ thống chạy thuật toán suy luận logic dựa trên thông số mìn lân cận
		// và cờ đã cắm để tìm ô an toàn 100%.
		int[] hintPos = findSafeHintPosition();
		// 9.2.0 Tại bước 9.0.3, nếu ván chơi không ở trạng thái RUNNING hoặc bàn cờ
		// không còn ô an toàn nào khác để gợi ý.
		if (hintPos == null) {
			// 9.2.1 Hệ thống chặn yêu cầu gợi ý, không thực hiện hành động và bỏ qua sự
			// kiện.
			// 9.2.2 Use case kết thúc.
			return false;
		}

		// 9.0.5 Hệ thống tìm thấy ô chắc chắn an toàn thông qua thuật toán suy luận và
		// trả tọa độ về cho bộ điều khiển.
		int r = hintPos[0];
		int c = hintPos[1];

		// 9.0.7 Bộ điều khiển phối hợp và kích hoạt cơ chế reveal hiện có để tự động mở
		// ô an toàn này.
		reveal(r, c);
		// 9.0.8 Hệ thống cập nhật trạng thái ô thành đã mở, kích hoạt lan truyền flood
		// fill (nếu ô trống), cập nhật bộ đếm/timer và kiểm tra điều kiện Thắng/Thua.

		return true;
	}

	/**
	 * Tìm một ô an toàn để gợi ý. Ưu tiên 1: Suy luận từ các ô số đã mở + cờ (logic
	 * cơ bản của người chơi giỏi). Ưu tiên 2: Bất kỳ ô chưa mở, chưa cắm cờ, không
	 * chứa mìn nào (đảm bảo an toàn tuyệt đối). Trong fallback ưu tiên ô có
	 * nearbyMines thấp (0 trước) để gợi ý có ích hơn.
	 */
	// ─── 9. THUẬT TOÁN TÌM Ô GỢI Ý (UC_09_GH) ───────────────────────────
	private int[] findSafeHintPosition() {
		// 9.0.4 Hệ thống chạy thuật toán suy luận logic dựa trên thông số mìn lân cận
		// và cờ đã cắm để tìm ô an toàn 100%.
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				Cell cell = grid[r][c];
				if (!cell.isRevealed() || cell.getNearbyMines() == 0) {
					continue;
				}

				int flaggedCount = countFlaggedNeighbors(r, c);
				if (flaggedCount == cell.getNearbyMines()) {
					int[] dr = { -1, -1, -1, 0, 0, 1, 1, 1 };
					int[] dc = { -1, 0, 1, -1, 1, -1, 0, 1 };
					for (int i = 0; i < 8; i++) {
						int nr = r + dr[i];
						int nc = c + dc[i];
						if (nr < 0 || nr >= rows || nc < 0 || nc >= cols)
							continue;
						Cell neighbor = grid[nr][nc];
						if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
							// 9.0.5 Hệ thống tìm thấy ô chắc chắn an toàn thông qua thuật toán suy luận và
							// trả tọa độ về cho bộ điều khiển.
							return new int[] { nr, nc };
						}
					}
				}
			}
		}

		// 9.1.0 Tại bước 9.0.4, nếu thuật toán suy luận logic không tìm ra ô chắc chắn
		// an toàn (do trạng thái bàn cờ bắt buộc phải đoán mò).
		// 9.1.1 Hệ thống chuyển sang thuật toán phụ quét toàn bộ bàn cờ để chọn ra một
		// ô an toàn ngẫu nhiên bất kỳ còn lại (chưa mở và chưa cắm cờ).
		int bestR = -1, bestC = -1;
		int bestNearby = 9;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				Cell cell = grid[r][c];
				if (!cell.isRevealed() && !cell.isFlagged() && !cell.isMine()) {
					int n = cell.getNearbyMines();
					if (n >= 1 && n < bestNearby) {
						bestNearby = n;
						bestR = r;
						bestC = c;
					}
				}
			}
		}
		if (bestR >= 0) {
			// 9.1.2 Hệ thống trả về tọa độ ô an toàn ngẫu nhiên này và tiếp tục luồng xử lý
			// từ bước 9.0.5.
			return new int[] { bestR, bestC };
		}
		bestNearby = 9;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				Cell cell = grid[r][c];
				if (!cell.isRevealed() && !cell.isFlagged() && !cell.isMine()) {
					if (cell.getNearbyMines() < bestNearby) {
						bestNearby = cell.getNearbyMines();
						bestR = r;
						bestC = c;
					}
				}
			}
		}
		if (bestR >= 0) {
			// 9.1.2 Hệ thống trả về tọa độ ô an toàn ngẫu nhiên này và tiếp tục luồng xử lý
			// từ bước 9.0.5.
			return new int[] { bestR, bestC };
		}

		return null;
	}

	/**
	 * Đếm số lượng ô lân cận đã được cắm cờ (dùng cho logic suy luận hint).
	 */
	private int countFlaggedNeighbors(int r, int c) {
		int count = 0;
		int[] dr = { -1, -1, -1, 0, 0, 1, 1, 1 };
		int[] dc = { -1, 0, 1, -1, 1, -1, 0, 1 };

		for (int i = 0; i < 8; i++) {
			int nr = r + dr[i];
			int nc = c + dc[i];
			if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
				if (grid[nr][nc].isFlagged()) {
					count++;
				}
			}
		}
		return count;
	}
}
