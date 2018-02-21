package me.koenn.ms.components;

public class Cell {

    private final Vector position;
    public int value;
    private boolean revealed;
    private boolean flagged;

    public Cell(Vector position) {
        this.position = position;
    }

    public Vector getPosition() {
        return this.position;
    }

    public boolean isMine() {
        return this.value == 9;
    }

    public boolean isRevealed() {
        return this.revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public boolean isFlagged() {
        return this.flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }
}
