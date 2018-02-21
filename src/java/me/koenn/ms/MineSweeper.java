package me.koenn.ms;

import me.koenn.ms.components.MineField;

public class MineSweeper {

    private final int mineCount;
    private final int size;

    private MineField field;
    private boolean finished;

    public int marked;
    public int flagCount;

    public MineSweeper(int mineCount, int size) {
        this.mineCount = mineCount;
        this.size = size;
        this.field = new MineField(mineCount, size);
        this.flagCount = this.field.getMines();
    }

    public void restart() {
        this.field = new MineField(this.mineCount, this.size);
        this.finished = false;
        this.marked = 0;
        this.flagCount = this.field.getMines();
    }

    public MineField getField() {
        return field;
    }

    public boolean isFinished() {
        return finished;
    }

    public void finish() {
        this.finished = true;
    }
}
