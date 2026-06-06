package model;

/**
 * 5/6/2026 - Mai Vũ Thành Hiển: Tạo class CustomGameInput.
 **/
public class CustomGameInput {
	private int rows;
	private int cols;
	private int mines;

	public CustomGameInput(int rows, int cols, int mines) {
		this.rows = rows;
		this.cols = cols;
		this.mines = mines;
	}

	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}

	public int getMines() {
		return mines;
	}
}