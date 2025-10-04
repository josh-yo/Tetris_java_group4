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

    private int score = 0;
    private int linesClearedTotal = 0;

    // --- AI helpers ---
    private boolean justLocked = false;
    private long pieceId = 0;

    // --- test mode flag (prevents JavaFX animations in unit tests) ---
    private boolean testing = false;

    /** Legacy: creates its own PieceFactory (random seed). */
    public GameEngine(Board board) {
        this(board, new PieceFactory());
    }

    /** Preferred: inject a PieceFactory (so we can control seed/sequence). */
    public GameEngine(Board board, PieceFactory factory) {
        this.board = board;
        this.factory = factory;
    }

    /** Special constructor for tests (skips JavaFX animation loop). */
    public GameEngine(Board board, PieceFactory factory, boolean testing) {
        this.board = board;
        this.factory = factory;
        this.testing = testing;
    }

    public void setLevel(int level) {
        // simple linear mapping (1..10) -> (800..120) ms
        fallMs = Math.max(120, 900 - level * 80);
        if (timeline != null) {
            timeline.stop();
            start(gcRef);
        }
    }

    public void setOnGameOver(Runnable r){ this.onGameOver = r; }

    public int getScore() { return score; }
    public int getLinesCleared() { return linesClearedTotal; }

    // ===== lifecycle =====
    public void start(GraphicsContext gc) {
        this.gcRef = gc;
        spawnNew();
        draw(gc);

        // Skip animations in test mode
        if (!testing) {
            timeline = new Timeline(new KeyFrame(Duration.millis(fallMs), e -> {
                if (!isGameOver && !isPaused) softDrop(gcRef);  // automatic gravity
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
    }

    public void stop() { if (timeline != null) timeline.stop(); }

    public void togglePause() {
        isPaused = !isPaused;
        if (timeline != null) {
            if (isPaused) timeline.pause(); else timeline.play();
        }
        if (gcRef != null) draw(gcRef);
    }

    // ===== player actions =====
    public void moveLeft(GraphicsContext gc)  { tryMove(-1, 0); draw(gc); }
    public void moveRight(GraphicsContext gc) { tryMove( 1, 0); draw(gc); }

    /** soft drop by user (returns true if actually moved) */
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

    // ===== internals =====
    private void spawnNew() {
        current = factory.createRandom(board.getWidth());
        pieceId++;                 // new identity for AI replanning
        if (!board.isValidPosition(current.getShape(), current.getX(), current.getY())) {
            isGameOver = true;
            if (timeline != null) timeline.stop();
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
        justLocked = true; // let AI know

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

    // ===== rendering =====
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

    // ==== AI & UI snapshots ====

    public boolean isGameOver() { return isGameOver; }

    /** one-tick flag telling AI the previous piece just locked */
    public boolean pieceJustLocked() {
        boolean was = justLocked;
        justLocked = false;
        return was;
    }

    public long snapshotPieceId() { return pieceId; }

    public TetrominoKind snapshotKind() { return current.getKind(); }

    public int[][] snapshotField() {
        int h = board.getHeight();
        int w = board.getWidth();
        int[][] src = board.getField();
        int[][] copy = new int[h][w];
        for (int i = 0; i < h; i++) System.arraycopy(src[i], 0, copy[i], 0, w);
        return copy;
    }

    public int[][] snapshotShape() {
        int[][] s = current.getShape();
        int[][] copy = new int[s.length][];
        for (int i = 0; i < s.length; i++) copy[i] = s[i].clone();
        return copy;
    }

    public int snapshotX() { return current.getX(); }
    public int snapshotY() { return current.getY(); }

    public int boardWidth()  { return board.getWidth(); }
    public int boardHeight() { return board.getHeight(); }

    // --- next-piece preview for GameScreen ---
    public int[][] snapshotNextShape() {
        Tetromino next = factory.peekNext();
        if (next == null) return null;
        int[][] s = next.getShape();
        int[][] copy = new int[s.length][];
        for (int i = 0; i < s.length; i++) copy[i] = s[i].clone();
        return copy;
    }

    public Color nextColor() {
        Tetromino next = factory.peekNext();
        return (next != null) ? next.getKind().color() : Color.GRAY;
    }
    public Board getBoard() {
        return board;
    }

    public Tetromino getCurrentPiece() {
        return current;
    }

}
