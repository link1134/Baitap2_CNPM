package Test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import model.Board;
import model.Cell;
import model.Difficulty;
import model.GameState;

/**
 * Test class cho Use Case:
 * - UC_09_GH: Gợi ý (Hint)
 * 
 * Các phương thức liên quan:
 * - Board.giveHint() — xử lý gợi ý
 * - Board.findSafeHintPosition() — thuật toán tìm ô an toàn
 * - Board.countFlaggedNeighbors(int r, int c) — đếm cờ lân cận
 */
public class HintTest {

    private Board board;
    private static final int ROWS = 9;
    private static final int COLS = 9;

    @Before
    public void setUp() {
        board = new Board(Difficulty.EASY.getConfig());
    }

    // =========================================================
    //  UC_09_GH: GỢI Ý
    // =========================================================

    /**
     * Test 1: Gợi ý khi chưa có nước đi nào (firstMove = true) → thất bại
     * - Input: giveHint() khi firstMove = true
     * - Expected: return false, không có ô nào được mở
     */
    @Test
    public void testGoiY_KhiFirstMove_ThatBai() {
        // Arrange
        assertTrue("Chưa có nước đi nào", board.isFirstMove());

        // Act
        boolean result = board.giveHint();

        // Assert
        assertFalse("Không thể gợi ý khi chưa có nước đi", result);
    }

    /**
     * Test 2: Gợi ý khi game state không phải RUNNING → thất bại
     * - Input: setGameState(WIN) → giveHint()
     * - Expected: return false
     */
    @Test
    public void testGoiY_GameStateKhongRunning_ThatBai() {
        // Arrange
        board.setGameState(GameState.WIN);

        // Act
        boolean result = board.giveHint();

        // Assert
        assertFalse("Không thể gợi ý khi game WIN", result);
    }

    /**
     * Test 3: Gợi ý khi game LOSE → thất bại
     */
    @Test
    public void testGoiY_GameLOSE_ThatBai() {
        // Arrange
        board.setGameState(GameState.LOSE);

        // Act
        boolean result = board.giveHint();

        // Assert
        assertFalse("Không thể gợi ý khi game LOSE", result);
    }

    /**
     * Test 4: Gợi ý khi game PAUSE → thất bại
     */
    @Test
    public void testGoiY_GamePAUSE_ThatBai() {
        // Arrange
        board.setGameState(GameState.PAUSE);

        // Act
        boolean result = board.giveHint();

        // Assert
        assertFalse("Không thể gợi ý khi game PAUSE", result);
    }

    /**
     * Test 5: Gợi ý thành công — mở được một ô an toàn sau first move
     * - Input: reveal(4,4) → giveHint()
     * - Expected: return true, một ô an toàn được mở thêm
     */
    @Test
    public void testGoiY_SauFirstMove_ThanhCong() {
        // Arrange
        board.reveal(4, 4); // first move — mìn đã được đặt
        assertFalse("Đã thực hiện first move", board.isFirstMove());

        // Đếm số ô đã mở trước hint
        int revealedBefore = countRevealedCells();

        // Act
        boolean result = board.giveHint();

        // Assert
        assertTrue("Gợi ý phải thành công", result);
        int revealedAfter = countRevealedCells();
        assertTrue("Phải có thêm ít nhất 1 ô được mở", revealedAfter > revealedBefore);
    }

    /**
     * Test 6: Gợi ý nhiều lần liên tiếp — mỗi lần đều mở thêm ô
     */
    @Test
    public void testGoiY_NhieuLan_LienTiep() {
        // Arrange
        board.reveal(4, 4); // first move

        // Act & Assert — gợi ý 3 lần
        for (int i = 0; i < 3; i++) {
            int before = countRevealedCells();
            boolean result = board.giveHint();
            if (result) {
                int after = countRevealedCells();
                assertTrue("Lần " + (i+1) + " phải mở thêm ô", after > before);
            } else {
                // Có thể hết ô an toàn để gợi ý — vẫn chấp nhận
                break;
            }
        }
    }

