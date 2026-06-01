package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.Board;
import model.Cell;
import model.GameState;

public class BoardView extends JFrame {
    private JPanel p1, p2, p11, p12, p13;
    private PauseOverlay overlay;
    private LoadData data;
    private Board board;
    private int cellSize;
    private JButton[][] cells;
    private JButton smileBtn;
    private LabelNumber lbtime, lbbomb;
    private JMenuItem easy, medium, hard, exit, importItem, exportItem, pauseItem;

    public BoardView(Board board) {
        this.board = board;
        data = new LoadData();
        setIconImage(data.getListImage().get("title"));
        initializeMenu();
        initializeFrame();
        initializeBoard();
        setVisible(true);
    }

    private void initializeMenu() {
        // TODO Auto-generated method stub
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("Game");
        JMenu gameMenu2 = new JMenu("New Game");
        easy = new JMenuItem("Easy");
        medium = new JMenuItem("Medium");
        hard = new JMenuItem("Hard");
        exit = new JMenuItem("Exit");
        importItem = new JMenuItem("Import");
        exportItem = new JMenuItem("Export");
        pauseItem = new JMenuItem("Pause");
        gameMenu2.add(easy);
        gameMenu2.add(medium);
        gameMenu2.add(hard);
        gameMenu.add(pauseItem);
        gameMenu.add(importItem);
        gameMenu.add(exportItem);
        gameMenu.addSeparator();
        gameMenu.add(exit);

        menuBar.add(gameMenu);
        menuBar.add(gameMenu2);

        setJMenuBar(menuBar);
    }

    private void initializeFrame() {
        setTitle("Minesweeper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initializeBoard() {
        setLayout(new BorderLayout(20, 20));

        add(p1 = new JPanel(new BorderLayout()), BorderLayout.NORTH);

        p1.setBorder(BorderFactory.createLoweredBevelBorder());

        p1.add(p11 = new JPanel(), BorderLayout.WEST);
        p1.add(p12 = new JPanel(), BorderLayout.EAST);
        p1.add(p13 = new JPanel(), BorderLayout.CENTER);

        p11.add(lbbomb = new LabelNumber(this, "000"));
        p12.add(lbtime = new LabelNumber(this, "000"));

        Image smileImg = data.getListImage().get("smile").getScaledInstance(50, 50, Image.SCALE_SMOOTH);

        smileBtn = new JButton(new ImageIcon(smileImg));

        smileBtn.setBorderPainted(false);
        smileBtn.setFocusPainted(false);
        smileBtn.setContentAreaFilled(false);
        smileBtn.setMargin(new Insets(0, 0, 0, 0));

        p13.add(smileBtn);

        int rows = board.getRows();
        int cols = board.getCols();

        cells = new JButton[rows][cols];

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(rows, cols));

        if (cols >= 30) {
            cellSize = 25;
        } else if (cols >= 16) {
            cellSize = 32;
        } else {
            cellSize = 40;
        }

        for (int row = 0; row < rows; row++) {

            for (int col = 0; col < cols; col++) {

                JButton button = new JButton();

                button.setPreferredSize(new Dimension(cellSize, cellSize));

                cells[row][col] = button;

                Image scaled = data.getListImage().get("noUse").getScaledInstance(cellSize, cellSize,
                        Image.SCALE_SMOOTH);

                button.setBorderPainted(false);
                button.setFocusPainted(false);
                button.setContentAreaFilled(false);

                button.setMargin(new Insets(0, 0, 0, 0));

                button.setIcon(new ImageIcon(scaled));

                boardPanel.add(button);
            }
        }

        boardPanel.setBorder(BorderFactory.createLoweredBevelBorder());

        // ===== OVERLAY =====

        overlay = new PauseOverlay();
        overlay.setVisible(false);

        JLayeredPane layeredPane = new JLayeredPane();

        Dimension size = boardPanel.getPreferredSize();

        layeredPane.setPreferredSize(size);

        boardPanel.setBounds(0, 0, size.width, size.height);
        overlay.setBounds(0, 0, size.width, size.height);

        layeredPane.add(boardPanel, Integer.valueOf(0));
        layeredPane.add(overlay, Integer.valueOf(1));

        add(layeredPane, BorderLayout.CENTER);

        pack();

        setLocationRelativeTo(null);
    }

