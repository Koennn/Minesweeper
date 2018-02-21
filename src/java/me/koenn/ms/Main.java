package me.koenn.ms;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import me.koenn.ms.components.Cell;
import me.koenn.ms.components.MineField;
import me.koenn.ms.components.Vector;

public class Main extends Application {

    private static final ClassLoader CLASS_LOADER = Main.class.getClassLoader();
    private static final Image T_NORMAL = new Image(CLASS_LOADER.getResourceAsStream("cell.png"));
    private static final Image T_HOVER = new Image(CLASS_LOADER.getResourceAsStream("cell_hover.png"));
    private static final Image T_EMPTY = new Image(CLASS_LOADER.getResourceAsStream("cell_empty.png"));
    private static final Image T_EXPLODED = new Image(CLASS_LOADER.getResourceAsStream("exploded_mine.png"));
    private static final Image T_FLAGGED = new Image(CLASS_LOADER.getResourceAsStream("flagged.png"));
    private static final Image T_MINE = new Image(CLASS_LOADER.getResourceAsStream("mine.png"));
    private static final Image T_WRONG = new Image(CLASS_LOADER.getResourceAsStream("wrong.png"));
    private static final Font FONT = Font.font("Arial", FontWeight.EXTRA_BOLD, 20.0);

    private static final int mineCount = 10;
    private static final int size = 10;

    private static Color[] COLORS;

    private MineSweeper mineSweeper;

    private GraphicsContext graphics;
    private Label flags;
    private Vector highlighted;

    public static void main(String[] args) {
        launch(args);
    }

    private static Color getColor(int value) {
        float red = Math.min(value * 1.5F, 4.0F) / 4.0F;
        float green = Math.max(1.0F - red, 0.0F);
        return new Color(red, green, 0.0F, 1.0);
    }

