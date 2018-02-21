package me.koenn.ms.components;

public class Vector {

    private final int x;
    private final int y;

    public Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Vector && ((Vector) obj).x == this.x && ((Vector) obj).y == this.y;
    }
}
