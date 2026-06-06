package Test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import model.Board;
import model.GameConfig;
import model.GameState;

/**
 * Test class cho chức năng EXPORT (Export màn chơi)
 * Use Case liên quan: Export Game Data
 */
public class ExportTest {

    private Board board;
    private static final int ROWS = 9;
    private static final int COLS = 9;

    @Before
    public void setUp() {
        GameConfig config = new GameConfig(ROWS, COLS, 10);
        board = new Board(config);
    }

    // =========================================================
    // TEST CHỨC NĂNG EXPORT
    // =========================================================

    /**
     * Test 1: Export board mới (chưa chơi) → chuỗi không rỗng và có format đúng
     */
    @Test
    public void testExport_BoardMoi() {
        String data = board.exportData();
        assertNotNull("Export phải trả về chuỗi", data);
        assertFalse("Chuỗi export không được rỗng", data.trim().isEmpty());
        assertTrue("Phải có dấu phẩy phân cách version", data.contains(","));
    }

    /**
     * Test 2: Export sau khi chơi (reveal, flag, time) → dữ liệu phải thay đổi
     */
    @Test
    public void testExport_SauGameplay() {
        // Trước khi chơi
        String dataBefore = board.exportData();
        
        // Thực hiện hành động
        board.reveal(4, 4);
        board.toggleFlag(2, 2);
        board.toggleFlag(3, 3);
        board.increaseTime();
        board.increaseTime();
        
        String dataAfter = board.exportData();
        
        assertNotNull("Export sau gameplay", dataAfter);
        assertNotEquals("Dữ liệu export phải khác nhau sau khi chơi", dataBefore, dataAfter);
    }

    /**
     * Test 3: Export khi game ở các trạng thái khác nhau
     */
    @Test
    public void testExport_GameStates() {
        // Test WIN
        Board winBoard = new Board(new GameConfig(1, 1, 0));
        winBoard.reveal(0, 0);
        String winData = winBoard.exportData();
        assertNotNull("Export WIN state", winData);
        
        // Test PAUSE
        Board pauseBoard = new Board(new GameConfig(ROWS, COLS, 10));
        pauseBoard.setGameState(GameState.PAUSE);
        String pauseData = pauseBoard.exportData();
        assertNotNull("Export PAUSE state", pauseData);
        
        // Test LOSE
        Board loseBoard = new Board(new GameConfig(ROWS, COLS, 10));
        loseBoard.setGameState(GameState.LOSE);
        String loseData = loseBoard.exportData();
        assertNotNull("Export LOSE state", loseData);
    }

    /**
     * Test 4: Export board lớn (20x20)
     */
    @Test
    public void testExport_LargeBoard() {
        GameConfig largeConfig = new GameConfig(20, 20, 60);
        Board largeBoard = new Board(largeConfig);
        largeBoard.reveal(10, 10);
        largeBoard.toggleFlag(15, 15);
        
        String data = largeBoard.exportData();
        assertNotNull("Export large board", data);
        assertTrue("Chuỗi export board lớn phải dài hơn", data.length() > 100);
    }

    /**
     * Test 5: Export nhiều lần → kết quả phải giống nhau (nếu trạng thái không đổi)
     */
    @Test
    public void testExport_MultipleTimes() {
        board.reveal(3, 3);
        
        String data1 = board.exportData();
        String data2 = board.exportData();
        String data3 = board.exportData();
        
        assertEquals("Export nhiều lần phải ra cùng chuỗi", data1, data2);
        assertEquals("Export nhiều lần phải ra cùng chuỗi", data1, data3);
    }

    /**
     * Test 6: Export sau khi import (Export → Import → Export)
     */
    @Test
    public void testExport_AfterImport() {
        board.reveal(2, 2);
        board.toggleFlag(5, 5);
        
        String originalExport = board.exportData();
        Board imported = Board.importData(originalExport);
        String afterImportExport = imported.exportData();
        
        assertNotNull("Export sau import", afterImportExport);
        assertEquals("Export trước và sau import phải giống nhau", originalExport, afterImportExport);
    }

    /**
     * Test 7: Export khi firstMove = true và firstMove = false
     */
    @Test
    public void testExport_FirstMoveStatus() {
        // Chưa chơi
        String data1 = board.exportData();
        assertTrue("Board ban đầu firstMove = true", board.isFirstMove());
        
        // Sau first move
        board.reveal(4, 4);
        String data2 = board.exportData();
        
        assertNotEquals("Export phải khác nhau khi firstMove thay đổi", data1, data2);
    }
}