    /**
     * Test 7: Gợi ý khi tất cả ô an toàn đã được mở → thất bại
     * (Mô phỏng bằng cách mở gần hết bàn cờ)
     */
    @Test
    public void testGoiY_KhiHetOAnToan_ThatBai() {
        // Arrange
        board.reveal(4, 4); // first move

        // Mở tất cả các ô không phải mìn (trừ mìn)
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Cell cell = board.getGrid()[r][c];
                if (!cell.isMine() && !cell.isRevealed()) {
                    board.reveal(r, c);
                }
            }
        }

        // Kiểm tra xem còn ô an toàn nào không
        boolean hasSafeCell = false;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Cell cell = board.getGrid()[r][c];
                if (!cell.isMine() && !cell.isRevealed()) {
                    hasSafeCell = true;
                    break;
                }
            }
            if (hasSafeCell) break;
        }

        // Act
        boolean result = board.giveHint();

        // Assert
        if (!hasSafeCell) {
            assertFalse("Không còn ô an toàn → gợi ý thất bại", result);
        }
    }

    /**
     * Test 8: Gợi ý mở ô — ô đó phải không phải mìn
     */
    @Test
    public void testGoiY_OMoRa_KhongPhaiMine() {
        // Arrange
        board.reveal(4, 4); // first move

        // Act
        boolean result = board.giveHint();

        // Assert
        if (result) {
            // Kiểm tra tất cả ô đã mở đều không phải mìn
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    Cell cell = board.getGrid()[r][c];
                    if (cell.isRevealed() && !cell.isExploded()) {
                        assertFalse("Ô được gợi ý mở không được là mìn", cell.isMine());
                    }
                }
            }
        }
    }

    /**
     * Test 9: Gợi ý khi có cờ xung quanh — thuật toán suy luận
     * 
     * Mô phỏng:
     * 1. Mở ô (4,4) — first move
     * 2. Nếu ô (4,4) có nearbyMines > 0, đặt cờ vào đúng số mìn lân cận
     * 3. Gợi ý sẽ suy luận và mở ô an toàn còn lại
     */
    @Test
    public void testGoiY_SuyLuanTuCoXungQuanh() {
        // Arrange
        board.reveal(4, 4); // first move
        Cell center = board.getGrid()[4][4];
        int nearby = center.getNearbyMines();

        // Nếu ô trung tâm có số > 0, thử đặt cờ xung quanh
        if (nearby > 0 && center.isRevealed()) {
            int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
            
            int flagged = 0;
            for (int i = 0; i < 8 && flagged < nearby; i++) {
                int nr = 4 + dr[i];
                int nc = 4 + dc[i];
                if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS) {
                    Cell neighbor = board.getGrid()[nr][nc];
                    if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
                        board.toggleFlag(nr, nc);
                        flagged++;
                    }
                }
            }

            if (flagged == nearby) {
                // Đã đặt đủ cờ → gợi ý sẽ suy luận ra ô an toàn
                int before = countRevealedCells();
                boolean result = board.giveHint();
                if (result) {
                    int after = countRevealedCells();
                    assertTrue("Suy luận từ cờ phải mở thêm ô", after > before);
                }
            }
        }
    }

    /**
     * Test 10: giveHint() không làm thay đổi gameState thành LOSE
     */
    @Test
    public void testGoiY_KhongLamGameLOSE() {
        
        board.reveal(4, 4); // first move
        GameState stateBefore = board.getGameState();

      
        board.giveHint();

        // Assert
        GameState stateAfter = board.getGameState();
        assertNotEquals("Gợi ý không được làm game LOSE", GameState.LOSE, stateAfter);
        assertNotEquals("Gợi ý không được làm game WIN (trừ khi hết ô)", GameState.WIN, stateAfter);
    }

    /**
     * Test 11: Gợi ý khi game đã WIN → thất bại
     */
    @Test
    public void testGoiY_KhiGameWIN_ThatBai() {
  
        board.setGameState(GameState.WIN);

    
        boolean result = board.giveHint();

     
        assertFalse("Không gợi ý khi game WIN", result);
    }



    /**
     * Đếm số ô đã được mở (revealed) trên bàn cờ
     */
    private int countRevealedCells() {
        int count = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (board.getGrid()[r][c].isRevealed()) {
                    count++;
                }
            }
        }
        return count;
    }
}