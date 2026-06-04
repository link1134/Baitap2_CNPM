package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import model.Board;
import model.Difficulty;
import model.GameConfig;
import view.BoardView;
import view.StartMenu;
/** 5/6/2026 - Mai Vũ Thành Hiển: Gắn actionListioner cho difficultyBox, để bật mở cái panel của custom nếu chọn.
 *  **/
public class StartMenuController {
	private StartMenu startMenu;

	public StartMenuController(StartMenu startMenu) {
		this.startMenu = startMenu;
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		startMenu.getStartButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					GameConfig config = getSelectedConfig();

					Board board = new Board(config);
					BoardView bv = new BoardView(board);

					new BoardController(bv, board);
					startMenu.dispose();

				} catch (NumberFormatException ex) {

					JOptionPane.showMessageDialog(startMenu, "Vui lòng nhập số nguyên hợp lệ!", "Lỗi",
							JOptionPane.ERROR_MESSAGE);

				} catch (Exception ex) {

					JOptionPane.showMessageDialog(startMenu, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		startMenu.getDifficultyBox().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// TODO Auto-generated method stub
					Difficulty selected = (Difficulty) startMenu.getDifficultyBox().getSelectedItem();
					startMenu.getCustomPanel().setVisible(selected == Difficulty.CUSTOM);
					startMenu.revalidate();
					startMenu.repaint();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(startMenu, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	public GameConfig getSelectedConfig() {

		Difficulty difficulty = (Difficulty) startMenu.getDifficultyBox().getSelectedItem();

		if (difficulty != Difficulty.CUSTOM) {
			return difficulty.getConfig();
		}

		int rows = Integer.parseInt(startMenu.getRowField().getText());
		if (rows <= 0) {
			throw new IllegalArgumentException("Số hàng phải lớn hơn 0!");
		} else if (rows > 50) {
			throw new IllegalArgumentException("Số hàng tối đa là 50!");
		}
		int cols = Integer.parseInt(startMenu.getColField().getText());
		if (cols <= 0) {
			throw new IllegalArgumentException("Số cột phải lớn hơn 0!");
		} else if (cols > 50) {
			throw new IllegalArgumentException("Số cột tối đa là 50!");
		}
		int mines = Integer.parseInt(startMenu.getMineField().getText());
		if (mines <= 0) {
			throw new IllegalArgumentException("Số mìn phải lớn hơn 0!");
		}

		if (mines >= rows * cols) {
			throw new IllegalArgumentException("Số mìn không được lớn hơn hoặc bằng số hàng × số cột!");
		} else if (mines > rows * cols * 0.8) {
			throw new IllegalArgumentException("Số mìn không được vượt quá 80% số ô!");
		}
		return new GameConfig(rows, cols, mines);
	}
}