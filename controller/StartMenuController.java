package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.Board;
import model.Difficulty;
import view.BoardView;
import view.StartMenu;

public class StartMenuController implements ActionListener {
    private StartMenu startMenu;
    public StartMenuController(StartMenu startMenu) {
        this.startMenu = startMenu;
        this.startMenu.getStartButton().addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Difficulty difficulty = startMenu.getSelectedDifficulty();
        Board board = new Board(difficulty);
        new BoardView(board);
        startMenu.dispose();
    }
}