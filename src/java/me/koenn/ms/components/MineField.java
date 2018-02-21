package me.koenn.ms.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MineField {

    private final List<Cell> field;
    private final int mines;
    private final int size;

    public MineField(int mines, int size) {
        this.field = new ArrayList<>();
        this.mines = mines;
        this.size = size;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                this.field.add(new Cell(new Vector(x, y)));
            }
        }

        Random random = ThreadLocalRandom.current();
        int placed = 0;
        do {
            Cell cell = this.field.get(random.nextInt(this.field.size()));
            if (cell.value == 0) {
                cell.value = 9;
                placed++;
            }
        } while (placed < mines);

        this.calculateNumbers();
    }

    private void calculateNumbers() {
        for (int x = 0; x < this.size; x++) {
            for (int y = 0; y < this.size; y++) {
                Cell cell = this.getCell(new Vector(x, y));
                if (!cell.isMine()) {
                    continue;
                }

                for (int dx = x - 1; dx <= x + 1; dx++) {
                    if (dx < 0 || dx >= this.size) {
                        continue;
                    }

                    for (int dy = y - 1; dy <= y + 1; dy++) {
                        if (dy < 0 || dy >= this.size) {
                            continue;
                        }

                        Cell calc = this.getCell(new Vector(dx, dy));
                        if (calc.isMine()) {
                            continue;
                        }

                        calc.value++;
                    }
                }
            }
        }
    }

    public List<Vector> getNeighbours(Vector position) {
        List<Vector> neighbours = new ArrayList<>();
        int x = position.getX();
        int y = position.getY();

        for (int dx = x - 1; dx <= x + 1; dx++) {
            if (dx < 0 || dx >= this.size) {
                continue;
            }

            for (int dy = y - 1; dy <= y + 1; dy++) {
                if (dy < 0 || dy >= this.size) {
                    continue;
                }

                neighbours.add(new Vector(dx, dy));
            }
        }

        return neighbours;
    }

    public Cell getCell(Vector position) {
        return this.field.stream()
                .filter(cell -> cell.getPosition().equals(position))
                .findFirst().orElse(null);
    }

    public List<Cell> getField() {
        return field;
    }

    public int getSize() {
        return this.size;
    }

    public int getMines() {
        return mines;
    }
}
