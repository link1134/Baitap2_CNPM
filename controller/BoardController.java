package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.Board;
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
			//7.1 và 7.2 Xử lí sự kiện người chơi nhấn nút Pause.
			@Override
			public void actionPerformed(ActionEvent e) {
				//Từ 7.3 đến 7.14: Thực hiện pause hoặc unpause tùy theo điều kiện.
				
				if (b.getGameState() == GameState.WIN || b.getGameState() == GameState.LOSE) {
					return;
				}
				//7.3 đến 7.8: Thực hiện Pause game khi trạng thái game đang là running.
				//7.3: Gọi Board, lấy trạng thái của game để xác định game có đang là running không.
				if (b.getGameState() == GameState.RUNNING) {
					//7.5: Khi 7.3 trả ra kết quả 7.4 là running, gọi Board đặt trạng thái game sang Pause.
					b.setGameState(GameState.PAUSE);
					//7.6: Gọi View, đặt tên của Pause item thành Unpause.
					bv.getPauseItem().setText("Unpause");
					//7.7: Gọi View, chuyển trạng thái của màn che màn hình thành true.
					bv.getOverlay().setVisible(true);
					//7.8: Dừng bộ đếm thời gian.
					timer.stop();
					
					//7.9 đến 7.14: Thực hiện Unpause game khi trạng thái game đang là pause.
					// 7.9: Gọi Board, lấy trạng thái của game để xác định game có đang là pause không.
				} else if (b.getGameState() == GameState.PAUSE) {
					//7.11: Khi 7.9 trả ra kết quả 7.10 là pause, gọi Board đặt trạng thái game sang Pause.
					b.setGameState(GameState.RUNNING);
					//7.12: Gọi View, đặt tên của Pause item thành Pause.
					bv.getPauseItem().setText("Pause");
					//7.13: Gọi View, chuyển trạng thái của màn che màn hình thành false.
					bv.getOverlay().setVisible(false);
					//7.14: Nếu bàn chơi chưa được mở ô nào, thì bộ đếm thời gian sẽ ko được phép mở lại, ngược lại thì được.
					if (!b.isFirstMove()) {
						timer.start();
					}
				}
			}
		});
		bv.getSmileBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				restartGame();
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
					// 3.1: Xử lí người dùng nhấn vào ô
					public void mousePressed(MouseEvent e) {
						if (b.getGameState() != GameState.RUNNING) {
							return;
						}
						
						if (e.getButton() == MouseEvent.BUTTON1) { // Left-click
							boolean firstMove = b.isFirstMove();
							// 3.2: Chuyển hướng xử lí sang lớp Board.
							b.reveal(r, c);

							if (firstMove) {
								timer.start();
							}

						} else if (e.getButton() == MouseEvent.BUTTON3) { // Right-click
							b.toggleFlag(r, c);
						}
						// 3.11: Phương thức gọi view cập nhật giao diện
						bv.refreshBoard();
						// 3.12: Cập nhật số mìn còn lại.
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