    public void refreshBoard() {
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                JButton btn = cells[row][col];
                Cell cell = board.getGrid()[row][col];
                if (cell.isFlagged() && !cell.isMine() && board.getGameState() != GameState.RUNNING) {
                    btn.setIcon(getScaledIcon("boomX"));

                } else if (!cell.isRevealed()) {
                    if (cell.isFlagged()) {
                        btn.setIcon(getScaledIcon("co"));
                    } else {
                        btn.setIcon(getScaledIcon("noUse"));
                    }
                } else {
                    if (cell.isMine()) {
                        if (cell.isExploded()) {
                            btn.setIcon(getScaledIcon("boomRed"));
                        } else {
                            btn.setIcon(getScaledIcon("boom"));
                        }
                    } else {
                        String key = "b" + cell.getNearbyMines();
                        btn.setIcon(getScaledIcon(key));
                    }
                }
                btn.repaint();
            }
        }
        if (board.getGameState() == GameState.WIN) {
            smileBtn.setIcon(getSmileIcon("smileWin"));
            JOptionPane.showMessageDialog(this, "Bạn đã thắng !");
            pauseItem.setEnabled(false);

        } else if (board.getGameState() == GameState.LOSE) {
            smileBtn.setIcon(getSmileIcon("smileLose"));
            JOptionPane.showMessageDialog(this, "Bạn đã thua !");
            pauseItem.setEnabled(false);
        }
    }

    private ImageIcon getScaledIcon(String key) {
        Image img = data.getListImage().get(key).getScaledInstance(cellSize, cellSize,
                Image.SCALE_SMOOTH);

        return new ImageIcon(img);
    }

    private ImageIcon getSmileIcon(String key) {
        Image img = data.getListImage().get(key).getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public JMenuItem getEasy() {
        return easy;
    }

    public void setEasy(JMenuItem easy) {
        this.easy = easy;
    }

    public JMenuItem getMedium() {
        return medium;
    }

    public void setMedium(JMenuItem medium) {
        this.medium = medium;
    }

    public JMenuItem getHard() {
        return hard;
    }

    public void setHard(JMenuItem hard) {
        this.hard = hard;
    }

    public JMenuItem getExit() {
        return exit;
    }

    public void setExit(JMenuItem exit) {
        this.exit = exit;
    }

    public JMenuItem getImportItem() {
        return importItem;
    }

    public void setImportItem(JMenuItem importItem) {
        this.importItem = importItem;
    }

    public JMenuItem getExportItem() {
        return exportItem;
    }

    public void setExportItem(JMenuItem exportItem) {
        this.exportItem = exportItem;
    }

    public JPanel getP1() {
        return p1;
    }

    public void setP1(JPanel p1) {
        this.p1 = p1;
    }

    public JPanel getP2() {
        return p2;
    }

    public void setP2(JPanel p2) {
        this.p2 = p2;
    }

    public JPanel getP11() {
        return p11;
    }

    public void setP11(JPanel p11) {
        this.p11 = p11;
    }

    public JPanel getP12() {
        return p12;
    }

    public void setP12(JPanel p12) {
        this.p12 = p12;
    }

    public JPanel getP13() {
        return p13;
    }

    public void setP13(JPanel p13) {
        this.p13 = p13;
    }

    public LoadData getData() {
        return data;
    }

    public void setData(LoadData data) {
        this.data = data;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public JButton getSmileBtn() {
        return smileBtn;
    }

    public void setSmileBtn(JButton smileBtn) {
        this.smileBtn = smileBtn;
    }

    public LabelNumber getLbtime() {
        return lbtime;
    }

    public void setLbtime(LabelNumber lbtime) {
        this.lbtime = lbtime;
    }

    public LabelNumber getLbbomb() {
        return lbbomb;
    }

    public void setLbbomb(LabelNumber lbbomb) {
        this.lbbomb = lbbomb;
    }

    public void setCells(JButton[][] cells) {
        this.cells = cells;
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    public JButton[][] getCells() {
        return cells;
    }

    public JMenuItem getPauseItem() {
        return pauseItem;
    }

    public void setPauseItem(JMenuItem pauseItem) {
        this.pauseItem = pauseItem;
    }

    public PauseOverlay getOverlay() {
        return overlay;
    }
}
