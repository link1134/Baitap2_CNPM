package controller;

import model.Board;
import model.Difficulty;
import view.BoardView;

import javax.swing.*;

/**
 * BoardController xử lý các thao tác trên màn hình chơi game.
 * Trong phần này gồm:
 * - Chơi ván mới bằng nút Reset
 * - Chơi ván mới theo độ khó Easy/Medium/Hard
 */
public class BoardController {

    private BoardView boardView;
    private Board board;
    private Timer timer;

    public BoardController(BoardView boardView, Board board) {
        this.boardView = boardView;
        this.board = board;

        initEvents();
    }

    private void initEvents() {
        // Nút mặt cười dùng để reset ván hiện tại
        boardView.getSmileBtn().addActionListener(e -> restartGame());

        // Menu New Game theo từng độ khó
        boardView.getEasyItem().addActionListener(e -> startNewGame(Difficulty.EASY));
        boardView.getMediumItem().addActionListener(e -> startNewGame(Difficulty.MEDIUM));
        boardView.getHardItem().addActionListener(e -> startNewGame(Difficulty.HARD));
    }

    /**
     * Chơi lại ván mới với độ khó hiện tại.
     */
    private void restartGame() {
        if (timer != null) {
            timer.stop();
        }

        // Tạo board mới theo độ khó hiện tại
        Board newBoard = new Board(board.getDifficulty());

        // Tạo giao diện mới
        BoardView newView = new BoardView(newBoard);

        // Gắn controller mới cho ván mới
        new BoardController(newView, newBoard);

        // Đóng giao diện cũ
        boardView.dispose();
    }

    /**
     * Tạo ván mới theo độ khó được chọn từ menu New Game.
     */
    private void startNewGame(Difficulty difficulty) {
        if (timer != null) {
            timer.stop();
        }

        // Tạo board mới theo độ khó được chọn
        Board newBoard = new Board(difficulty);

        // Tạo giao diện mới
        BoardView newView = new BoardView(newBoard);

        // Gắn controller mới
        new BoardController(newView, newBoard);

        // Đóng màn hình cũ
        boardView.dispose();
    }
}
