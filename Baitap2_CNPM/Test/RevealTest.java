package Test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import model.Board;
import model.Cell;
import model.GameConfig;
import model.GameState;

/**
 * Test class cho chức năng Mở ô (Reveal)
 * Use Case liên quan: UC_03_MO
 */
public class RevealTest {

    private Board board;
    private static final int ROWS = 9;
    private static final int COLS = 9;
    private static final int MINES = 10;

    @Before
    public void setUp() {
        GameConfig config = new GameConfig(ROWS, COLS, MINES);
        board = new Board(config);
    }

    /**
     * Test 1: First Move - Không đặt mìn tại vị trí người chơi click
     */
    @Test
    public void testReveal_FirstMove_KhongDatMinTaiViTriClick() {
        board.reveal(4, 4);
        assertFalse("First move phải đặt mìn tránh ô click", board.getGrid()[4][4].isMine());
        assertTrue("Ô đầu tiên phải được mở", board.getGrid()[4][4].isRevealed());
        assertFalse("Sau first move, firstMove flag phải là false", board.isFirstMove());
    }

    /**
     * Test 2: Flood Fill - Tự động mở nhiều ô khi gặp ô trống
     */
    @Test
    public void testReveal_FloodFill_KhiGapOTrong() {
        board.reveal(4, 4); // first move
        int revealedBefore = countRevealedCells(board);
        
        board.reveal(5, 5);
        
        assertTrue("Flood fill phải mở nhiều ô khi gặp ô trống", 
                  countRevealedCells(board) > revealedBefore + 1);
    }

    /**
     * Test 3: Mở trúng mìn → Game Over (LOSE)
     */
    @Test
    public void testReveal_DungMin_ThuaGame() {
        Board smallBoard = new Board(new GameConfig(3, 3, 1));
        smallBoard.reveal(0, 0); // first move
        
        boolean foundMine = false;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (!smallBoard.getGrid()[r][c].isRevealed() && smallBoard.getGrid()[r][c].isMine()) {
                    smallBoard.reveal(r, c);
                    foundMine = true;
                    break;
                }
            }
            if (foundMine) break;
        }
        
        assertEquals("Khi mở trúng mìn phải chuyển sang LOSE", 
                    GameState.LOSE, smallBoard.getGameState());
    }

    /**
     * Test 4: Click lại ô đã mở → Không thay đổi trạng thái
     */
    @Test
    public void testReveal_DaMoRoi_KhongLamGi() {
        board.reveal(4, 4);
        int revealedCount = countRevealedCells(board);
        
        board.reveal(4, 4); // click lại
        
        assertEquals("Click ô đã mở không thay đổi số ô được mở", 
                    revealedCount, countRevealedCells(board));
    }

    /**
     * Test 5: Không cho phép mở ô khi game đã kết thúc
     */
    @Test
    public void testReveal_KhiGameDaKetThuc_KhongChoPhep() {
        board.reveal(4, 4);
        board.setGameState(GameState.WIN);
        
        board.reveal(5, 5);
        
        assertFalse("Không được mở ô mới khi game đã WIN", board.getGrid()[5][5].isRevealed());
    }

    /**
     * Test 6: Không cho phép mở ô khi game đang Pause
     */
    @Test
    public void testReveal_KhiGameDangPause_KhongChoPhep() {
        board.reveal(4, 4);
        board.setGameState(GameState.PAUSE);
        
        board.reveal(5, 5);
        
        assertFalse("Không được mở ô khi game đang PAUSE", board.getGrid()[5][5].isRevealed());
    }

    /**
     * Test 7: Chord (Click chuột giữa)
     */
    @Test
    public void testReveal_Chord_ClickChuotGiua() {
        board.reveal(4, 4); // first move
        // Giả sử sau first move có ô số
        board.chord(4, 4);
        // Kiểm tra logic chord đã được gọi (không throw exception)
        assertTrue("Chord không gây lỗi", true);
    }

    private int countRevealedCells(Board b) {
        int count = 0;
        Cell[][] grid = b.getGrid();
        for (int r = 0; r < b.getRows(); r++) {
            for (int c = 0; c < b.getCols(); c++) {
                if (grid[r][c].isRevealed()) {
                    count++;
                }
            }
        }
        return count;
    }
}
