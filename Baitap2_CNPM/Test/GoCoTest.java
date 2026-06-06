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
 * - UC_04_GC / UC_08: Gỡ cờ (Remove Flag)
 * 
 * Các phương thức liên quan:
 * - Board.toggleFlag(int r, int c)  — hàm điều phối (nếu ô có cờ → gỡ)
 * - Board.handleRemoveFlag(int r, int c) — gỡ cờ
 * - Board.getFlagCount() — đếm số cờ đã đặt
 * - Board.getRemainingMines() — số mìn còn lại
 * 
 * Lưu ý: toggleFlag nếu ô đã có cờ → gỡ cờ; nếu ô chưa có cờ → đặt cờ.
 *         File này chỉ test luồng "gỡ cờ" (ô đã có flag trước đó).
 */
public class GoCoTest {

    private Board board;
    private static final int ROWS = 9;
    private static final int COLS = 9;

    @Before
    public void setUp() {
        board = new Board(Difficulty.EASY);
    }

    // =========================================================
    //  UC_04_GC / UC_08: GỠ CỜ
    // =========================================================

    /**
     * Test 1: Gỡ cờ từ ô đang có cờ → thành công
     * - Input: Đặt cờ (0,0) → toggleFlag(0,0) lần 2 (gỡ)
     * - Expected: cell.isFlagged() == false, flagCount giảm 1
     */
    @Test
    public void testGoCo_TuODangCoCo_ThanhCong() {
        // Arrange
        Cell cell = board.getGrid()[5][5];
        board.toggleFlag(5, 5); // đặt cờ trước
        assertTrue("Đã đặt cờ thành công", cell.isFlagged());
        assertEquals("flagCount = 1", 1, board.getFlagCount());

        // Act
        board.toggleFlag(5, 5); // gỡ cờ (toggle)

        // Assert
        assertFalse("Cờ phải được gỡ bỏ", cell.isFlagged());
        assertEquals("flagCount phải về 0", 0, board.getFlagCount());
    }

    /**
     * Test 2: Gỡ cờ nhiều lần trên cùng một ô (đặt ⇄ gỡ ⇄ đặt ⇄ gỡ)
     */
    @Test
    public void testGoCo_DatVaGo_NhieuLan() {
        Cell cell = board.getGrid()[3][7];

        // Lần 1: Đặt
        board.toggleFlag(3, 7);
        assertTrue("Lần 1: có cờ", cell.isFlagged());
        assertEquals(1, board.getFlagCount());

        // Lần 2: Gỡ
        board.toggleFlag(3, 7);
        assertFalse("Lần 2: gỡ cờ", cell.isFlagged());
        assertEquals(0, board.getFlagCount());

        // Lần 3: Đặt lại
        board.toggleFlag(3, 7);
        assertTrue("Lần 3: đặt cờ lại", cell.isFlagged());
        assertEquals(1, board.getFlagCount());

        // Lần 4: Gỡ lại
        board.toggleFlag(3, 7);
        assertFalse("Lần 4: gỡ cờ lại", cell.isFlagged());
        assertEquals(0, board.getFlagCount());
    }

    /**
     * Test 3: Gỡ cờ khi firstMove = true (chưa mở ô nào) → vẫn được phép
     */
    @Test
    public void testGoCo_KhiFirstMove_VanDuocPhep() {
        // Arrange
        assertTrue("Chưa có nước đi nào", board.isFirstMove());
        board.toggleFlag(0, 1); // đặt cờ trước
        assertTrue("Đặt cờ thành công khi firstMove", board.getGrid()[0][1].isFlagged());

        // Act
        board.toggleFlag(0, 1); // gỡ cờ

        // Assert
        assertFalse("Gỡ cờ thành công khi firstMove", board.getGrid()[0][1].isFlagged());
        assertEquals(0, board.getFlagCount());
    }

    /**
     * Test 4: Gỡ cờ không ảnh hưởng đến trạng thái revealed của ô
     */
    @Test
    public void testGoCo_KhongAnhHuongRevealed() {
        Cell cell = board.getGrid()[2][5];
        board.toggleFlag(2, 5); // đặt cờ
        assertTrue("Cờ đã được đặt", cell.isFlagged());
        assertFalse("Ô vẫn chưa revealed", cell.isRevealed());

        // Act
        board.toggleFlag(2, 5); // gỡ cờ

        // Assert
        assertFalse("Sau gỡ cờ, ô không còn cờ", cell.isFlagged());
        assertFalse("Sau gỡ cờ, ô vẫn chưa revealed", cell.isRevealed());
    }

    /**
     * Test 5: Gỡ cờ khi game đang RUNNING → vẫn được phép (luồng chính)
     */
    @Test
    public void testGoCo_GameRunning_GoDuoc() {
        // Arrange
        board.setGameState(GameState.RUNNING);
        board.toggleFlag(2, 3); // đặt cờ
        assertTrue("Đã đặt cờ", board.getGrid()[2][3].isFlagged());
        assertEquals(1, board.getFlagCount());

        // Act
        board.toggleFlag(2, 3); // gỡ cờ

        // Assert
        assertFalse("Gỡ cờ thành công khi RUNNING", board.getGrid()[2][3].isFlagged());
        assertEquals(0, board.getFlagCount());
    }

