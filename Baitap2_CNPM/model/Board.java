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

    // ─── 4. ĐẶT CỜ (UC_04_DC) ─────────────────────────────────────────────
    public void handlePlaceFlag(int r, int c) {
        Cell cell = grid[r][c];
        // 4.0.6 Hệ thống thực hiện hành động đặt cờ vào ô đó.
        cell.setFlagged(true);
        // 4.0.7 Hệ thống tiến hành tăng số lượng cờ đã đặt trên bàn chơi.
        flagCount++;
        // 4.0.8 Hệ thống kiểm tra số lượng cờ còn lại.
        // 4.0.9 Hệ thống hoàn tất quá trình xử lý logic và trả kết quả về cho bộ điều khiển.
    }

    // ─── 8. GỠ CỜ (UC_04_GC) ─────────────────────────────────────────────
    public void handleRemoveFlag(int r, int c) {
        Cell cell = grid[r][c];
        // 8.1.6 Hệ thống thực hiện hành động gỡ bỏ cờ khỏi ô (Cell) đó.
        cell.setFlagged(false);
        // 8.1.7 Hệ thống tiến hành giảm số lượng cờ đã sử dụng xuống (tương đương với việc tăng số lượng cờ còn lại).
        flagCount--;
        // 8.1.8 Hệ thống hoàn tất quá trình cập nhật trạng thái logic và trả kết quả về cho bộ điều khiển.
    }

    // ─── 4. ĐẶT CỜ / 8. GỠ CỜ (UC_04_DC / UC_04_GC) ──────────────────────
    public void toggleFlag(int r, int c) {
        // 4.1.0 Tại bước 4.0.4, nếu ô được nhấp chuột phải là ô đã mở.
        if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c].isRevealed()) {
            // 4.1.1 Hệ thống bỏ qua sự kiện, không thực hiện bất kỳ hành động nào.
            return;
        }

        Cell cell = grid[r][c];

        // 4.0.4 Hệ thống kiểm tra trạng thái của ô (Cell) tương ứng để xem ô này đã có cờ hay chưa.
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
    // ─── 9. GỢI Ý (UC_09_GH) ─────────────────────────────────────────────
    public boolean giveHint() {
        // 9.0.3 Bộ điều khiển gọi hàm xử lý tính toán gợi ý trên đối tượng bàn chơi (Board).

        // 9.2.0 Tại bước 9.0.3, nếu ván chơi không ở trạng thái RUNNING hoặc bàn cờ không còn ô an toàn nào khác để gợi ý.
        if (gameState != GameState.RUNNING) {
            // 9.2.1 Hệ thống chặn yêu cầu gợi ý, không thực hiện hành động và bỏ qua sự kiện.
            // 9.2.2 Use case kết thúc.
            return false;
        }

        if (firstMove) {
            return false;
        }

        // 9.0.4 Hệ thống chạy thuật toán suy luận logic dựa trên thông số mìn lân cận và cờ đã cắm để tìm ô an toàn 100%.
        int[] hintPos = findSafeHintPosition();
        // 9.2.0 Tại bước 9.0.3, nếu ván chơi không ở trạng thái RUNNING hoặc bàn cờ không còn ô an toàn nào khác để gợi ý.
        if (hintPos == null) {
            // 9.2.1 Hệ thống chặn yêu cầu gợi ý, không thực hiện hành động và bỏ qua sự kiện.
            // 9.2.2 Use case kết thúc.
            return false;
        }

        // 9.0.5 Hệ thống tìm thấy ô chắc chắn an toàn thông qua thuật toán suy luận và trả tọa độ về cho bộ điều khiển.
        int r = hintPos[0];
        int c = hintPos[1];

        // 9.0.7 Bộ điều khiển phối hợp và kích hoạt cơ chế reveal hiện có để tự động mở ô an toàn này.
        reveal(r, c);
        // 9.0.8 Hệ thống cập nhật trạng thái ô thành đã mở, kích hoạt lan truyền flood fill (nếu ô trống), cập nhật bộ đếm/timer và kiểm tra điều kiện Thắng/Thua.

        return true;
    }

    /**
     * Tìm một ô an toàn để gợi ý.
     * Ưu tiên 1: Suy luận từ các ô số đã mở + cờ (logic cơ bản của người chơi giỏi).
     * Ưu tiên 2: Bất kỳ ô chưa mở, chưa cắm cờ, không chứa mìn nào (đảm bảo an toàn tuyệt đối).
     * Trong fallback ưu tiên ô có nearbyMines thấp (0 trước) để gợi ý có ích hơn.
     */
    // ─── 9. THUẬT TOÁN TÌM Ô GỢI Ý (UC_09_GH) ───────────────────────────
    private int[] findSafeHintPosition() {
        // 9.0.4 Hệ thống chạy thuật toán suy luận logic dựa trên thông số mìn lân cận và cờ đã cắm để tìm ô an toàn 100%.
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = grid[r][c];
                if (!cell.isRevealed() || cell.getNearbyMines() == 0) {
                    continue;
                }

                int flaggedCount = countFlaggedNeighbors(r, c);
                if (flaggedCount == cell.getNearbyMines()) {
                    int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
                    int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
                    for (int i = 0; i < 8; i++) {
                        int nr = r + dr[i];
                        int nc = c + dc[i];
                        if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                        Cell neighbor = grid[nr][nc];
                        if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
                            // 9.0.5 Hệ thống tìm thấy ô chắc chắn an toàn thông qua thuật toán suy luận và trả tọa độ về cho bộ điều khiển.
                            return new int[]{nr, nc};
                        }
                    }
                }
            }
        }

        // 9.1.0 Tại bước 9.0.4, nếu thuật toán suy luận logic không tìm ra ô chắc chắn an toàn (do trạng thái bàn cờ bắt buộc phải đoán mò).
        // 9.1.1 Hệ thống chuyển sang thuật toán phụ quét toàn bộ bàn cờ để chọn ra một ô an toàn ngẫu nhiên bất kỳ còn lại (chưa mở và chưa cắm cờ).
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
            // 9.1.2 Hệ thống trả về tọa độ ô an toàn ngẫu nhiên này và tiếp tục luồng xử lý từ bước 9.0.5.
            return new int[]{bestR, bestC};
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
            // 9.1.2 Hệ thống trả về tọa độ ô an toàn ngẫu nhiên này và tiếp tục luồng xử lý từ bước 9.0.5.
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
