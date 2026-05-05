package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.Board;
import model.Difficulty;
import view.BoardView;
import view.StartMenu;

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
				Difficulty difficulty = startMenu.getSelectedDifficulty();
		        Board board = new Board(difficulty);
		        new BoardView(board);
		        BoardView bv = new BoardView(board);

		        new BoardController(bv, board);
		        startMenu.dispose();
			}
		});
		
	}
}