    @Override
    public void start(Stage primaryStage) {
        this.mineSweeper = new MineSweeper(mineCount, size);

        //Bereken de kleuren voor de nummers (0-9) van te voren.
        COLORS = new Color[10];
        for (int i = 0; i < COLORS.length; i++) {
            COLORS[i] = getColor(i);
        }

        VBox root = new VBox();
        Scene scene = new Scene(root, size * 50, (size * 50) + 50);
        primaryStage.setTitle("Minesweeper");
        primaryStage.setScene(scene);

        HBox buttons = new HBox();
        buttons.setPadding(new Insets(5, 5, 5, 5));
        buttons.setSpacing(10);

        this.flags = new Label("Flags: " + this.mineSweeper.flagCount);
        this.flags.setFont(FONT);
        this.flags.setPadding(new Insets(8, 0, 8, 0));

        Button restart = new Button("Restart");
        restart.setFont(FONT);
        restart.setPrefHeight(40);
        restart.setPrefWidth(100);
        restart.setOnMouseClicked(event -> this.mineSweeper.restart());

        buttons.getChildren().addAll(flags, restart);

        Canvas canvas = new Canvas(size * 50, size * 50);
        this.graphics = canvas.getGraphicsContext2D();
        this.graphics.setFont(FONT);

        root.getChildren().addAll(buttons, canvas);

        scene.setOnMouseMoved(this::onMouseMove);
        scene.setOnMouseClicked(this::onMouseClick);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                render();
            }
        }.start();

        primaryStage.show();
    }

    /**
     * Wordt elke frame aangeroepen. Gebruik alleen voor renderen.
     */
    public void render() {
        this.flags.setText("Flags: " + this.mineSweeper.flagCount);

        MineField field = this.mineSweeper.getField();
        for (int x = 0; x < field.getSize(); x++) {
            for (int y = 0; y < field.getSize(); y++) {
                Cell cell = field.getCell(new Vector(x, y));

                //Bereken de coordinated van de cell in de scene.
                int tx = x * 50, ty = y * 50;

                //Teken eerst de cell met het nummer in de juiste kleur.
                this.graphics.drawImage(T_EMPTY, tx, ty);
                this.graphics.setFill(COLORS[cell.value]);
                this.graphics.fillText(String.valueOf(cell.value), tx + 20, ty + 30);

                if (cell.isMine()) {
                    //Als de cell een mine is, teken de rode mine texture er overheen.
                    this.graphics.drawImage(T_EXPLODED, tx, ty);
                }

                if (cell.isFlagged()) {
                    //Als de cell geflagged is, teken de verkeerde flag texture er overheen.
                    this.graphics.drawImage(T_WRONG, tx, ty);

                    if (cell.isMine()) {
                        //Als de cell een mine is, teken de groene mine texture er overheen.
                        this.graphics.drawImage(T_MINE, tx, ty);
                    }
                }

                if (!cell.isRevealed()) {
                    //Als de cell niet revealed is, teken de normale cell texture er overheen.
                    this.graphics.drawImage(T_NORMAL, tx, ty);

                    if (this.highlighted != null && this.highlighted.equals(cell.getPosition())) {
                        //Als de cell gehighlight is, teken de highlight texture er overheen.
                        this.graphics.drawImage(T_HOVER, tx, ty);
                    }

                    if (cell.isFlagged()) {
                        //Als de cell geflagged is, teken de flag texture er overheen.
                        this.graphics.drawImage(T_FLAGGED, tx, ty);
                    }
                }
            }
        }
    }

    private Cell getCell(double realX, double realY) {
        //Zit een 50 pixel bar boven het veld, moet er eerst afgehaald worden.
        realY -= 50;

        MineField field = this.mineSweeper.getField();
        for (int x = 0; x < field.getSize(); x++) {
            for (int y = 0; y < field.getSize(); y++) {
                int tx = x * 50, ty = y * 50;

                if (realX >= tx && realX <= tx + 50 && realY >= ty && realY <= ty + 50) {
                    return field.getCell(new Vector(x, y));
                }
            }
        }
        return null;
    }

    public void onMouseMove(MouseEvent event) {
        Cell cell = this.getCell(event.getSceneX(), event.getSceneY());
        if (cell != null && !this.mineSweeper.isFinished()) {
            this.highlighted = cell.getPosition();
        }
    }

    public void onMouseClick(MouseEvent event) {
        Cell cell = this.getCell(event.getSceneX(), event.getSceneY());
        if (cell == null || this.mineSweeper.isFinished() || cell.isRevealed()) {
            return;
        }

        switch (event.getButton()) {
            case PRIMARY:
                if (!cell.isFlagged()) {
                    this.revealCell(cell);
                }
                break;
            case SECONDARY:
                if (!cell.isFlagged() && this.mineSweeper.flagCount > 0) {
                    cell.setFlagged(true);
                    this.mineSweeper.flagCount--;
                    if (cell.isMine()) {
                        this.mineSweeper.marked++;
                        if (this.mineSweeper.marked == this.mineSweeper.getField().getMines()) {
                            this.revealAll();
                        }
                    }
                } else if (cell.isFlagged()) {
                    cell.setFlagged(false);
                    this.mineSweeper.flagCount++;
                    if (cell.isMine()) {
                        this.mineSweeper.marked--;
                    }
                }
                break;
        }
    }

    private void revealAll() {
        this.mineSweeper.getField().getField().forEach(cell -> cell.setRevealed(true));
    }

    private void revealCell(Cell cell) {
        assert cell != null;

        cell.setRevealed(true);

        if (cell.isMine()) {
            this.mineSweeper.finish();
            this.revealAll();
        }

        if (cell.value == 0) {
            MineField field = this.mineSweeper.getField();
            field.getNeighbours(cell.getPosition()).stream()
                    .map(field::getCell)
                    .filter(neighbour -> !neighbour.isRevealed() && !neighbour.isMine() && !neighbour.isFlagged())
                    .forEach(this::revealCell);
        }
    }
}
