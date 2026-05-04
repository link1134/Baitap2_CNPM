package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import model.Board;
import model.Cell;

public class BoardView extends JFrame {
    private Board board;
    private JButton[][] cells;
    public BoardView(Board board) {
        this.board = board;
        initializeFrame();
        initializeBoard();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initializeBoard() {
        int rows = board.getRows();
        int cols = board.getCols();
        cells = new JButton[rows][cols];
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(rows, cols));
        boardPanel.setBackground(new Color(40, 40, 40));
        int cellSize;
        if (cols >= 30) {
            cellSize = 25;
        }
        else if (cols >= 16) {
            cellSize = 32;
        }
        else {
            cellSize = 40;
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(cellSize, cellSize));
                button.setFocusPainted(false);
                button.setBackground(new Color(90, 90, 90));
                cells[row][col] = button;
                boardPanel.add(button);
            }
        }

        add(boardPanel);
        pack();
        setLocationRelativeTo(null);
    }

    public JButton[][] getCells() {
        return cells;
    }
}