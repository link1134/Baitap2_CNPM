package model;

import java.io.Serializable;

public class Cell implements Serializable {
    private boolean mine;
    private boolean revealed;
    private boolean flagged;
    private boolean exploded;
    private int nearbyMines;

    //	public enum Type {
//		EMPTY, MINE, NUMBER
//	}
//
//	private Type type;
//	private int x;
//	private int y;
//	private int number;
//	public boolean revealed;
//	public boolean flagged;
//	public boolean exploded;
    public Cell() {

    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public boolean isExploded() {
        return exploded;
    }

    public void setExploded(boolean exploded) {
        this.exploded = exploded;
    }

    public int getNearbyMines() {
        return nearbyMines;
    }

    public void setNearbyMines(int nearbyMines) {
        this.nearbyMines = nearbyMines;
    }
}
