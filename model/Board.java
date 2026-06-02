package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.io.Serializable;
import java.util.Random;

public class Board implements Serializable {
	private Cell[][] grid;
	private int rows;
	private int cols;
	private int mineCount;
	private int flagCount;
	private boolean firstMove = true;
	private GameState gameState;
	private int elapsedTime;
	private GameConfig config;
	private static final int SAVE_VERSION = 1;

	public Board(GameConfig config) {
		this.config = config;
		this.rows = config.getRows();
	    this.cols = config.getCols();
	    this.mineCount = config.getMineCount();

	    this.gameState = GameState.RUNNING;
	    this.elapsedTime = 0;

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

	public String exportData() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);

			// Serialize toàn bộ object Board
			oos.writeObject(this);
			oos.close();

			String data = Base64.getEncoder().encodeToString(baos.toByteArray());
			// Thêm version để tương thích sau này
			return SAVE_VERSION + "," + data;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Board importData(String input) {
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
			// Fix trạng thái nếu bị null sau khi deserialize
			if (board.getGameState() == null) {
				board.setGameState(GameState.RUNNING);
			}
			return board;
		} catch (Exception e) {
			System.err.println("Lỗi khi import save: " + e.getMessage());
			e.printStackTrace();
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
		}
	}

	public void toggleFlag(int r, int c) {
		if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c].isRevealed()) {
			return;
		}
		Cell cell = grid[r][c];
		cell.setFlagged(!cell.isFlagged());
		if (cell.isFlagged()) {
			flagCount++;
		} else {
			flagCount--;
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
	
}
