package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import model.Board;
import model.Difficulty;
import model.GameConfig;
import view.BoardView;
import view.StartMenu;

/**
 * 5/6/2026 - Mai Vũ Thành Hiển: Gắn actionListioner cho difficultyBox, để bật
 * mở cái panel của custom nếu chọn.
 **/
public class StartMenuController {
	private StartMenu startMenu;

	public StartMenuController(StartMenu startMenu) {
		this.startMenu = startMenu;
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		// [2.1.2]/[2.2]: Người chơi xác nhận chọn độ khó.

		startMenu.getStartButton().addActionListener(new ActionListener() {
			// [2.1.3]: Hệ thống tiếp nhận lựa chọn
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					// [2.1.4]: Hệ thống khởi tạo cấu hình tương ứng với độ khó người chơi chọn.
					// Tạo ván chơi mới ở start menu.
					// [1.1.1] Hệ thống nhận cấu hình màn chơi từ Use Case UC_02_CD "Chọn độ khó".
					GameConfig config = getSelectedConfig();
					// [1.1.2] Hệ thống tạo bàn chơi mới tương ứng với cấu hình đã nhận.
					Board board = new Board(config);
					// [1.1.3] Hệ thống khởi tạo giao diện bàn chơi mới.
					// [1.1.4] Hệ thống hiển thị bàn chơi mới cho người chơi.
					BoardView bv = new BoardView(board);
					new BoardController(bv, board);
					// startMenu được ngừng hoạt động sau khi bàn chơi được khởi tạo.
					startMenu.dispose();

				} catch (NumberFormatException ex) {

					JOptionPane.showMessageDialog(startMenu, "Vui lòng nhập số nguyên hợp lệ!", "Lỗi",
							JOptionPane.ERROR_MESSAGE);

				} catch (Exception ex) {

					JOptionPane.showMessageDialog(startMenu, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		// [2.1.1]: Người chơi chọn mức độ khó từ giao diện.
		startMenu.getDifficultyBox().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// TODO Auto-generated method stub
					Difficulty selected = (Difficulty) startMenu.getDifficultyBox().getSelectedItem();
					// [2.2.1]: Hệ thống hiển thị nơi cho người chơi nhập các
					// thông số tùy chỉnh gồm số hàng, số cột và số mìn.
					startMenu.getCustomPanel().setVisible(selected == Difficulty.CUSTOM);
					startMenu.revalidate();
					startMenu.repaint();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(startMenu, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	// Chọn độ khó khi đang ở startmenu
	public GameConfig getSelectedConfig() {

		Difficulty difficulty = (Difficulty) startMenu.getDifficultyBox().getSelectedItem();
		if (difficulty != Difficulty.CUSTOM) {
			return difficulty.getConfig();
		}
		// [2.2.4] Hệ thống nhận và bắt đầu kiểm tra các dữ liệu.
		int rows = Integer.parseInt(startMenu.getRowField().getText());
		// [2.4.1]: Hệ thống hiển thị thông báo “Số hàng phải lớn hơn 0”
		if (rows <= 0) {
			throw new IllegalArgumentException("Số hàng phải lớn hơn 0!");
			// [2.5.1]: Hệ thống hiển thị thông báo “Số hàng tối đa là 50”
		} else if (rows > 50) {
			throw new IllegalArgumentException("Số hàng tối đa là 50!");
		}
		int cols = Integer.parseInt(startMenu.getColField().getText());
		// [2.6.1]: Hệ thống hiển thị thông báo “Số cột phải lớn hơn 0”
		if (cols <= 0) {
			throw new IllegalArgumentException("Số cột phải lớn hơn 0!");
			// [2.8.1]: Hệ thống hiển thị thông báo “Số hàng tối đa là 50”
		} else if (cols > 50) {
			throw new IllegalArgumentException("Số cột tối đa là 50!");
		}
		int mines = Integer.parseInt(startMenu.getMineField().getText());
		// [2.6.1]: Hệ thống hiển thị thông báo “Số mìn phải lớn hơn 0”
		if (mines <= 0) {
			throw new IllegalArgumentException("Số mìn phải lớn hơn 0!");
		}
		// [2.7.1]: Hệ thống hiển thị thông báo “Số mìn không được vượt quá 80% số ô”
		if (mines > rows * cols * 0.8) {
			throw new IllegalArgumentException("Số mìn không được vượt quá 80% số ô!");
		}
		// [2.2.5]: Hệ thống tạo cấu hình màn chơi từ các thông số nhận từ người dùng.
		return new GameConfig(rows, cols, mines);
	}
}