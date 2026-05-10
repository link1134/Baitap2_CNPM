package view;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JLabel;

public class LabelNumber extends JLabel {
    private BoardView bv;
    private String number;

    public LabelNumber(BoardView bv, String number) {

        this.bv = bv;
        this.number = number;
        this.setPreferredSize(new Dimension(78, 46));
    }

    @Override
    protected void paintComponent(Graphics g) {
        // TODO Auto-generated method stub
        super.paintComponent(g);
        g.drawImage(bv.getData().getListImage().get(String.valueOf(number.charAt(0))), 0, 0, 26, 46, null);
        g.drawImage(bv.getData().getListImage().get(String.valueOf(number.charAt(1))), 26, 0, 26, 46, null);
        g.drawImage(bv.getData().getListImage().get(String.valueOf(number.charAt(2))), 52, 0, 26, 46, null);
    }

    public void setNumber(String number) {
        // TODO Auto-generated method stub
        this.number = number;
        repaint();
    }
}
