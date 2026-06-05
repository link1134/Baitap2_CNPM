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
 * - UC_04_DC: Đặt cờ (Place Flag)
 * 
 * Các phương thức liên quan:
 * - Board.toggleFlag(int r, int c)  — hàm điều phối
 * - Board.handlePlaceFlag(int r, int c)  — đặt cờ
 * - Board.getFlagCount() — đếm số cờ đã đặt
 * - Board.getRemainingMines() — số mìn còn lại
 * 
 * Lưu ý: toggleFlag nếu ô chưa có cờ → đặt cờ; nếu ô đã có cờ → gỡ cờ.
 *         File này chỉ test luồng "đặt cờ" (ô chưa flag).
 */
public class DatCoTest {

    private Board board;
    private static final int ROWS = 9;
    private static final int COLS = 9;

    @Before
    public void setUp() {
        // Dùng EASY: 9x9, ~10 mìn
        board = new Board(Difficulty.EASY);
    }

    // =========================================================
    //  UC_04_DC: ĐẶT CỜ
    // =========================================================

    /**
     * Test 1: Đặt cờ vào ô chưa mở, chưa có cờ → thành công
     * - Input: toggleFlag(0, 0) trên ô chưa mở, chưa flag
     * - Expected: cell.isFlagged() == true, flagCount tăng 1
     */
    @Test
    public void testDatCo_OChuaMo_ThanhCong() {
        // Arrange
        int initialFlagCount = board.getFlagCount();
        Cell cell = board.getGrid()[0][0];
        assertFalse("Ô phải chưa được mở", cell.isRevealed());
        assertFalse("Ô phải chưa có cờ", cell.isFlagged());

        // Act
        board.toggleFlag(0, 0);

        // Assert
        assertTrue("Ô phải có cờ sau khi đặt", cell.isFlagged());
        assertEquals("Số cờ phải tăng lên 1", initialFlagCount + 1, board.getFlagCount());
    }

    /**
     * Test 2: Đặt cờ vào ô đã mở (revealed) → không được phép
     * - Input: reveal(4,4) rồi toggleFlag(4,4) trên ô đã mở
     * - Expected: cell.isFlagged() == false, flagCount không đổi
     */
    @Test
    public void testDatCo_ODaMo_KhongDuocPhep() {
        // Arrange
        board.reveal(4, 4); // first move, mìn tránh ô này
        Cell cell = board.getGrid()[4][4];
        assertTrue("Ô phải đã được mở", cell.isRevealed());

        int flagCountBefore = board.getFlagCount();

        // Act
        board.toggleFlag(4, 4);

        // Assert
        assertFalse("Ô đã mở không thể đặt cờ", cell.isFlagged());
        assertEquals("Số cờ không được thay đổi", flagCountBefore, board.getFlagCount());
    }

    /**
     * Test 3: Đặt cờ vào ô ngoài phạm vi bàn cờ → không được phép
     * - Input: toggleFlag(-1, 0), (0, 99), (ROWS, COLS), (-5, -5)
     * - Expected: flagCount không đổi, không throw exception
     */
    @Test
    public void testDatCo_NgoaiPhamVi_KhongDuocPhep() {
        int flagCountBefore = board.getFlagCount();

        // Act — không throw exception
        board.toggleFlag(-1, 0);
        board.toggleFlag(0, 99);
        board.toggleFlag(ROWS, COLS);
        board.toggleFlag(-5, -5);

        // Assert
        assertEquals("Số cờ không được thay đổi khi click ngoài phạm vi",
                flagCountBefore, board.getFlagCount());
    }

    /**
     * Test 4: Đặt cờ khi game đang RUNNING → vẫn được phép (luồng chính)
     */
    @Test
    public void testDatCo_GameRunning_DatDuoc() {
        // Arrange
        board.setGameState(GameState.RUNNING);
        Cell cell = board.getGrid()[2][3];
        int flagCountBefore = board.getFlagCount();

        // Act
        board.toggleFlag(2, 3);

        // Assert
        assertTrue("Đặt cờ thành công khi game RUNNING", cell.isFlagged());
        assertEquals("flagCount tăng 1", flagCountBefore + 1, board.getFlagCount());
    }

    /**
     * Test 5: Đặt nhiều cờ trên nhiều ô khác nhau
     */
    @Test
    public void testDatCo_NhieuCo_TrenNhieuO() {
        // Act
        board.toggleFlag(0, 0);
        board.toggleFlag(1, 1);
        board.toggleFlag(2, 2);
        board.toggleFlag(3, 3);

        // Assert
        assertEquals("Phải đặt được 4 cờ", 4, board.getFlagCount());
        assertTrue("Ô (0,0) có cờ", board.getGrid()[0][0].isFlagged());
        assertTrue("Ô (1,1) có cờ", board.getGrid()[1][1].isFlagged());
        assertTrue("Ô (2,2) có cờ", board.getGrid()[2][2].isFlagged());
        assertTrue("Ô (3,3) có cờ", board.getGrid()[3][3].isFlagged());
    }

