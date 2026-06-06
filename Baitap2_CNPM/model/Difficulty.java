package model;

/**
 * 5/6/2026 - Mai Vũ Thành Hiển: Thêm thuộc tính config, thêm constructor
 * gameconfig và chỉnh sửa lại cho hoạt động với game config.
 **/
public enum Difficulty {

	EASY(new GameConfig(9, 9, 10)), MEDIUM(new GameConfig(16, 16, 40)), HARD(new GameConfig(16, 30, 99)), CUSTOM(null);

	private final GameConfig config;

	Difficulty(GameConfig config) {
		this.config = config;
	}

	// Phương thức con trong [2.1.4], tạo ra gameConfig cho độ khó custom.
	public GameConfig getConfig() {
		if (config == null) {
			return null;
		}

		return new GameConfig(config.getRows(), config.getCols(), config.getMineCount());
	}

	@Override
	public String toString() {
		return name();
	}
}