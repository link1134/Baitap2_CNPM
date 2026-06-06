package model;

/**
 * Board quản lý dữ liệu bàn chơi.
 * Khi tạo Board mới, hệ thống sẽ lấy thông tin độ khó,
 * khởi tạo số dòng, số cột, số mìn, trạng thái game và thời gian.
 */
public class Board {

    private int rows;
    private int cols;
    private int mineCount;
    private int elapsedTime;

    private Difficulty difficulty;
    private GameState gameState;
    private Cell[][] cells;

    public Board(Difficulty difficulty) {
        this.difficulty = difficulty;

        // Lấy kích thước bàn từ độ khó
        this.rows = difficulty.getRows();
        this.cols = difficulty.getCols();

        // Tính số mìn dựa trên mật độ mìn
        this.mineCount = (int) (rows * cols * difficulty.getMineDensity());

        // Khi tạo ván mới, trạng thái game về RUNNING
        this.gameState = GameState.RUNNING;

        // Reset thời gian về 0
        this.elapsedTime = 0;

        // Tạo ma trận ô
        createBoard();
    }

    private void createBoard() {
        cells = new Cell[rows][cols];

        // Khởi tạo từng ô trong bàn chơi
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    public Difficulty getDifficulty() {
        return difficulty;
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

    public int getElapsedTime() {
        return elapsedTime;
    }

    public GameState getGameState() {
        return gameState;
    }
}