    /**
     * Test 6: Gỡ cờ → remainingMines tăng trở lại
     */
    @Test
    public void testGoCo_RemainingMines_Tang() {
        // Arrange
        board.reveal(4, 4); // first move
        board.toggleFlag(0, 0); // đặt 1 cờ
        int remainingAfterPlace = board.getRemainingMines();
        int mineCount = board.getMineCount();
        assertEquals("Sau đặt 1 cờ", mineCount - 1, remainingAfterPlace);

        // Act
        board.toggleFlag(0, 0); // gỡ cờ

        // Assert
        assertEquals("Remaining mines tăng lại", mineCount, board.getRemainingMines());
        assertEquals("flagCount về 0", 0, board.getFlagCount());
    }

    /**
     * Test 7: Gỡ cờ từ ô có mìn (đã đặt cờ sai) → vẫn gỡ được
     */
    @Test
    public void testGoCo_TuOMine_DatCoSai_GoDuoc() {
        // Arrange
        board.reveal(4, 4); // first move
        // Tìm ô có mìn
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
        assertNotNull("Phải tìm thấy ô mìn", mineCell);

        board.toggleFlag(mineR, mineC); // đặt cờ sai
        assertTrue("Đã đặt cờ lên ô mìn", mineCell.isFlagged());

        // Act
        board.toggleFlag(mineR, mineC); // gỡ cờ

        // Assert
        assertFalse("Gỡ cờ từ ô mìn thành công", mineCell.isFlagged());
    }

    /**
     * Test 8: handleRemoveFlag trực tiếp — gỡ cờ chính xác
     */
    @Test
    public void testHandleRemoveFlag_TrucTiep() {
        // Arrange
        Cell cell = board.getGrid()[6][2];
        board.toggleFlag(6, 2); // đặt cờ trước
        assertTrue("Đã đặt cờ", cell.isFlagged());
        assertEquals(1, board.getFlagCount());

        // Act
        board.handleRemoveFlag(6, 2);

        // Assert
        assertFalse("Cell không còn flagged", cell.isFlagged());
        assertEquals("flagCount giảm 1", 0, board.getFlagCount());
    }

    /**
     * Test 9: Gỡ nhiều cờ trên nhiều ô khác nhau
     */
    @Test
    public void testGoCo_NhieuCo_TrenNhieuO() {
        // Arrange — đặt 3 cờ
        board.toggleFlag(0, 0);
        board.toggleFlag(0, 1);
        board.toggleFlag(0, 2);
        assertEquals("Đã đặt 3 cờ", 3, board.getFlagCount());

        // Act — gỡ 2 cờ
        board.toggleFlag(0, 0);
        board.toggleFlag(0, 1);

        // Assert
        assertEquals("Còn 1 cờ", 1, board.getFlagCount());
        assertFalse("Ô (0,0) hết cờ", board.getGrid()[0][0].isFlagged());
        assertFalse("Ô (0,1) hết cờ", board.getGrid()[0][1].isFlagged());
        assertTrue("Ô (0,2) còn cờ", board.getGrid()[0][2].isFlagged());
    }

    /**
     * Test 10: Gỡ cờ khi game PAUSE (ở Board layer vẫn gọi được)
     */
    @Test
    public void testGoCo_KhiGamePause_VanGoDuoc() {
        // Arrange
        board.setGameState(GameState.PAUSE);
        board.toggleFlag(1, 1); // đặt cờ khi PAUSE
        assertTrue("Đã đặt cờ khi PAUSE", board.getGrid()[1][1].isFlagged());

        // Act
        board.toggleFlag(1, 1); // gỡ cờ khi PAUSE

        // Assert
        assertFalse("Gỡ cờ thành công khi PAUSE", board.getGrid()[1][1].isFlagged());
        assertEquals(0, board.getFlagCount());
    }

    /**
     * Test 11: Gỡ cờ không ảnh hưởng đến các ô lân cận
     */
    @Test
    public void testGoCo_KhongAnhHuongLanCan() {
        // Arrange
        board.toggleFlag(3, 3); 
        board.toggleFlag(3, 4); 
        board.toggleFlag(4, 3); 

        // Act — chỉ gỡ cờ ở ô (3,3)
        board.toggleFlag(3, 3);

        // Assert
        assertFalse("Ô (3,3) hết cờ", board.getGrid()[3][3].isFlagged());
        assertTrue("Ô (3,4) vẫn còn cờ", board.getGrid()[3][4].isFlagged());
        assertTrue("Ô (4,3) vẫn còn cờ", board.getGrid()[4][3].isFlagged());
        assertEquals("Còn 2 cờ", 2, board.getFlagCount());
    }
}