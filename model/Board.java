package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.io.Serializable;
import java.util.Random;

public class Board {
	private Cell[][] grid;
	private int rows;
	private int cols;
	private int mineCount;
	private int flagCount;
	private boolean firstMove = true;
	private GameState gameState;
	private int elapsedTime;
	private Difficulty difficulty;
	private static final int SAVE_VERSION = 1;

	public Board(Difficulty difficulty) {
		this.difficulty = difficulty;
		this.rows = difficulty.getRows();
		this.cols = difficulty.getCols();
		this.mineCount = (int) (rows * cols * difficulty.getMineDensity());
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
	        
	        oos.writeObject(this);
	        oos.close();

	        String data = Base64.getEncoder().encodeToString(baos.toByteArray());
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
	        if (data.contains(",")) {
	            String[] parts = data.split(",", 2);
	            version = Integer.parseInt(parts[0]);
	            data = parts[1];
	        }
	        byte[] bytes = Base64.getDecoder().decode(data);
	        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
	        Board board = (Board) ois.readObject();
	        ois.close();
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

		if (firstMove) {

			placeMines(r, c);

			calculateNearbyMines();

			firstMove = false;
		}

		cell.setRevealed(true);
		if (cell.isMine()) {
			cell.setExploded(true);
			revealAllMines();
			gameState = GameState.LOSE;
			return;
		}

		if (!cell.isMine() && cell.getNearbyMines() == 0) {

			floodFill(r, c);
		}
		if (checkWin()) {

			gameState = GameState.WIN;
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
	public Difficulty getDifficulty() {
	    return difficulty;
	}
}
