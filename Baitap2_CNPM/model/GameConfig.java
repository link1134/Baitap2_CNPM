package model;

import java.io.Serializable;

/**
 * Mai Vũ Thành Hiển - 5/6/2026: Tạo class Gameconfig
 **/
public class GameConfig implements Serializable {
	private final int rows;
	private final int cols;
	private final int mineCount;

	// Constructor GameConfig, dùng cho [2.2.5]/[2.1.4]
	public GameConfig(int rows, int cols, int mineCount) {
		this.rows = rows;
		this.cols = cols;
		this.mineCount = mineCount;
	}

	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}

	public int getMineCount() {
		return mineCount;
	}
}
