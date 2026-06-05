package model;

public enum Difficulty {

    EASY(9, 9, 0.12),
    MEDIUM(16, 16, 0.16),
    HARD(16, 30, 0.20);

    private final int rows;
    private final int cols;
    private final double mineDensity;

    Difficulty(int rows, int cols, double mineDensity) {

        this.rows = rows;
        this.cols = cols;
        this.mineDensity = mineDensity;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public double getMineDensity() {
        return mineDensity;
    }
}