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

    public void toggleFlag(int r, int c) {
        // 4.1.0 Tại bước 4.0.4, nếu ô được nhấp chuột phải là ô đã mở → bỏ qua (luồng thay thế).
        if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c].isRevealed()) {
            return;
        }

        Cell cell = grid[r][c];

        // 4.0.4 / 8.1.4 Hệ thống kiểm tra trạng thái ô (Cell) xem đã có cờ hay chưa.
        if (cell.isFlagged()) {
            // 8.1.5 Hệ thống xác nhận ô đang có cờ.
            // 4.2.0 Tại bước 4.0.4 nếu ô đã có cờ → chuyển sang gỡ cờ.
            // 8.1.6 Hệ thống gỡ bỏ cờ khỏi ô (Cell).
            cell.setFlagged(false);
            // 8.1.7 Hệ thống giảm số lượng cờ đã sử dụng (tăng cờ còn lại).
            flagCount--;
        } else {
            // 4.0.5 Hệ thống xác nhận ô chưa có cờ.
            // 4.0.6 Hệ thống đặt cờ vào ô.
            cell.setFlagged(true);
            // 4.0.7 Hệ thống tăng số lượng cờ đã đặt trên bàn chơi.
            flagCount++;
        }

        // 4.0.8 Hệ thống kiểm tra số cờ còn lại (qua getRemainingMines).
        // 4.0.9 / 8.1.8 Hệ thống hoàn tất xử lý logic, trả kết quả về Controller.
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

        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};

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

        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};

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

    /**
     * Cung cấp gợi ý (Hint) cho người chơi bằng cách tự động mở một ô an toàn.
     * Sử dụng logic suy luận cơ bản trước (nếu cờ xung quanh == số mìn của ô đã mở → các ô còn lại an toàn),
     * sau đó fallback chọn ô an toàn bất kỳ (ưu tiên ô có số nhỏ).
     */
    public boolean giveHint() {
        // 9.0.3 Hệ thống kiểm tra trạng thái cho phép nhận gợi ý (phải RUNNING).
        if (gameState != GameState.RUNNING) {
            return false;
        }

        // 9.0.4 Tại bước 9.0.3, nếu firstMove → không đưa gợi ý (mìn chưa đặt).
        if (firstMove) {
            return false;
        }

        // 9.0.5 Hệ thống tìm vị trí ô gợi ý an toàn.
        int[] hintPos = findSafeHintPosition();
        if (hintPos == null) {
            // 9.1.0 Luồng thay thế: không còn ô an toàn để gợi ý.
            return false;
        }

        int r = hintPos[0];
        int c = hintPos[1];

        // 9.0.6 Hệ thống thực hiện mở ô gợi ý (tái sử dụng reveal để nhất quán flood/win/lose).
        reveal(r, c);

        // 9.0.7 Sau khi mở, trả kết quả thành công về Controller.
        return true;
    }

    /**
     * Tìm một ô an toàn để gợi ý.
     * Ưu tiên 1: Suy luận từ các ô số đã mở + cờ (logic cơ bản của người chơi giỏi).
     * Ưu tiên 2: Bất kỳ ô chưa mở, chưa cắm cờ, không chứa mìn nào (đảm bảo an toàn tuyệt đối).
     * Trong fallback ưu tiên ô có nearbyMines thấp (0 trước) để gợi ý có ích hơn.
     */
    private int[] findSafeHintPosition() {
        // 9.0.5.1 Quét các ô đã mở có nearbyMines > 0 để suy luận.
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = grid[r][c];
                if (!cell.isRevealed() || cell.getNearbyMines() == 0) {
                    continue;
                }

                int flaggedCount = countFlaggedNeighbors(r, c);
                // 9.0.5.2 Nếu số cờ xung quanh == số mìn lân cận → các ô chưa cờ chưa mở là AN TOÀN.
                if (flaggedCount == cell.getNearbyMines()) {
                    int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
                    int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
                    for (int i = 0; i < 8; i++) {
                        int nr = r + dr[i];
                        int nc = c + dc[i];
                        if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                        Cell neighbor = grid[nr][nc];
                        if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
                            // 9.0.5.3 Tìm được ô an toàn qua suy luận → trả về ngay.
                            return new int[]{nr, nc};
                        }
                    }
                }
            }
        }

        // 9.0.5.4 Fallback: chọn ô an toàn bất kỳ (không bao giờ gợi ý mìn).
        // Ưu tiên ô có nearby >=1 (hữu ích hơn) trước, sau đó mới nhận ô 0.
        int bestR = -1, bestC = -1;
        int bestNearby = 9;
        // Pass 1: ô có nearbyMines >=1
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
            return new int[]{bestR, bestC};
        }
        // Pass 2: chấp nhận ô 0
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
            return new int[]{bestR, bestC};
        }

        return null;
    }

    /**
     * Đếm số lượng ô lân cận đã được cắm cờ (dùng cho logic suy luận hint).
     */
    private int countFlaggedNeighbors(int r, int c) {
        int count = 0;
        int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};

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
