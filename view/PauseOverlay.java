package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class PauseOverlay extends JPanel {

	public PauseOverlay() {
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {

		Graphics2D g2 = (Graphics2D) g.create();

		g2.setColor(new Color(0, 0, 0, 120));
		g2.fillRect(0, 0, getWidth(), getHeight());

		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 60));

		String text = "PAUSED";

		int textWidth = g2.getFontMetrics().stringWidth(text);

		int x = (getWidth() - textWidth) / 2;
		int y = getHeight() / 2;

		g2.drawString(text, x, y);

		g2.dispose();
	}
}