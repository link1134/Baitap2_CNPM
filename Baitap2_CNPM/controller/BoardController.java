package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import model.Board;
import model.CustomGameInput;
import model.Difficulty;
import model.GameConfig;
import model.GameState;
import view.BoardView;
import model.GameStatistics;

/**
 * 5/6/2026 - Mai Vũ Thành Hiển: Gắn actionListener cho jmenuitem custom để nó
 * nhảy pop up chọn số bom, số hàng và cột khi chọn.
 **/
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
			// 7.1 và 7.2 Xử lí sự kiện người chơi nhấn nút Pause.
			@Override
			public void actionPerformed(ActionEvent e) {
				// Từ 7.3 đến 7.14: Thực hiện pause hoặc unpause tùy theo điều kiện.

				if (b.getGameState() == GameState.WIN || b.getGameState() == GameState.LOSE) {
					return;
				}
				// 7.3 đến 7.8: Thực hiện Pause game khi trạng thái game đang là running.
				// 7.3: Gọi Board, lấy trạng thái của game để xác định game có đang là running
				// không.
				if (b.getGameState() == GameState.RUNNING) {
					// 7.5: Khi 7.3 trả ra kết quả 7.4 là running, gọi Board đặt trạng thái game
					// sang Pause.
					b.setGameState(GameState.PAUSE);
					// 7.6: Gọi View, đặt tên của Pause item thành Unpause.
					bv.getPauseItem().setText("Unpause");
					// 7.7: Gọi View, chuyển trạng thái của màn che màn hình thành true.
					bv.getOverlay().setVisible(true);
					// 7.8: Dừng bộ đếm thời gian.
					timer.stop();
					if (bv.getHintBtn() != null)
						bv.getHintBtn().setEnabled(false);
					// 7.9 đến 7.14: Thực hiện Unpause game khi trạng thái game đang là pause.
					// 7.9: Gọi Board, lấy trạng thái của game để xác định game có đang là pause
					// không.
				} else if (b.getGameState() == GameState.PAUSE) {
					// 7.11: Khi 7.9 trả ra kết quả 7.10 là pause, gọi Board đặt trạng thái game
					// sang Pause.
					b.setGameState(GameState.RUNNING);
					// 7.12: Gọi View, đặt tên của Pause item thành Pause.
					bv.getPauseItem().setText("Pause");
					// 7.13: Gọi View, chuyển trạng thái của màn che màn hình thành false.
					bv.getOverlay().setVisible(false);
					// 7.14: Nếu bàn chơi chưa được mở ô nào, thì bộ đếm thời gian sẽ ko được phép
					// mở lại, ngược lại thì được.
					if (!b.isFirstMove()) {
						timer.start();
					}
					if (bv.getHintBtn() != null)
						bv.getHintBtn().setEnabled(true);
				}
			}
		});
		// Statistics
		if (bv.getStatisticsItem() != null) {
			bv.getStatisticsItem().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (b.getStatistics() != null) {
						b.getStatistics().showStatistics();
					}
				}
			});
		bv.getSmileBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				restartGame();
			}
		});

		// ─── 9. GỢI Ý (UC_09_GH) ─────────────────────────────────────────────
		bv.getHintBtn().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 9.0.1 Người chơi thực hiện thao tác nhấp vào nút "Gợi ý" (Hint) trên giao
				// diện bàn chơi.
				// 9.0.2 Hệ thống (BoardView) tiếp nhận sự kiện kích hoạt và gửi yêu cầu lấy gợi
				// ý đến bộ điều khiển (BoardController).
				// 9.2.0 Tại bước 9.0.3, nếu ván chơi không ở trạng thái RUNNING hoặc bàn cờ
				// không còn ô an toàn nào khác để gợi ý.
				if (b.getGameState() != GameState.RUNNING) {
					// 9.2.1 Hệ thống chặn yêu cầu gợi ý, không thực hiện hành động và bỏ qua sự
					// kiện.
					// 9.2.2 Use case kết thúc.
					return;
				}
				// 9.0.3 Bộ điều khiển gọi hàm xử lý tính toán gợi ý trên đối tượng bàn chơi
				// (Board).
				boolean didHint = b.giveHint();
				// 9.0.6 Bộ điều khiển tiếp nhận tọa độ ô gợi ý từ hệ thống (thông qua kết quả
				// trả về).

				// 9.0.9 Hệ thống hoàn tất quá trình xử lý và cập nhật lại toàn bộ giao diện bàn
				// chơi để hiển thị trạng thái mới nhất.
				bv.refreshBoard();
				updateBombUI();

				if (!didHint && b.getGameState() == GameState.RUNNING) {
					JOptionPane.showMessageDialog(bv, "Không có gợi ý phù hợp lúc này.", "Hint",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		// 4.0.2. BoardController nhận sự kiện từ menu Export
		bv.getExportItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 4.0.3. getGameState() Lấy trạng thái game hiện tại
				GameState oldState = b.getGameState();
				// 4.0.4. setGameState(PAUSE) Nếu game đang RUNNING thì tạm chuyển sang PAUSE
				if (oldState == GameState.RUNNING) {
					b.setGameState(GameState.PAUSE);
				}
				// 4.0.5. Gọi hàm exportData() trên đối tượng Board
				String data = b.exportData();
				// 4.0.6. Khôi phục lại trạng thái game cũ
				b.setGameState(oldState);
				// 4.0.7. Tạo JTextArea chứa chuỗi save
				JTextArea area = new JTextArea(data);
				area.setLineWrap(true);
				area.setWrapStyleWord(true);
				area.setEditable(false);
				// 4.0.8. Hiển thị chuỗi save cho người chơi
				JOptionPane.showMessageDialog(bv, new JScrollPane(area), "Export Data",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		// 5.0.2. Hệ thống (BoardView) tiếp nhận sự kiện và chuyển cho BoardController
		bv.getImportItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 5.0.3. Hệ thống hiển thị dialog nhập chuỗi save
				String data = JOptionPane.showInputDialog(bv, "Nhập chuỗi save:", "Import Game",
						JOptionPane.PLAIN_MESSAGE);
				// 5.2.1 Người chơi nhấn Cancel hoặc nhập chuỗi trống
				if (data == null || data.trim().isBlank()) {
					// 5.2.2. Kết thúc use case, không thực hiện import
					return;
				}
				// 5.0.5 BoardController gọi Board.importData(String)
				Board importedBoard = Board.importData(data.trim());
				// 5.0.7. Kiểm tra dữ liệu hợp lệ
				// 5.1.1. Dữ liệu save không hợp lệ
				if (importedBoard == null) {
					// 5.1.2. Hiển thị thông báo lỗi
					JOptionPane.showMessageDialog(bv, "Dữ liệu save không hợp lệ!", "Import thất bại",
							JOptionPane.ERROR_MESSAGE);
					// 5.1.3. Use case kết thúc
					return;
				}
				// 5.0.8. Dừng timer hiện tại
				if (timer != null) {
					timer.stop();
				}
				// 5.0.9. Đóng BoardView cũ
				bv.dispose();
				// 5.0.10. Tạo BoardView mới từ Board đã import
				BoardView newView = new BoardView(importedBoard);
				// 5.0.11. Tạo BoardController mới
				BoardController controller = new BoardController(newView, importedBoard);
				// 5.0.12. Xử lý giao diện theo trạng thái game được lưu
				if (importedBoard.getGameState() == GameState.PAUSE) {
					newView.getPauseItem().setText("Unpause");
					newView.getOverlay().setVisible(true);
					if (newView.getHintBtn() != null)
						newView.getHintBtn().setEnabled(false);
				} else {
					if (newView.getHintBtn() != null)
						newView.getHintBtn().setEnabled(true);
				}
				// 5.0.13. Cập nhật UI (refresh board, thời gian, số mìn)
				controller.updateView();
				String text = String.format("%03d", importedBoard.getElapsedTime());
				newView.getLbtime().setNumber(text);
				int remain = importedBoard.getRemainingMines();
				String mineText = String.format("%03d", remain);
				newView.getLbbomb().setNumber(mineText);
			}
		});
		// Chọn độ khó khi đang chơi.
		// [2.1.1]/[2.1.2]Người chơi chọn mức độ khó từ giao diện và xác nhận chọn độ
		// khó
		// [2.1.3]: Hệ thống tiếp nhận lựa chọn
		// [2.1.4]: Hệ thống khởi tạo cấu hình tương ứng với độ khó người chơi chọn.
		bv.getEasy().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				GameConfig easyMode = Difficulty.EASY.getConfig();
				startNewGame(easyMode);
			}
		});

		bv.getMedium().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				GameConfig mediumMode = Difficulty.MEDIUM.getConfig();
				startNewGame(mediumMode);
			}
		});

		bv.getHard().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				GameConfig hardMode = Difficulty.HARD.getConfig();
				startNewGame(hardMode);
			}
		});
		bv.getCustom().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				startCustomGame();
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
					// 8.2.0 Tại bước 8.1.1, nếu người chơi nhấp gỡ cờ nhưng ván chơi đã kết thúc
					// (thắng hoặc thua).
					public void mousePressed(MouseEvent e) {
						if (b.getGameState() != GameState.RUNNING) {
							// 8.2.1 Hệ thống chặn sự kiện chuột phải, giữ nguyên hiện trạng và không thực
							// hiện thay đổi trạng thái của ô.
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
							// 4.0.1 Người chơi thực hiện thao tác nhấp chuột phải vào một ô trên giao diện
							// bàn chơi.
							// 8.1.1 Người chơi thực hiện thao tác nhấp chuột phải vào một ô đang có cờ trên
							// giao diện bàn chơi.
							// 4.0.2 Hệ thống (BoardView) tiếp nhận sự kiện chuột và gửi tín hiệu xử lý nút
							// bấm đến bộ điều khiển (BoardController).
							// 8.1.2 Hệ thống (BoardView) tiếp nhận sự kiện chuột và gửi tín hiệu xử lý nút
							// bấm đến bộ điều khiển (BoardController).
							// 4.0.3 Bộ điều khiển gọi hàm bắt đầu xử lý sự kiện trên đối tượng bàn chơi
							// (Board).
							// 8.1.3 Bộ điều khiển gọi hàm bắt đầu xử lý sự kiện trên đối tượng bàn chơi
							// (Board).
							b.toggleFlag(r, c);
						}
						// 3.11: Phương thức gọi view cập nhật giao diện
						// 4.0.10 Bộ điều khiển yêu cầu cập nhật lại giao diện (hiển thị hình lá cờ tại
						// ô vừa nhấp và cập nhật bộ đếm cờ).
						// 8.1.9 Bộ điều khiển yêu cầu BoardView cập nhật lại giao diện hiển thị (xóa
						// hình ảnh lá cờ trên ô và tăng số trên bộ đếm).
						bv.refreshBoard();
						// 3.12: Cập nhật số mìn còn lại.
						updateBombUI();
					}
				});
			}
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

		Board newBoard = new Board(b.getConfig());
		BoardView newView = new BoardView(newBoard);
		new BoardController(newView, newBoard);
	}

	private void startNewGame(GameConfig config) {

		if (timer != null) {
			timer.stop();
		}
		bv.dispose();
		Board newBoard = new Board(config);
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
	public GameStatistics getStatistics() {
		return statistics;
	}
	
	private void startCustomGame() {
		try {
			// [2.2.1]: Hệ thống hiển thị nơi cho người chơi nhập các thông số tùy chỉnh gồm
			// số hàng, số cột và số mìn.
			// [2.2.2]: Người chơi nhập vào các thông số mong muốn
			// [2.2.3]: Người chơi xác nhận tiếp tục.
			CustomGameInput input = bv.showCustomDialog();
			if (input == null) {
				return;
			}
			// [2.2.4]: Hệ thống nhận và kiểm tra các thông số người dùng đã nhập.
			validateConfig(input.getRows(), input.getCols(), input.getMines());
			// [2.2.5]: Hệ thống tạo cấu hình màn chơi từ các thông số nhận từ người dùng.
			GameConfig config = new GameConfig(input.getRows(), input.getCols(), input.getMines());
			startNewGame(config);
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(bv, "Vui lòng nhập số nguyên hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(bv, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void validateConfig(int rows, int cols, int mines) {
		// [2.4.1]: Hệ thống hiển thị thông báo “Số hàng phải lớn hơn 0”
		if (rows <= 0)
			throw new IllegalArgumentException("Số hàng phải lớn hơn 0!");
		// [2.5.1]: Hệ thống hiển thị thông báo “Số hàng tối đa là 50”
		else if (rows > 50)
			throw new IllegalArgumentException("Số hàng tối đa là 50!");
		// [2.6.1]: Hệ thống hiển thị thông báo “Số cột phải lớn hơn 0”
		if (cols <= 0)
			throw new IllegalArgumentException("Số cột phải lớn hơn 0!");
		// [2.8.1]: Hệ thống hiển thị thông báo “Số hàng tối đa là 50”
		else if (cols > 50)
			throw new IllegalArgumentException("Số cột tối đa là 50!");
		// [2.6.1]: Hệ thống hiển thị thông báo “Số cột phải lớn hơn 0”
		if (mines <= 0)
			throw new IllegalArgumentException("Số mìn phải lớn hơn 0!");
		// [2.7.1]: Hệ thống hiển thị thông báo “Số mìn không được vượt quá 80% số ô”
		else if (mines > rows * cols * 0.8)
			throw new IllegalArgumentException("Số mìn không được vượt quá 80% số ô!");
	}

}
