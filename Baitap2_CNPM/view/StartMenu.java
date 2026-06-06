package view;

import model.Difficulty;

import javax.swing.*;
import java.awt.*;

/**
 * StartMenu là giao diện màn hình bắt đầu.
 * Người chơi chọn độ khó và nhấn Start Game để bắt đầu.
 */
public class StartMenu extends JFrame {

    private JComboBox<Difficulty> difficultyBox;
    private JButton startButton;

    public StartMenu() {
        setTitle("Minesweeper");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("MINESWEEPER");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Combobox hiển thị danh sách độ khó EASY, MEDIUM, HARD
        difficultyBox = new JComboBox<>(Difficulty.values());
        difficultyBox.setMaximumSize(new Dimension(250, 35));
        difficultyBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Nút bắt đầu game
        startButton = new JButton("Start Game");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(40));
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));
        panel.add(difficultyBox);
        panel.add(Box.createVerticalStrut(20));
        panel.add(startButton);

        add(panel);
    }

    /**
     * Lấy độ khó mà người chơi đã chọn.
     */
    public Difficulty getSelectedDifficulty() {
        return (Difficulty) difficultyBox.getSelectedItem();
    }

    public JButton getStartButton() {
        return startButton;
    }
}
