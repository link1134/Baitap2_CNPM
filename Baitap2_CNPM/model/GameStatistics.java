package model;

import java.io.*;
import java.util.Properties;
import javax.swing.JOptionPane;

public class GameStatistics {
    private static final String FILE_PATH = "statistics.properties";

    private int wins = 0;
    private int losses = 0;
    private int currentStreak = 0;
    private int bestStreak = 0;

    public GameStatistics() {
        load();
    }

    private void load() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(FILE_PATH)) {
            props.load(fis);
            wins = Integer.parseInt(props.getProperty("wins", "0"));
            losses = Integer.parseInt(props.getProperty("losses", "0"));
            currentStreak = Integer.parseInt(props.getProperty("currentStreak", "0"));
            bestStreak = Integer.parseInt(props.getProperty("bestStreak", "0"));
        } catch (Exception e) {
            // File chưa tồn tại → dùng mặc định
        }
    }

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

    public void recordWin() {
        wins++;
        currentStreak++;
        if (currentStreak > bestStreak) bestStreak = currentStreak;
        save();
    }

    public void recordLoss() {
        losses++;
        currentStreak = 0;
        save();
    }

    public void showStatistics() {
        String message = String.format("""
                THỐNG KÊ MINESWEEPER
                Thắng: %d ván
                Thua: %d ván
                Chuỗi thắng hiện tại: %d
                Chuỗi thắng dài nhất: %d
                """, wins, losses, currentStreak, bestStreak);

        JOptionPane.showMessageDialog(null, message, "Thống kê Minesweeper", 
                JOptionPane.INFORMATION_MESSAGE);
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