package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import model.Difficulty;

public class StartMenu extends JFrame {

    private JComboBox<Difficulty> difficultyBox;
    private JButton startButton;

    public StartMenu() {
        initializeFrame();
        initializeComponents();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Minesweeper");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    private void initializeComponents() {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(35, 35, 35));
        JLabel titleLabel = new JLabel("MINESWEEPER");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(35, 35, 35));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));
        JLabel difficultyLabel = new JLabel("Select Difficulty");
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        difficultyLabel.setForeground(Color.WHITE);
        difficultyLabel.setAlignmentX(CENTER_ALIGNMENT);
        centerPanel.add(difficultyLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        difficultyBox = new JComboBox<>(Difficulty.values());
        difficultyBox.setMaximumSize(new Dimension(300, 40));
        difficultyBox.setFont(new Font("Arial", Font.PLAIN, 16));
        centerPanel.add(difficultyBox);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        startButton = new JButton("START GAME");
        startButton.setFocusPainted(false);
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setBackground(new Color(70, 70, 70));
        startButton.setForeground(Color.WHITE);
        startButton.setAlignmentX(CENTER_ALIGNMENT);
        startButton.setMaximumSize(new Dimension(300, 50));
        centerPanel.add(startButton);
        JLabel footerLabel = new JLabel("Java Swing Minesweeper");
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        footerLabel.setForeground(new Color(170, 170, 170));
        footerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerLabel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    public Difficulty getSelectedDifficulty() {
        return (Difficulty) difficultyBox.getSelectedItem();
    }

    public JButton getStartButton() {
        return startButton;
    }
    public static void main(String[] args) {
		StartMenu sm = new StartMenu();
	}
}
