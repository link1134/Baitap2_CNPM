package controller;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import model.Board;
import model.Cell;
import view.BoardView;

public class BoardController {
	private BoardView bv;
	private Board b;

	public BoardController(BoardView bv, Board b) {
		this.bv = bv;
		this.b = b;
		init();
	}

	private void init() {
		for (int row = 0; row < bv.getCells().length; row++) {
			for (int col = 0; col < bv.getCells()[0].length; col++) {
				int r = row;
				int c = col;
				bv.getCells()[row][col].addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (e.getButton() == MouseEvent.BUTTON1) {
							b.reveal(r, c);
						} 
						updateView();
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
				if (!cell.isRevealed()) {
					if (cell.isFlagged()) {
						btn.setIcon(getScaledIcon("co"));
					} else {
						btn.setIcon(getScaledIcon("noUse"));
					}
				} else {
					if (cell.isMine()) {
							btn.setIcon(getScaledIcon("boom"));
						
					} else {
						String key = "b" + cell.getNearbyMines();
						btn.setIcon(getScaledIcon(key));
					}
				}
				btn.repaint();
			}
		}
	}

	private ImageIcon getScaledIcon(String key) {
		Image img = bv.getData().getListImage().get(key).getScaledInstance(bv.getCellSize(), bv.getCellSize(),
				Image.SCALE_SMOOTH);

		return new ImageIcon(img);
	}
}
