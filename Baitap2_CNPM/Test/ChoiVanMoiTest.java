package Test;

import static org.junit.Assert.*;

import org.junit.Test;

import model.Board;
import model.Difficulty;
import model.GameConfig;
import model.GameState;

/**
 * Test class cho Use Case:
 * - UC_01: Chơi ván mới
 *
 * Các phương thức liên quan:
 * - Board(GameConfig config) : khởi tạo bàn chơi mới
 * - Board.getConfig()
 * - Board.getRows()
 * - Board.getCols()
 * - Board.getMineCount()
 * - Board.getGameState()
 *
 * Lưu ý:
 * Use case bắt đầu sau khi đã nhận được GameConfig.
 * Vì vậy các test chỉ tập trung vào quá trình tạo Board mới.
 */
public class ChoiVanMoiTest {

    // =========================================================
    // UC_01 : CHƠI VÁN MỚI
    // =========================================================

    /**
     * Test 1: Tạo ván mới với cấu hình Easy
     * - Input: 9x9, 10 mìn
     * - Expected:
     *      + Board được tạo thành công
     *      + Kích thước và số mìn đúng
     *      + Trạng thái game là RUNNING
     */
    @Test
    public void testChoiVanMoi_Easy_ThanhCong() {

        // Arrange
        GameConfig config = Difficulty.EASY.getConfig();

        // Act
        Board board = new Board(config);

        // Assert
        assertEquals("Số hàng phải bằng 9", 9, board.getRows());
        assertEquals("Số cột phải bằng 9", 9, board.getCols());
        assertEquals("Số mìn phải bằng 10", 10, board.getMineCount());

        assertEquals("Game phải ở trạng thái RUNNING",
                GameState.RUNNING, board.getGameState());
    }

    /**
     * Test 2: Tạo ván mới với cấu hình Medium
     */
    @Test
    public void testChoiVanMoi_Medium_ThanhCong() {

        // Arrange
        GameConfig config = Difficulty.MEDIUM.getConfig();

        // Act
        Board board = new Board(config);

        // Assert
        assertEquals(16, board.getRows());
        assertEquals(16, board.getCols());
        assertEquals(40, board.getMineCount());

        assertEquals(GameState.RUNNING, board.getGameState());
    }

    /**
     * Test 3: Tạo ván mới với cấu hình Hard
     */
    @Test
    public void testChoiVanMoi_Hard_ThanhCong() {

        // Arrange
        GameConfig config = Difficulty.HARD.getConfig();

        // Act
        Board board = new Board(config);

        // Assert
        assertEquals(16, board.getRows());
        assertEquals(30, board.getCols());
        assertEquals(99, board.getMineCount());

        assertEquals(GameState.RUNNING, board.getGameState());
    }

    /**
     * Test 4: Sau khi tạo ván mới, firstMove phải bằng true
     */
    @Test
    public void testChoiVanMoi_FirstMove_BanDauLaTrue() {

        // Arrange
        GameConfig config = Difficulty.EASY.getConfig();

        // Act
        Board board = new Board(config);

        // Assert
        assertTrue("Ván mới phải chưa có nước đi đầu tiên",
                board.isFirstMove());
    }

    /**
     * Test 5: Sau khi tạo ván mới, thời gian phải bằng 0
     */
    @Test
    public void testChoiVanMoi_ElapsedTime_Bang0() {

        // Arrange
        GameConfig config = Difficulty.EASY.getConfig();

        // Act
        Board board = new Board(config);

        // Assert
        assertEquals("Thời gian ban đầu phải bằng 0",
                0, board.getElapsedTime());
    }

    /**
     * Test 6: Sau khi tạo ván mới, số cờ đã đặt phải bằng 0
     */
    @Test
    public void testChoiVanMoi_FlagCount_Bang0() {

        // Arrange
        GameConfig config = Difficulty.EASY.getConfig();

        // Act
        Board board = new Board(config);

        // Assert
        assertEquals("Chưa có cờ nào được đặt",
                0, board.getFlagCount());
    }

    /**
     * Test 7: Sau khi tạo ván mới, config phải được lưu lại đúng
     */
    @Test
    public void testChoiVanMoi_LuuDungConfig() {

        // Arrange
        GameConfig config = new GameConfig(12, 15, 20);

        // Act
        Board board = new Board(config);

        // Assert
        assertEquals(12, board.getConfig().getRows());
        assertEquals(15, board.getConfig().getCols());
        assertEquals(20, board.getConfig().getMineCount());
    }

    /**
     * Test 8: Tạo nhiều ván mới liên tiếp thì mỗi ván độc lập với nhau
     */
    @Test
    public void testChoiVanMoi_NhieuLan_CacBoardDocLap() {

        // Arrange
        Board board1 = new Board(Difficulty.EASY.getConfig());

        // Act
        Board board2 = new Board(Difficulty.MEDIUM.getConfig());

        // Assert
        assertNotSame("Hai board phải là hai đối tượng khác nhau",
                board1, board2);

        assertEquals(9, board1.getRows());
        assertEquals(16, board2.getRows());
    }

    /**
     * Test 9: Sau khi tạo ván mới, tất cả các ô đều chưa được mở
     */
    @Test
    public void testChoiVanMoi_TatCaCell_ChuaReveal() {

        // Arrange
        Board board = new Board(Difficulty.EASY.getConfig());

        // Assert
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                assertFalse(
                        "Ô (" + r + "," + c + ") chưa được mở",
                        board.getGrid()[r][c].isRevealed());
            }
        }
    }

    /**
     * Test 10: Sau khi tạo ván mới, tất cả các ô đều chưa có cờ
     */
    @Test
    public void testChoiVanMoi_TatCaCell_ChuaCoCo() {

        // Arrange
        Board board = new Board(Difficulty.EASY.getConfig());

        // Assert
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                assertFalse(
                        "Ô (" + r + "," + c + ") chưa có cờ",
                        board.getGrid()[r][c].isFlagged());
            }
        }
    }
}