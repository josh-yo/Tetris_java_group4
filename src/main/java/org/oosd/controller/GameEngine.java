package org.oosd.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.oosd.model.*;
import org.oosd.service.Effect;

public class GameEngine {
    private final Board board;
    private final PieceFactory factory;

    private Tetromino current;
    private boolean isGameOver = false;
    private boolean isPaused   = false;

    private Timeline timeline;
    private GraphicsContext gcRef;

    private Runnable onGameOver;

    private int fallMs = 500;

    // scoring / lines
    private int score = 0;
    private int linesClearedTotal = 0;

    // lets AI know when a piece has just locked (clears on read)
    private boolean justLocked = false;

    /** Legacy: creates its own PieceFactory (random seed). */
    public GameEngine(Board board) {
        this(board, new PieceFactory());
    }

    /** Preferred: inject a PieceFactory (so we can control seed/sequence). */
    public GameEngine(Board board, PieceFactory factory) {
        this.board   = board;
        this.factory = factory;
    }

    // ----------------------------------------------------
    // Config
    // ----------------------------------------------------
    public void setLevel(int level) {
        // simple linear mapping (1..10) -> (800..120) ms
        fallMs = Math.max(120, 900 - level * 80);
        if (timeline != null) {
            timeline.stop();
            start(gcRef);
        }
    }

    public void setOnGameOver(Runnable r){ this.onGameOver = r; }

    public int getScore()            { return score; }
    public int getLinesCleared()     { return linesClearedTotal; }

    // ----------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------
    public void start(GraphicsContext gc) {
        this.gcRef = gc;
        isGameOver = false;
        justLocked = false;

        spawnNew();
        draw(gc);

        timeline = new Timeline(new KeyFrame(Duration.millis(fallMs), e -> {
            if (!isGameOver && !isPaused) {
                // auto soft drop; AI/human calls softDrop() too
                softDrop(gcRef);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    public void togglePause() {
        isPaused = !isPaused;
        if (timeline != null) {
            if (isPaused) timeline.pause(); else timeline.play();
        }
        if (gcRef != null) draw(gcRef);
    }

    // ----------------------------------------------------
    // Player actions
    // ----------------------------------------------------
    public void moveLeft(GraphicsContext gc)  { tryMove(-1, 0); draw(gc); }
    public void moveRight(GraphicsContext gc) { tryMove( 1, 0); draw(gc); }

    /** Soft drop by user or gravity. Returns true if actually moved down. */
    public boolean softDrop(GraphicsContext gc)  {
        boolean moved = tryMove(0, 1);
        if (!moved) lockAndProceed();
        draw(gc);
        return moved;
    }

    public void rotate(GraphicsContext gc) {
        int[][] rotated = current.rotatedShape();
        if (board.isValidPosition(rotated, current.getX(), current.getY())) {
            current.applyRotation(rotated);
        }
        draw(gc);
    }

    public void dropDown(GraphicsContext gc) {
        while (board.isValidPosition(current.getShape(), current.getX(), current.getY()+1)) {
            current.move(0, 1);
        }
        lockAndProceed();
        draw(gc);
    }

    // ----------------------------------------------------
    // Internals
    // ----------------------------------------------------
    private void spawnNew() {
        current = factory.createRandom(board.getWidth());
        justLocked = false; // new piece spawns -> reset
        if (!board.isValidPosition(current.getShape(), current.getX(), current.getY())) {
            isGameOver = true;
            stop();
            if (gcRef != null) draw(gcRef);
            Effect.GAME_FINISH.play();
            if (onGameOver != null) onGameOver.run();
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
        // mark that the current piece has just locked (AI watches this)
        justLocked = true;

        board.fixShape(current.getShape(), current.getX(), current.getY(), current.getKind().color());
        int cleared = board.clearFullRows();
        if (cleared > 0) {
            linesClearedTotal += cleared;
            switch (cleared) {
                case 1 -> score += 100;
                case 2 -> score += 300;
                case 3 -> score += 600;
                case 4 -> score += 1000;
            }
            Effect.ERASE.play();
        }
        spawnNew();
    }

    // ----------------------------------------------------
    // Rendering
    // ----------------------------------------------------
    public void draw(GraphicsContext gc) {
        int tile = Board.TILE;
        int W = board.getWidth() * tile;
        int H = board.getHeight() * tile;

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, W, H);

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

        if (!isGameOver) {
            gc.setFill(current.getKind().color());
            int[][] s = current.getShape();
            for (int i = 0; i < s.length; i++) {
                for (int j = 0; j < s[i].length; j++) {
                    if (s[i][j] == 1) {
                        gc.fillRect((current.getX() + j) * tile,
                                (current.getY() + i) * tile,
                                tile - 1, tile - 1);
                    }
                }
            }
            if (isPaused) {
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font(26));
                gc.fillText("Game is Paused!", W / 2.0 - 90, H / 2.0);
                gc.fillText("Press P to Continue.", W / 2.0 - 100, H / 2.0 + 40);
            }
        } else {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Monospaced Bold", 30));
            gc.fillText("GAME OVER", W / 2.0 - 90, H / 2.0);
        }
    }

    // ----------------------------------------------------
    // AI helpers & simple getters
    // ----------------------------------------------------
    public boolean isGameOver() { return isGameOver; }

    /** Returns a deep copy of the board occupancy (0/1). */
    public int[][] snapshotField() {
        int h = board.getHeight();
        int w = board.getWidth();
        int[][] src = board.getField();
        int[][] copy = new int[h][w];
        for (int i = 0; i < h; i++) System.arraycopy(src[i], 0, copy[i], 0, w);
        return copy;
    }

    /** Returns a deep copy of the current tetromino shape matrix. */
    public int[][] snapshotShape() {
        int[][] s = current.getShape();
        int[][] copy = new int[s.length][];
        for (int i = 0; i < s.length; i++) copy[i] = s[i].clone();
        return copy;
    }

    /** Current piece position (top-left of the shape in board coords). */
    public int snapshotX() { return current.getX(); }
    public int snapshotY() { return current.getY(); }

    /** True if a piece locked this frame; clears the flag on read. */
    public boolean pieceJustLocked() {
        boolean v = justLocked;
        justLocked = false;
        return v;
    }

    public int boardWidth()  { return board.getWidth(); }
    public int boardHeight() { return board.getHeight(); }
}
