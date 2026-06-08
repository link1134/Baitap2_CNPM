package Test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import model.Board;
import model.GameConfig;
import model.GameState;
import model.GameStatistics;

/**
 * Test class cho chức năng Thống kê (Statistics)
 * Use Case liên quan: UC_08_TS
 */
public class StatisticsTest {

    private GameStatistics statistics;

    @Before
    public void setUp() {
        statistics = new GameStatistics();
    }

    /**
     * Test 1: Record Win - Tăng số ván thắng và chuỗi thắng
     */
    @Test
    public void testStatistics_RecordWin_TangThongKeDung() {
        int winBefore = statistics.getWins();
        int streakBefore = statistics.getCurrentStreak();
        
        statistics.recordWin();
        
        assertEquals("Số ván thắng phải tăng 1", winBefore + 1, statistics.getWins());
        assertEquals("Chuỗi thắng hiện tại phải tăng 1", streakBefore + 1, statistics.getCurrentStreak());
    }

    /**
     * Test 2: Record Loss - Reset chuỗi thắng về 0
     */
    @Test
    public void testStatistics_RecordLoss_ResetChuoiThang() {
        statistics.recordWin();
        statistics.recordWin();
        
        statistics.recordLoss();
        
        assertEquals("Số ván thua phải tăng 1", 1, statistics.getLosses());
        assertEquals("Chuỗi thắng phải reset về 0 khi thua", 0, statistics.getCurrentStreak());
    }

    /**
     * Test 3: Best Streak - Cập nhật chuỗi thắng dài nhất
     */
    @Test
    public void testStatistics_BestStreak_DuocCapNhatDung() {
        statistics.recordWin();
        statistics.recordWin();
        statistics.recordWin();
        
        assertEquals("Best streak phải bằng current streak", 3, statistics.getBestStreak());
        
        statistics.recordLoss();
        statistics.recordWin();
        statistics.recordWin();
        
        assertEquals("Best streak vẫn giữ nguyên sau khi thua", 3, statistics.getBestStreak());
    }

    /**
     * Test 4: Thắng game → Statistics tự động cập nhật
     */
    @Test
    public void testStatistics_KhiThangGame_ThongKeTuDongTang() {
        Board winBoard = new Board(new GameConfig(1, 1, 0));
        int winBefore = winBoard.getStatistics().getWins();
        
        winBoard.reveal(0, 0); // Thắng ngay
        
        assertEquals("Khi thắng game, statistics phải tự động recordWin", 
                    winBefore + 1, winBoard.getStatistics().getWins());
    }

    /**
     * Test 5: Thua game → Statistics tự động cập nhật
     */
    @Test
    public void testStatistics_KhiThuaGame_ThongKeTuDongTang() {
        Board board = new Board(new GameConfig(5, 5, 5));
        int lossBefore = board.getStatistics().getLosses();
        
        board.reveal(0, 0);
        // Mở trúng mìn để thua
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                if (board.getGrid()[r][c].isMine()) {
                    board.reveal(r, c);
                    break;
                }
            }
        }
        
        assertEquals("Khi thua game, statistics phải tự động recordLoss", 
                    lossBefore + 1, board.getStatistics().getLosses());
    }

    /**
     * Test 6: Load thống kê từ file khi khởi tạo
     */
    @Test
    public void testStatistics_LoadTuFile_KhiKhoiTao() {
        GameStatistics stats = new GameStatistics();
        assertNotNull("GameStatistics phải load được dữ liệu", stats);
        assertTrue("Số ván thắng >= 0", stats.getWins() >= 0);
        assertTrue("Số ván thua >= 0", stats.getLosses() >= 0);
    }

    /**
     * Test 7: Xem thống kê khi chưa chơi ván nào
     */
    @Test
    public void testStatistics_ChuaChoiVanNao() {
        assertEquals("Khi chưa chơi, số thắng = 0", 0, statistics.getWins());
        assertEquals("Khi chưa chơi, số thua = 0", 0, statistics.getLosses());
        assertEquals("Khi chưa chơi, chuỗi thắng = 0", 0, statistics.getCurrentStreak());
    }
}