    /**
     * Test 6: Đặt cờ lên ô có chứa mìn → vẫn được phép (cắm cờ sai)
     */
    @Test
    public void testDatCo_LenOMine_VanDuocPhep() {
        // Arrange
        board.reveal(4, 4); // first move, mìn được đặt
        // Tìm một ô có mìn
        Cell mineCell = null;
        int mineR = -1, mineC = -1;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (board.getGrid()[r][c].isMine()) {
                    mineCell = board.getGrid()[r][c];
                    mineR = r;
                    mineC = c;
                    break;
                }
            }
            if (mineCell != null) break;
        }
        assertNotNull("Phải tìm thấy ô có mìn", mineCell);
        assertFalse("Ô mìn chưa được mở", mineCell.isRevealed());

        // Act
        board.toggleFlag(mineR, mineC);

        // Assert
        assertTrue("Có thể đặt cờ lên ô có mìn", mineCell.isFlagged());
    }

    /**
     * Test 7: getRemainingMines() giảm sau khi đặt cờ
     */
    @Test
    public void testDatCo_RemainingMines_Giam() {
        // Arrange
        board.reveal(4, 4); // first move
        int remainingBefore = board.getRemainingMines();

        // Act
        board.toggleFlag(0, 0);

        // Assert
        int remainingAfter = board.getRemainingMines();
        assertEquals("Remaining mines giảm đi 1", remainingBefore - 1, remainingAfter);
    }

    /**
     * Test 8: Đặt cờ quá số lượng mìn → remainingMines = 0 (không âm)
     */
    @Test
    public void testDatCo_RemainingMines_KhongAm() {
        // Arrange
        board.reveal(4, 4); // first move
        int mineCount = board.getMineCount();

        // Act: đặt cờ nhiều hơn số mìn
        for (int i = 0; i < mineCount + 5; i++) {
            int r = i / COLS;
            int c = i % COLS;
            if (r < ROWS && c < COLS && !board.getGrid()[r][c].isRevealed()) {
                board.toggleFlag(r, c);
            }
        }

        // Assert
        assertTrue("flagCount có thể > mineCount", board.getFlagCount() >= mineCount);
        assertEquals("remainingMines không được âm", 0, board.getRemainingMines());
    }

    /**
     * Test 9: Đặt cờ không ảnh hưởng đến trạng thái revealed của ô
     */
    @Test
    public void testDatCo_KhongAnhHuongRevealed() {
        Cell cell = board.getGrid()[2][5];
        assertFalse("Ô chưa revealed", cell.isRevealed());

        board.toggleFlag(2, 5); // đặt cờ

        assertFalse("Sau đặt cờ, ô vẫn chưa revealed", cell.isRevealed());
        assertTrue("Cờ đã được đặt", cell.isFlagged());
    }

    /**
     * Test 10: Đặt cờ khi game đang ở trạng thái PAUSE → vẫn được phép
     * (Board.toggleFlag không kiểm tra gameState, controller mới kiểm tra)
     */
    @Test
    public void testDatCo_KhiGamePause_VanCoTheGoiToggleFlag() {
        // Arrange
        board.setGameState(GameState.PAUSE);
        Cell cell = board.getGrid()[1][2];
        int flagCountBefore = board.getFlagCount();

        // Act
        board.toggleFlag(1, 2);

        // Assert
        assertTrue("Đặt cờ thành công khi PAUSE ở Board layer", cell.isFlagged());
        assertEquals("flagCount tăng 1", flagCountBefore + 1, board.getFlagCount());
    }

    /**
     * Test 11: Đặt cờ khi chưa có nước đi nào (firstMove = true)
     */
    @Test
    public void testDatCo_KhiFirstMove_DatDuoc() {
        // Arrange
        assertTrue("Chưa có nước đi nào", board.isFirstMove());
        int flagCountBefore = board.getFlagCount();

        // Act
        board.toggleFlag(0, 1);

        // Assert
        assertTrue("Đặt cờ thành công khi firstMove", board.getGrid()[0][1].isFlagged());
        assertEquals("flagCount tăng 1", flagCountBefore + 1, board.getFlagCount());
    }

    /**
     * Test 12: handlePlaceFlag trực tiếp — đặt cờ chính xác
     */
    @Test
    public void testHandlePlaceFlag_TrucTiep() {
        // Arrange
        Cell cell = board.getGrid()[3][4];
        assertFalse("Ô chưa có cờ", cell.isFlagged());
        int flagCountBefore = board.getFlagCount();

        // Act
        board.handlePlaceFlag(3, 4);

        // Assert
        assertTrue("Cell phải được set flagged", cell.isFlagged());
        assertEquals("flagCount tăng 1", flagCountBefore + 1, board.getFlagCount());
    }
}