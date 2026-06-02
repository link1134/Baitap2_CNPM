package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import model.Difficulty;

public class StartMenu extends JFrame {
    private LoadData data;
    private JComboBox<Difficulty> difficultyBox;
    private JButton startButton;
    private JTextField rowField;
    private JTextField colField;
    private JTextField mineField;

    private JPanel customPanel;

    public StartMenu() {
        data = new LoadData();
        setIconImage(data.getListImage().get("title"));
        initializeFrame();
        initializeComponents();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Minesweeper");
        setSize(500, 500);
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
        
        customPanel = new JPanel(new GridLayout(3,2,10,10));
        customPanel.setAlignmentX(CENTER_ALIGNMENT);
        customPanel.setBackground(new Color(35,35,35));

        customPanel.add(new JLabel("Rows"));
        rowField = new JTextField("9");
        customPanel.add(rowField);

        customPanel.add(new JLabel("Columns"));
        colField = new JTextField("9");
        customPanel.add(colField);

        customPanel.add(new JLabel("Mines"));
        mineField = new JTextField("10");
        customPanel.add(mineField);

        customPanel.setVisible(false);
        for(Component c : customPanel.getComponents()) {
            if(c instanceof JLabel label) {
                label.setForeground(Color.WHITE);
            }
        }
        centerPanel.add(customPanel);
        
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

    public JComboBox<Difficulty> getDifficultyBox() {
        return difficultyBox;
    }

    public void setDifficultyBox(JComboBox<Difficulty> difficultyBox) {
        this.difficultyBox = difficultyBox;
    }

    public void setStartButton(JButton startButton) {
        this.startButton = startButton;
    }

    public JButton getStartButton() {
        return startButton;
    }
    
    public LoadData getData() {
		return data;
	}

	public void setData(LoadData data) {
		this.data = data;
	}

	public JTextField getRowField() {
		return rowField;
	}

	public void setRowField(JTextField rowField) {
		this.rowField = rowField;
	}

	public JTextField getColField() {
		return colField;
	}

	public void setColField(JTextField colField) {
		this.colField = colField;
	}

	public JTextField getMineField() {
		return mineField;
	}

	public void setMineField(JTextField mineField) {
		this.mineField = mineField;
	}

	public JPanel getCustomPanel() {
		return customPanel;
	}

	public void setCustomPanel(JPanel customPanel) {
		this.customPanel = customPanel;
	}

	public static void main(String[] args) {
        StartMenu sm = new StartMenu();
    }
}
