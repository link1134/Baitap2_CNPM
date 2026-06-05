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

	// ─── KHỞI TẠO CÁC ĐIỀU KHIỂN ─────────────────────────────────────
	private void init() {
		initPauseControl();
		initSmileButton();
		initHintButton();
		initExportControl();
		initImportControl();
		initDifficultyButtons();
		initTimer();
		initCellListeners();
	}

	// ─── 1. TẠM DỪNG / TIẾP TỤC (Pause/Unpause) ─────────────────────
	private void initPauseControl() {
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
	}

	// ─── 2. CHƠI LẠI (Restart) ───────────────────────────────────────
	private void initSmileButton() {
		bv.getSmileBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				restartGame();
			}
		});
	}    // ─── 9. GỢI Ý (UC_09_GH) ─────────────────────────────────────────────
    private void initHintButton() {
        bv.getHintBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 9.0.1 Người chơi thực hiện thao tác nhấp vào nút "Gợi ý" (Hint) trên giao diện bàn chơi.
                // 9.0.2 Hệ thống (BoardView) tiếp nhận sự kiện kích hoạt và gửi yêu cầu lấy gợi ý đến bộ điều khiển (BoardController).
                // 9.2.0 Tại bước 9.0.3, nếu ván chơi không ở trạng thái RUNNING hoặc bàn cờ không còn ô an toàn nào khác để gợi ý.
                if (b.getGameState() != GameState.RUNNING) {
                    // 9.2.1 Hệ thống chặn yêu cầu gợi ý, không thực hiện hành động và bỏ qua sự kiện.
                    // 9.2.2 Use case kết thúc.
                    return;
                }
                // 9.0.3 Bộ điều khiển gọi hàm xử lý tính toán gợi ý trên đối tượng bàn chơi (Board).
                boolean didHint = b.giveHint();
                // 9.0.6 Bộ điều khiển tiếp nhận tọa độ ô gợi ý từ hệ thống (thông qua kết quả trả về).
                // 9.0.9 Hệ thống hoàn tất quá trình xử lý và cập nhật lại toàn bộ giao diện bàn chơi để hiển thị trạng thái mới nhất.
                bv.refreshBoard();
                updateBombUI();

                if (!didHint && b.getGameState() == GameState.RUNNING) {
                    JOptionPane.showMessageDialog(bv, "Không có gợi ý phù hợp lúc này.", "Hint",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

	// ─── EXPORT (XUẤT DỮ LIỆU) ───────────────────────────────────────
	private void initExportControl() {
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
	}

	// ─── IMPORT (NHẬP DỮ LIỆU) ───────────────────────────────────────
	private void initImportControl() {
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
	}

	// ─── CHỌN ĐỘ KHÓ (Difficulty) ────────────────────────────────────
	private void initDifficultyButtons() {
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
	}

	// ─── TIMER (ĐỒNG HỒ) ─────────────────────────────────────────────
	private void initTimer() {
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
	}    // ─── 4. ĐẶT CỜ / 8. GỠ CỜ (UC_04_DC / UC_04_GC) ─────────────────
    private void initCellListeners() {
        for (int row = 0; row < bv.getCells().length; row++) {
            for (int col = 0; col < bv.getCells()[0].length; col++) {
                int r = row;
                int c = col;
                bv.getCells()[row][col].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        // 8.2.0 Tại bước 8.1.1, nếu người chơi nhấp gỡ cờ nhưng ván chơi đã kết thúc (thắng hoặc thua).
                        if (b.getGameState() != GameState.RUNNING) {
                            // 8.2.1 Hệ thống chặn sự kiện chuột phải, giữ nguyên hiện trạng và không thực hiện thay đổi trạng thái của ô.
                            return;
                        }
                        if (e.getButton() == MouseEvent.BUTTON1) { // Left-click
                            boolean firstMove = b.isFirstMove();
                            b.reveal(r, c);
                            if (firstMove) {
                                timer.start();
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) { // Right-click
                            // 4.0.1 Người chơi thực hiện thao tác nhấp chuột phải vào một ô trên giao diện bàn chơi.
                            // 8.1.1 Người chơi thực hiện thao tác nhấp chuột phải vào một ô đang có cờ trên giao diện bàn chơi.
                            // 4.0.2 Hệ thống (BoardView) tiếp nhận sự kiện chuột và gửi tín hiệu xử lý nút bấm đến bộ điều khiển (BoardController).
                            // 8.1.2 Hệ thống (BoardView) tiếp nhận sự kiện chuột và gửi tín hiệu xử lý nút bấm đến bộ điều khiển (BoardController).
                            // 4.0.3 Bộ điều khiển gọi hàm bắt đầu xử lý sự kiện trên đối tượng bàn chơi (Board).
                            // 8.1.3 Bộ điều khiển gọi hàm bắt đầu xử lý sự kiện trên đối tượng bàn chơi (Board).
                            b.toggleFlag(r, c);
                        }

                        // 4.0.10 Bộ điều khiển yêu cầu cập nhật lại giao diện (hiển thị hình lá cờ tại ô vừa nhấp và cập nhật bộ đếm cờ).
                        // 8.1.9 Bộ điều khiển yêu cầu BoardView cập nhật lại giao diện hiển thị (xóa hình ảnh lá cờ trên ô và tăng số trên bộ đếm).
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
