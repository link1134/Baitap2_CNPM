package controller;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import model.Board;
import model.Cell;
import model.Difficulty;
import model.GameState;
import view.BoardView;

public class BoardController {
	private BoardView bv;
	private Board b;
	private Timer timer;

	public BoardController(BoardView bv, Board b) {
		this.bv = bv;
		this.b = b;

		init();

	}

	private void init() {
		bv.getSmileBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				restartGame();
			}
		});
		bv.getEasy().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				startNewGame(Difficulty.EASY);
			}
		});

		bv.getMedium().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				startNewGame(Difficulty.MEDIUM);
			}
		});

		bv.getHard().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				startNewGame(Difficulty.HARD);
			}
		});
		timer = new Timer(1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (b.getGameState() != GameState.RUNNING) {
					timer.stop();
					return;
				}

				b.increaseTime();

				String text = String.format("%03d", b.getElapsedTime());

				bv.getLbtime().setNumber(text);
			}
		});
		for (int row = 0; row < bv.getCells().length; row++) {
			for (int col = 0; col < bv.getCells()[0].length; col++) {
				int r = row;
				int c = col;
				bv.getCells()[row][col].addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (b.getGameState() != GameState.RUNNING) {
							return;
						} else if (e.getButton() == MouseEvent.BUTTON1) {
							boolean firstMove = b.isFirstMove();

							b.reveal(r, c);

							if (firstMove) {
								timer.start();
							}

						}
						updateView();
						updateBombUI();
					}
				});
			}
		}
	}

	protected void updateView() {

		for (int row = 0; row < b.getRows(); row++) {
			for (int col = 0; col < b.getCols(); col++) {
				JButton btn = bv.getCells()[row][col];
				Cell cell = b.getGrid()[row][col];
				if (cell.isFlagged() && !cell.isMine() && b.getGameState() != GameState.RUNNING) {
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
		if (b.getGameState() == GameState.WIN) {
			bv.getSmileBtn().setIcon(getSmileIcon("smileWin"));
			JOptionPane.showMessageDialog(bv, "Bạn đã thắng !");
			bv.getPauseItem().setEnabled(false);

		} else if (b.getGameState() == GameState.LOSE) {
			bv.getSmileBtn().setIcon(getSmileIcon("smileLose"));
			JOptionPane.showMessageDialog(bv, "Bạn đã thua !");
			bv.getPauseItem().setEnabled(false);
		}
	}

	private ImageIcon getScaledIcon(String key) {
		Image img = bv.getData().getListImage().get(key).getScaledInstance(bv.getCellSize(), bv.getCellSize(),
				Image.SCALE_SMOOTH);

		return new ImageIcon(img);
	}

	private ImageIcon getSmileIcon(String key) {
		Image img = bv.getData().getListImage().get(key).getScaledInstance(50, 50, Image.SCALE_SMOOTH);
		return new ImageIcon(img);
	}

	private void updateBombUI() {
		int remain = b.getRemainingMines();
		String text = String.format("%03d", remain);
		bv.getLbbomb().setNumber(text);
		bv.getLbbomb().repaint();
	}

	private void restartGame() {

		if (timer != null) {
			timer.stop();
		}

		bv.dispose();

		Board newBoard = new Board(b.getDifficulty());
		BoardView newView = new BoardView(newBoard);
		new BoardController(newView, newBoard);
	}

	private void startNewGame(Difficulty difficulty) {

		if (timer != null) {
			timer.stop();
		}
		bv.dispose();
		Board newBoard = new Board(difficulty);
		BoardView newView = new BoardView(newBoard);
		new BoardController(newView, newBoard);
	}

	public BoardView getBv() {
		return bv;
	}

	public void setBv(BoardView bv) {
		this.bv = bv;
	}

	public Board getB() {
		return b;
	}

	public void setB(Board b) {
		this.b = b;
	}

}
