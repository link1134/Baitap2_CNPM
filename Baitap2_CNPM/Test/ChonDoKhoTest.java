package Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import controller.StartMenuController;
import model.Difficulty;
import model.GameConfig;
import view.StartMenu;

public class ChonDoKhoTest {
	// Kiểm tra tạo GameConfig cho độ khó EASY.
	@Test
	public void testEasyConfig() {
		GameConfig config = Difficulty.EASY.getConfig();

		assertEquals(9, config.getRows());
		assertEquals(9, config.getCols());
		assertEquals(10, config.getMineCount());
	}

	// Kiểm tra tạo GameConfig cho độ khó MEDIUM.
	@Test
	public void testMediumConfig() {
		GameConfig config = Difficulty.MEDIUM.getConfig();

		assertEquals(16, config.getRows());
		assertEquals(16, config.getCols());
		assertEquals(40, config.getMineCount());
	}

	// Kiểm tra tạo GameConfig cho độ khó HARD.
	@Test
	public void testHardConfig() {
		GameConfig config = Difficulty.HARD.getConfig();

		assertEquals(16, config.getRows());
		assertEquals(30, config.getCols());
		assertEquals(99, config.getMineCount());
	}

	// Kiểm tra tạo GameConfig với thông số CUSTOM hợp lệ.
	@Test
	public void testCustomConfig() {
		GameConfig config = new GameConfig(20, 15, 50);

		assertEquals(20, config.getRows());
		assertEquals(15, config.getCols());
		assertEquals(50, config.getMineCount());
	}

	// Kiểm tra ngoại lệ khi số hàng nhỏ hơn hoặc bằng 0.
	@Test
	public void testCustomRowLessThanZero() {
		StartMenu menu = new StartMenu();
		StartMenuController controller = new StartMenuController(menu);

		menu.getDifficultyBox().setSelectedItem(Difficulty.CUSTOM);
		menu.getRowField().setText("0");
		menu.getColField().setText("9");
		menu.getMineField().setText("10");

		assertThrows(IllegalArgumentException.class, () -> controller.getSelectedConfig());
	}

	// rows > 50
	@Test
	public void testRowGreaterThan50() {
		StartMenu menu = new StartMenu();
		StartMenuController controller = new StartMenuController(menu);

		menu.getDifficultyBox().setSelectedItem(Difficulty.CUSTOM);
		menu.getRowField().setText("51");
		menu.getColField().setText("9");
		menu.getMineField().setText("10");

		assertThrows(IllegalArgumentException.class, () -> controller.getSelectedConfig());
	}

	// cols <= 0
	@Test
	public void testColLessThanOrEqualZero() {
		StartMenu menu = new StartMenu();
		StartMenuController controller = new StartMenuController(menu);

		menu.getDifficultyBox().setSelectedItem(Difficulty.CUSTOM);
		menu.getRowField().setText("9");
		menu.getColField().setText("0");
		menu.getMineField().setText("10");

		assertThrows(IllegalArgumentException.class, () -> controller.getSelectedConfig());
	}

	// cols > 50
	@Test
	public void testColGreaterThan50() {
		StartMenu menu = new StartMenu();
		StartMenuController controller = new StartMenuController(menu);

		menu.getDifficultyBox().setSelectedItem(Difficulty.CUSTOM);
		menu.getRowField().setText("9");
		menu.getColField().setText("51");
		menu.getMineField().setText("10");

		assertThrows(IllegalArgumentException.class, () -> controller.getSelectedConfig());
	}

	// mines <= 0
	@Test
	public void testMineLessThanOrEqualZero() {
		StartMenu menu = new StartMenu();
		StartMenuController controller = new StartMenuController(menu);

		menu.getDifficultyBox().setSelectedItem(Difficulty.CUSTOM);
		menu.getRowField().setText("9");
		menu.getColField().setText("9");
		menu.getMineField().setText("0");

		assertThrows(IllegalArgumentException.class, () -> controller.getSelectedConfig());
	}

	// mines > 80%
	@Test
	public void testMineGreaterThan80Percent() {
		StartMenu menu = new StartMenu();
		StartMenuController controller = new StartMenuController(menu);

		menu.getDifficultyBox().setSelectedItem(Difficulty.CUSTOM);
		menu.getRowField().setText("10");
		menu.getColField().setText("10");
		menu.getMineField().setText("81");

		assertThrows(IllegalArgumentException.class, () -> controller.getSelectedConfig());
	}

	// nhập không phải số
	@Test
	public void testInvalidNumberFormat() {
		StartMenu menu = new StartMenu();
		StartMenuController controller = new StartMenuController(menu);

		menu.getDifficultyBox().setSelectedItem(Difficulty.CUSTOM);
		menu.getRowField().setText("abc");
		menu.getColField().setText("9");
		menu.getMineField().setText("10");

		assertThrows(NumberFormatException.class, () -> controller.getSelectedConfig());
	}
}
