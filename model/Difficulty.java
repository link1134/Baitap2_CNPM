package model;

public enum Difficulty {

	EASY(new GameConfig(9, 9, 10)), MEDIUM(new GameConfig(16, 16, 40)), HARD(new GameConfig(16, 30, 99)), CUSTOM(null);

	private final GameConfig config;

	Difficulty(GameConfig config) {
		this.config = config;
	}

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