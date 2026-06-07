package Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import controller.StartMenuController;
import model.Difficulty;
import model.GameConfig;
import view.StartMenu;

/**
 * Test class cho Use Case: - UC_02: Chọn độ khó
 *
 * Các phương thức liên quan: - Difficulty.getConfig() -
 * StartMenuController.getSelectedConfig() - GameConfig(...)
 *
 * Lưu ý: Use case này kết thúc khi hệ thống tạo được GameConfig hợp lệ. Vì vậy
 * các test chỉ tập trung vào việc tạo và kiểm tra GameConfig.
 */
public class ChonDoKhoTest {

	// =========================================================
	// UC_02 : CHỌN ĐỘ KHÓ
	// =========================================================

	/**
	 * Test 1: Chọn độ khó EASY
	 *
	 * Expected: - Tạo GameConfig thành công - Kích thước 9x9 - Có 10 quả mìn
	 */
	@Test
	public void testChonDoKho_Easy_ThanhCong() {

		// Arrange + Act
		GameConfig config = Difficulty.EASY.getConfig();

		// Assert
		assertEquals(9, config.getRows());
		assertEquals(9, config.getCols());
		assertEquals(10, config.getMineCount());
	}

	/**
	 * Test 2: Chọn độ khó MEDIUM
	 *
	 * Expected: - Tạo GameConfig 16x16 - Có 40 quả mìn
	 */
	@Test
	public void testChonDoKho_Medium_ThanhCong() {

		// Arrange + Act
		GameConfig config = Difficulty.MEDIUM.getConfig();

		// Assert
		assertEquals(16, config.getRows());
		assertEquals(16, config.getCols());
		assertEquals(40, config.getMineCount());
	}

	/**
	 * Test 3: Chọn độ khó HARD
	 *
	 * Expected: - Tạo GameConfig 16x30 - Có 99 quả mìn
	 */
	@Test
	public void testChonDoKho_Hard_ThanhCong() {

		// Arrange + Act
		GameConfig config = Difficulty.HARD.getConfig();

		// Assert
		assertEquals(16, config.getRows());
		assertEquals(30, config.getCols());
		assertEquals(99, config.getMineCount());
	}

	/**
	 * Test 4: Chọn chế độ CUSTOM hợp lệ
	 *
	 * Input: - rows = 20 - cols = 15 - mines = 50
	 *
	 * Expected: - GameConfig được tạo thành công
	 */
	@Test
	public void testChonDoKho_CustomHopLe_ThanhCong() {

		// Arrange + Act
		GameConfig config = new GameConfig(20, 15, 50);

		// Assert
		assertEquals(20, config.getRows());
		assertEquals(15, config.getCols());
		assertEquals(50, config.getMineCount());
	}

	/**
	 * Test 5: rows <= 0
	 *
	 * Expected: - IllegalArgumentException
	 */
	@Test
	public void testChonDoKho_RowLessThanOrEqualZero_NgoaiLe() {

		// Arrange
		StartMenu menu = new StartMenu();
		StartMenuController controller = new StartMenuController(menu);

		menu.getDifficultyBox().setSelectedItem(Difficulty.CUSTOM);
		menu.getRowField().setText("0");
		menu.getColField().setText("9");
		menu.getMineField().setText("10");

		// Act + Assert
		assertThrows(IllegalArgumentException.class, () -> controller.getSelectedConfig());
	}

	/**
	 * Test 6: rows > 50
	 *
	 * Expected: - IllegalArgumentException
	 */
	@Test
	public void testChonDoKho_RowGreaterThan50_NgoaiLe() {

		StartMenu menu = new StartMenu();
		StartMenuController controller = new StartMenuController(menu);

		menu.getDifficultyBox().setSelectedItem(Difficulty.CUSTOM);
		menu.getRowField().setText("51");
		menu.getColField().setText("9");
		menu.getMineField().setText("10");

		assertThrows(IllegalArgumentException.class, () -> controller.getSelectedConfig());
	}

	/**
	 * Test 7: cols <= 0
	 *
	 * Expected: - IllegalArgumentException
	 */
	@Test
	public void testChonDoKho_ColLessThanOrEqualZero_NgoaiLe() {

		StartMenu menu = new StartMenu();
		StartMenuController controller = new StartMenuController(menu);

		menu.getDifficultyBox().setSelectedItem(Difficulty.CUSTOM);
		menu.getRowField().setText("9");
		menu.getColField().setText("0");
		menu.getMineField().setText("10");

		assertThrows(IllegalArgumentException.class, () -> controller.getSelectedConfig());
	}

	/**
	 * Test 8: cols > 50
	 *
	 * Expected: - IllegalArgumentException
	 */
	@Test
	public void testChonDoKho_ColGreaterThan50_NgoaiLe() {

		StartMenu menu = new StartMenu();
		StartMenuController controller = new StartMenuController(menu);

		menu.getDifficultyBox().setSelectedItem(Difficulty.CUSTOM);
		menu.getRowField().setText("9");
		menu.getColField().setText("51");
		menu.getMineField().setText("10");

		assertThrows(IllegalArgumentException.class, () -> controller.getSelectedConfig());
	}

	/**
	 * Test 9: mines <= 0
	 *
	 * Expected: - IllegalArgumentException
	 */
	@Test
	public void testChonDoKho_MineLessThanOrEqualZero_NgoaiLe() {

		StartMenu menu = new StartMenu();
		StartMenuController controller = new StartMenuController(menu);

		menu.getDifficultyBox().setSelectedItem(Difficulty.CUSTOM);
		menu.getRowField().setText("9");
		menu.getColField().setText("9");
		menu.getMineField().setText("0");

		assertThrows(IllegalArgumentException.class, () -> controller.getSelectedConfig());
	}

	/**
	 * Test 10: số mìn vượt quá 80% số ô
	 *
	 * Expected: - IllegalArgumentException
	 */
	@Test
	public void testChonDoKho_MineGreaterThan80Percent_NgoaiLe() {

		StartMenu menu = new StartMenu();
		StartMenuController controller = new StartMenuController(menu);

		menu.getDifficultyBox().setSelectedItem(Difficulty.CUSTOM);
		menu.getRowField().setText("10");
		menu.getColField().setText("10");
		menu.getMineField().setText("81");

		assertThrows(IllegalArgumentException.class, () -> controller.getSelectedConfig());
	}

	/**
	 * Test 11: nhập dữ liệu không phải số
	 *
	 * Expected: - NumberFormatException
	 */
	@Test
	public void testChonDoKho_NhapKhongPhaiSo_NgoaiLe() {

		StartMenu menu = new StartMenu();
		StartMenuController controller = new StartMenuController(menu);

		menu.getDifficultyBox().setSelectedItem(Difficulty.CUSTOM);
		menu.getRowField().setText("abc");
		menu.getColField().setText("9");
		menu.getMineField().setText("10");

		assertThrows(NumberFormatException.class, () -> controller.getSelectedConfig());
	}
}