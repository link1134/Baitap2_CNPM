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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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
		bv.getPauseItem().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (b.getGameState() == GameState.WIN || b.getGameState() == GameState.LOSE) {
					return;
				}

				if (b.getGameState() == GameState.RUNNING) {

					b.setGameState(GameState.PAUSE);

					bv.getPauseItem().setText("Unpause");

					bv.getOverlay().setVisible(true);

					timer.stop();
					if (bv.getHintBtn() != null) bv.getHintBtn().setEnabled(false);

				} else if (b.getGameState() == GameState.PAUSE) {

					b.setGameState(GameState.RUNNING);

					bv.getPauseItem().setText("Pause");

					bv.getOverlay().setVisible(false);

					if (!b.isFirstMove()) {
						timer.start();
					}
					if (bv.getHintBtn() != null) bv.getHintBtn().setEnabled(true);
				}
			}
		});
		bv.getSmileBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				restartGame();
			}
		});

		// Xử lý nút Hint (UC_09_GH)
		bv.getHintBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 9.0.1 Người chơi nhấn nút Hint.
				// 9.0.2 BoardView gửi sự kiện → Controller nhận.
				if (b.getGameState() != GameState.RUNNING) {
					return;
				}
				// 9.0.6 Gọi giveHint (bên trong sẽ reveal ô an toàn)
				boolean didHint = b.giveHint();
				// 9.0.7 Cập nhật giao diện sau hint (có thể flood win/lose)
				bv.refreshBoard();
				updateBombUI();

				if (!didHint && b.getGameState() == GameState.RUNNING) {
					JOptionPane.showMessageDialog(bv, "Không có gợi ý phù hợp lúc này.", "Hint",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		bv.getExportItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GameState oldState = b.getGameState();
				if (oldState == GameState.RUNNING) {
					b.setGameState(GameState.PAUSE);
				}
				String data = b.exportData();
				b.setGameState(oldState);
				JTextArea area = new JTextArea(data);
				area.setLineWrap(true);
				area.setWrapStyleWord(true);
				area.setEditable(false);
				JOptionPane.showMessageDialog(bv, new JScrollPane(area), "Export Data",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		bv.getImportItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String data = JOptionPane.showInputDialog(bv, "Nhập chuỗi save:", "Import Game",
						JOptionPane.PLAIN_MESSAGE);
				if (data == null || data.trim().isBlank()) {
					return;
				}
				Board importedBoard = Board.importData(data.trim());
				if (importedBoard == null) {
					JOptionPane.showMessageDialog(bv, "Dữ liệu save không hợp lệ!", "Import thất bại",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (timer != null) {
					timer.stop();
				}
				bv.dispose();
				BoardView newView = new BoardView(importedBoard);
				BoardController controller = new BoardController(newView, importedBoard);
				if (importedBoard.getGameState() == GameState.PAUSE) {
					newView.getPauseItem().setText("Unpause");
					newView.getOverlay().setVisible(true);
					if (newView.getHintBtn() != null) newView.getHintBtn().setEnabled(false);
				} else {
					if (newView.getHintBtn() != null) newView.getHintBtn().setEnabled(true);
				}
				controller.updateView();
				String text = String.format("%03d", importedBoard.getElapsedTime());
				newView.getLbtime().setNumber(text);
				int remain = importedBoard.getRemainingMines();
				String mineText = String.format("%03d", remain);
				newView.getLbbomb().setNumber(mineText);
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
						// 8.2.0 / 4.x Tại bước nhấp chuột phải (hoặc trái) nhưng game không RUNNING → chặn.
						if (b.getGameState() != GameState.RUNNING) {
							return;
						} 
						if (e.getButton() == MouseEvent.BUTTON1) { // Left-click
							boolean firstMove = b.isFirstMove();

							b.reveal(r, c);

							if (firstMove) {
								timer.start();
							}

						} else if (e.getButton() == MouseEvent.BUTTON3) { // Right-click
							// 4.0.1 Người chơi nhấp chuột phải vào một ô.
							// 4.0.2 BoardView tiếp nhận sự kiện chuột và gửi tín hiệu đến Controller.
							// 8.1.1 / 8.1.2 Tương tự cho trường hợp ô đang có cờ (gỡ cờ).
							// 4.0.3 / 8.1.3 Controller gọi toggleFlag trên Board.
							b.toggleFlag(r, c);
						}

						// 4.0.10 / 8.1.9 Controller yêu cầu BoardView refreshBoard + updateBombUI.
						bv.refreshBoard();
						updateBombUI();
					}
				});
			}
		}
	}

	protected void updateView() {
		bv.refreshBoard();
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
