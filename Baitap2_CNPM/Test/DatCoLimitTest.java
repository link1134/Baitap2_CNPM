package Test;

import model.Board;
import model.Cell;
import model.Difficulty;

public class DatCoLimitTest {

    public static void main(String[] args) {
        Board board = new Board(Difficulty.EASY.getConfig());
        // Giả lập nước đi đầu để mìn được đặt
        board.reveal(0, 0);

        int mineCount = board.getMineCount();
        System.out.println("Số mìn: " + mineCount);

        // Đặt cờ cho đến khi đầy
        int flagsPlaced = 0;
        for (int r = 0; r < board.getRows() && flagsPlaced < mineCount; r++) {
            for (int c = 0; c < board.getCols() && flagsPlaced < mineCount; c++) {
                Cell cell = board.getGrid()[r][c];
                if (!cell.isRevealed() && !cell.isFlagged()) {
                    board.handlePlaceFlag(r, c);
                    flagsPlaced++;
                }
            }
        }
        System.out.println("Cờ đã đặt: " + flagsPlaced + " / flagCount=" + board.getFlagCount());

        // Thử đặt thêm 1 cờ nữa — phải bị chặn
        int before = board.getFlagCount();
        // Tìm ô chưa đặt cờ
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Cell cell = board.getGrid()[r][c];
                if (!cell.isRevealed() && !cell.isFlagged()) {
                    board.handlePlaceFlag(r, c);
                    System.out.println("Đặt thêm 1 cờ nữa → flagCount=" + board.getFlagCount());
                    if (board.getFlagCount() == before) {
                        System.out.println("=> ĐÃ BỊ CHẶN (đúng)");
                    } else {
                        System.out.println("=> KHÔNG BỊ CHẶN (sai)");
                    }
                    break;
                }
            }
            break;
        }

        System.out.println("Test hoàn tất.");
    }
}