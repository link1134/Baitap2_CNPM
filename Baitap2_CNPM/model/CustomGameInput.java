package model;

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