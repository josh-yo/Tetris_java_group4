package org.oosd.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.oosd.model.*;

public class GameEngine {
    private final Board board;
    private final PieceFactory factory = new PieceFactory();

    private Tetromino current;
    private boolean isGameOver = false;
    private boolean isPaused = false;

    private Timeline timeline;

    public GameEngine(Board board) { this.board = board; }

    // === Lifecycle ===
    public void start(GraphicsContext gc) {
        spawnNew();
        draw(gc);

        timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            if (!isGameOver && !isPaused) {
                moveDown(gc); // automatic falling
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stop() {
        if (timeline != null) timeline.stop();
    }

    public void togglePause() {
        isPaused = !isPaused;
        if (timeline == null) return;
        if (isPaused) timeline.pause(); else timeline.play();
    }

    // === Player Control API (called by InputController) ===
    public void moveLeft(GraphicsContext gc)  { tryMove(-1, 0); draw(gc); }
    public void moveRight(GraphicsContext gc) { tryMove( 1, 0); draw(gc); }
    public void moveDown(GraphicsContext gc)  { if (!tryMove(0, 1)) lockAndProceed(); draw(gc); }
    public void rotate(GraphicsContext gc)    {
        int[][] rotated = current.rotatedShape();
        if (board.isValidPosition(rotated, current.getX(), current.getY())) {
            current.applyRotation(rotated);
        }
        draw(gc);
    }
    public void dropDown(GraphicsContext gc)  {
        while (board.isValidPosition(current.getShape(), current.getX(), current.getY()+1)) {
            current.move(0, 1);
        }
        lockAndProceed();
        draw(gc);
    }

    // === Internal Logic ===
    private void spawnNew() {
        current = factory.createRandom(board.getWidth());
        if (!board.isValidPosition(current.getShape(), current.getX(), current.getY())) {
            isGameOver = true;
        }
    }

    private boolean tryMove(int dx, int dy) {
        int nx = current.getX() + dx;
        int ny = current.getY() + dy;
        if (board.isValidPosition(current.getShape(), nx, ny)) {
            current.move(dx, dy);
            return true;
        }
        return false;
    }

    private void lockAndProceed() {
        // Fix the current shape onto the board
        board.fixShape(current.getShape(), current.getX(), current.getY(), current.getKind().color());
        // Clear completed rows
        board.clearFullRows();
        // Spawn a new block
        spawnNew();
    }

    // === Rendering (simplified: engine draws; can be separated into GameScreen if needed) ===
    public void draw(GraphicsContext gc) {
        int tile = Board.TILE;
        int W = board.getWidth() * tile;
        int H = board.getHeight() * tile;

        // Background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, W, H);

        // Fixed blocks
        Color[][] colors = board.getFieldColor();
        int[][] field = board.getField();
        for (int i = 0; i < board.getHeight(); i++) {
            for (int j = 0; j < board.getWidth(); j++) {
                if (field[i][j] == 1) {
                    gc.setFill(colors[i][j] != null ? colors[i][j] : Color.GRAY);
                    gc.fillRect(j * tile, i * tile, tile - 1, tile - 1);
                }
            }
        }

        // Current falling block
        if (!isGameOver) {
            gc.setFill(current.getKind().color());
            int[][] s = current.getShape();
            for (int i = 0; i < s.length; i++) {
                for (int j = 0; j < s[i].length; j++) {
                    if (s[i][j] == 1) {
                        gc.fillRect((current.getX() + j) * tile, (current.getY() + i) * tile, tile - 1, tile - 1);
                    }
                }
            }
        } else {
            gc.setFill(Color.WHITE);
            gc.fillText("GAME OVER", W / 2.0 - 60, H / 2.0);
        }
    }
}
