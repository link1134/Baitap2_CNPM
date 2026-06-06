package controller;

import model.Board;
import model.Difficulty;
import view.BoardView;
import view.StartMenu;

/**
 * StartMenuController xử lý sự kiện ở màn hình StartMenu.
 * Khi người chơi nhấn Start Game, hệ thống lấy độ khó và tạo bàn chơi mới.
 */
public class StartMenuController {

    private StartMenu startMenu;

    public StartMenuController(StartMenu startMenu) {
        this.startMenu = startMenu;

        // Gắn sự kiện cho nút Start Game
        this.startMenu.getStartButton().addActionListener(e -> startGame());
    }

    private void startGame() {
        // Lấy độ khó người chơi chọn
        Difficulty difficulty = startMenu.getSelectedDifficulty();

        // Tạo board mới theo độ khó
        Board board = new Board(difficulty);

        // Tạo giao diện bàn chơi
        BoardView boardView = new BoardView(board);

        // Gắn controller cho bàn chơi
        new BoardController(boardView, board);

        // Đóng màn hình StartMenu
        startMenu.dispose();
    }
}
