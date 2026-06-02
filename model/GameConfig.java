package model;

import java.io.Serializable;

public class GameConfig implements Serializable {
	private final int rows;
	private final int cols;
	private final int mineCount;

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
