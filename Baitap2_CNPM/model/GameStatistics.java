package model;

import java.io.*;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * UC_08_TS - THỐNG KÊ MINESWEEPER
 * Quản lý và hiển thị thống kê kết quả chơi game:
 * - Số ván thắng, thua
 * - Chuỗi thắng hiện tại
 * - Chuỗi thắng dài nhất
 */
public class GameStatistics {
    private static final String FILE_PATH = "statistics.properties";

    private int wins = 0;
    private int losses = 0;
    private int currentStreak = 0;
    private int bestStreak = 0;

    public GameStatistics() {
    	// 8.0.1 Hệ thống tạo hoặc tải dữ liệu thống kê
        load();
    }
    
    /**
     * 8.0.1 & 8.5.1 - Tải dữ liệu thống kê từ file
     */
    private void load() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(FILE_PATH)) {
            props.load(fis);
            wins = Integer.parseInt(props.getProperty("wins", "0"));
            losses = Integer.parseInt(props.getProperty("losses", "0"));
            currentStreak = Integer.parseInt(props.getProperty("currentStreak", "0"));
            bestStreak = Integer.parseInt(props.getProperty("bestStreak", "0"));
        } catch (Exception e) {
        	// 8.5.1 Nếu File chưa tồn tại hoặc lỗi thì sẽ dùng giá trị mặc định 0 
        }
    }
    
    /**
     * 8.2.1 & 8.3.1 - Lưu dữ liệu thống kê vào file
     */
    private void save() {
        Properties props = new Properties();
        props.setProperty("wins", String.valueOf(wins));
        props.setProperty("losses", String.valueOf(losses));
        props.setProperty("currentStreak", String.valueOf(currentStreak));
        props.setProperty("bestStreak", String.valueOf(bestStreak));

        try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
            props.store(fos, "Minesweeper Statistics");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 8.2.1 - Ghi nhận khi người chơi thắng một ván
     */
    public void recordWin() {
    	// 8.2.2 Tăng số ván thắng lên 1
        wins++;
        // 8.2.3 Tăng chuỗi thắng hiện tại lên 1
        currentStreak++;
        // 8.2.4 Nếu chuỗi thắng hiện tại lớn hơn chuỗi thắng dài nhất thì cập nhật bestStreak
        if (currentStreak > bestStreak) {
        	bestStreak = currentStreak;
        }
        // Lưu lại
        save();
    }
    /**
     * 8.3.1 - Ghi nhận khi người chơi thua một ván
     */
    public void recordLoss() {
    	// 8.3.2 Tăng số ván thua lên 1
        losses++;
        // 8.3.3 Reset chuỗi thắng hiện tại về 0
        currentStreak = 0;
        // Lưu lại
        save();
    }
    
    /**
     * 8.0.2 - Hiển thị thống kê cho người chơi
     * Main Flow: 8.0.3 → 8.0.5
     */
    public void showStatistics() {
        String message = String.format("""
                THỐNG KÊ MINESWEEPER
                Thắng: %d ván
                Thua: %d ván
                Chuỗi thắng hiện tại: %d
                Chuỗi thắng dài nhất: %d
                """, wins, losses, currentStreak, bestStreak);

        JOptionPane.showMessageDialog(null, message, "Thống kê Minesweeper", JOptionPane.INFORMATION_MESSAGE);
    }

	public int getWins() {
		return wins;
	}

	public int getLosses() {
		return losses;
	}

	public int getCurrentStreak() {
		return currentStreak;
	}

	public int getBestStreak() {
		return bestStreak;
	}
    
}
