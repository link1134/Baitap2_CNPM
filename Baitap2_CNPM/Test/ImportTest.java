package Test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import model.Board;
import model.GameConfig;
import model.GameState;

/**
 * Test class cho chức năng Import(Load màn chơi)
 * Use Case liên quan: UC-6.0
 */
public class ImportTest {

    private Board board;
    private static final int ROWS = 9;
    private static final int COLS = 9;

    @Before
    public void setUp() {
        GameConfig config = new GameConfig(ROWS, COLS, 10);
        board = new Board(config);
    }

    /**
     * Test 1: Import với input null hoặc empty
     */
    @Test
    public void testImport_NullOrEmpty_ThatBai() {
        assertNull("Null input phải trả về null", Board.importData(null));
        assertNull("Empty string phải trả về null", Board.importData(""));
        assertNull("Whitespace phải trả về null", Board.importData("   "));
    }

    /**
     * Test 2: Import với dữ liệu không hợp lệ (invalid Base64, sai format)
     */
    @Test
    public void testImport_InvalidData_ThatBai() {
        assertNull("Dữ liệu invalid phải trả về null", Board.importData("day la du lieu sai"));
        assertNull("Base64 sai phải trả về null", Board.importData("1,invalidbase64!!!"));
        assertNull("Random string", Board.importData("abc123xyz"));
    }

    /**
     * Test 3: Export + Import board mới (chưa chơi) → giữ nguyên trạng thái cơ bản
     */
    @Test
    public void testExportImport_BasicBoard() {
        String data = board.exportData();
        assertNotNull("Export phải thành công", data);

        Board imported = Board.importData(data);
        assertNotNull("Import phải thành công", imported);

        assertEquals("Rows phải giữ nguyên", ROWS, imported.getRows());
        assertEquals("Cols phải giữ nguyên", COLS, imported.getCols());
        assertEquals("Mine count phải giữ nguyên", 10, imported.getMineCount());
        assertEquals("GameState ban đầu", GameState.RUNNING, imported.getGameState());
        assertTrue("First move ban đầu", imported.isFirstMove());
    }

    /**
     * Test 4: Import sau khi đã chơi (reveal, flag, time)
     */
    @Test
    public void testImport_AfterGameplay() {
        // Thực hiện một số hành động
        board.reveal(4, 4); // first move
        board.toggleFlag(2, 2);
        board.toggleFlag(3, 3);
        board.increaseTime();
        board.increaseTime();

        String data = board.exportData();
        Board imported = Board.importData(data);

        assertNotNull("Import sau gameplay", imported);
        assertFalse("FirstMove phải là false", imported.isFirstMove());
        assertEquals("Thời gian phải được lưu", 2, imported.getElapsedTime());
        assertEquals("Số cờ phải được lưu", 2, imported.getFlagCount());
    }

    /**
     * Test 5: Import các trạng thái game khác nhau (WIN, LOSE, PAUSE)
     */
    @Test
    public void testImport_GameStates() {
        // Test WIN
        Board winBoard = new Board(new GameConfig(1, 1, 0));
        winBoard.reveal(0, 0);
        Board importedWin = Board.importData(winBoard.exportData());
        assertEquals("WIN state phải được lưu", GameState.WIN, importedWin.getGameState());

        // Test PAUSE
        Board pauseBoard = new Board(new GameConfig(ROWS, COLS, 10));
        pauseBoard.setGameState(GameState.PAUSE);
        Board importedPause = Board.importData(pauseBoard.exportData());
        assertEquals("PAUSE state phải được lưu", GameState.PAUSE, importedPause.getGameState());

        // Test LOSE
        Board loseBoard = new Board(new GameConfig(ROWS, COLS, 10));
        loseBoard.setGameState(GameState.LOSE);
        Board importedLose = Board.importData(loseBoard.exportData());
        assertEquals("LOSE state phải được lưu", GameState.LOSE, importedLose.getGameState());
    }

    /**
     * Test 6: Import nhiều lần liên tiếp từ cùng một data string
     */
    @Test
    public void testImport_MultipleTimes() {
        board.reveal(3, 3);
        board.toggleFlag(5, 5);

        String data = board.exportData();

        Board import1 = Board.importData(data);
        Board import2 = Board.importData(data);
        Board import3 = Board.importData(data);

        assertNotNull("Import lần 1", import1);
        assertNotNull("Import lần 2", import2);
        assertNotNull("Import lần 3", import3);

        assertEquals("Các import phải giống nhau về rows", import1.getRows(), import2.getRows());
        assertEquals("Flag count phải giống", import1.getFlagCount(), import2.getFlagCount());
    }

    /**
     * Test 7: Import board lớn (20x20)
     */
    @Test
    public void testImport_LargeBoard() {
        GameConfig largeConfig = new GameConfig(20, 20, 60);
        Board largeBoard = new Board(largeConfig);
        largeBoard.reveal(10, 10);

        String data = largeBoard.exportData();
        Board imported = Board.importData(data);

        assertNotNull("Import large board", imported);
        assertEquals(20, imported.getRows());
        assertEquals(20, imported.getCols());
        assertEquals(60, imported.getMineCount());
    }

    /**
     * Test 8: Import sau khi export nhiều lần (multiple cycles)
     */
    @Test
    public void testExportImport_MultipleCycles() {
        board.reveal(2, 2);
        String data1 = board.exportData();
        Board b1 = Board.importData(data1);

        b1.toggleFlag(4, 4);
        String data2 = b1.exportData();
        Board b2 = Board.importData(data2);

        assertNotNull(b2);
        assertFalse(b2.isFirstMove());
        assertEquals(1, b2.getFlagCount());
    }

    private int countRevealedCells(Board b) {
        int count = 0;
        for (int r = 0; r < b.getRows(); r++) {
            for (int c = 0; c < b.getCols(); c++) {
                if (b.getGrid()[r][c].isRevealed()) {
                    count++;
                }
            }
        }
        return count;
    }
}
