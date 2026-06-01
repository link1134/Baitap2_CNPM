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

				} else if (b.getGameState() == GameState.PAUSE) {

					b.setGameState(GameState.RUNNING);

					bv.getPauseItem().setText("Pause");

					bv.getOverlay().setVisible(false);

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
					public void mousePressed(MouseEvent e) {
						// [UC_08_GC - Luồng thay thế]: 8.2.0 Tại bước 8.1.1, nếu người chơi nhấp gỡ cờ nhưng ván chơi
						// đã kết thúc (thắng hoặc thua).
						// 8.2.1 Hệ thống chặn sự kiện chuột phải, giữ nguyên hiện trạng và không thực hiện
						// thay đổi trạng thái của ô.
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
							// [UC_04_DC]: 4.0.1 Người chơi thực hiện thao tác nhấp chuột phải vào một ô trên
							// giao diện bàn chơi.
							// [UC_04_DC]: 4.0.2 Hệ thống (BoardView) tiếp nhận sự kiện chuột và gửi tín hiệu
							// xử lý nút bấm đến bộ điều khiển (BoardController).
							// [UC_08_GC]: 8.1.1 Người chơi thực hiện thao tác nhấp chuột phải vào một ô đang có
							// cờ trên giao diện bàn chơi.
							// [UC_08_GC]: 8.1.2 Hệ thống (BoardView) tiếp nhận sự kiện chuột và gửi tín hiệu
							// xử lý nút bấm đến bộ điều khiển (BoardController).

							// [UC_04_DC]: 4.0.3 Bộ điều khiển gọi hàm bắt đầu xử lý sự kiện trên đối tượng
							// bàn chơi (Board).
							// [UC_08_GC]: 8.1.3 Bộ điều khiển gọi hàm bắt đầu xử lý sự kiện trên đối tượng
							// bàn chơi (Board).
							b.toggleFlag(r, c);
						}

						// [UC_04_DC]: 4.0.10 Bộ điều khiển yêu cầu cập nhật lại giao diện (hiển thị hình lá cờ
						// tại ô vừa nhấp và cập nhật bộ đếm cờ).
						// [UC_08_GC]: 8.1.9 Bộ điều khiển yêu cầu BoardView cập nhật lại giao diện hiển thị
						// (xóa hình ảnh lá cờ trên ô và tăng số trên bộ đếm).
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
