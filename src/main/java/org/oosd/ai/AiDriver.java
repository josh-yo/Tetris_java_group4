package org.oosd.ai;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;
import org.oosd.controller.GameEngine;
import org.oosd.model.Board;

public class AiDriver {
    private final GameEngine engine;
    private final GraphicsContext gc;
    private Timeline tl;
    private Integer targetX = null;
    private Integer rotations = null;
    private long lastPlannedPieceId = -1;

    public AiDriver(GameEngine engine, GraphicsContext gc) {
        this.engine = engine;
        this.gc = gc;
    }

    public void start() {
        stop();
        tl = new Timeline(new KeyFrame(Duration.millis(120), e -> tick()));
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();
    }

    public void stop() { if (tl != null) { tl.stop(); tl = null; } }

    private void tick() {
        if (engine.isGameOver()) { stop(); return; }

        long pieceId = engine.snapshotPieceId();
        if (pieceId != lastPlannedPieceId) {
            Move m = findBestMove();
            targetX   = m.x;
            rotations = m.rot;
            lastPlannedPieceId = pieceId;
        }

        if (rotations != null && rotations > 0) {
            engine.rotate(gc);
            rotations--;
            return;
        }
        int cx = engine.snapshotX();
        if (cx < targetX) { engine.moveRight(gc); return; }
        if (cx > targetX) { engine.moveLeft(gc);  return; }
        engine.softDrop(gc); // aligned: descend
    }

    private Move findBestMove() {
        int[][] original = engine.snapshotShape();
        int bestX = 0, bestRot = 0;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (int rot = 0; rot < 4; rot++) {
            int[][] rotated = rotateShape(original, rot);
            int w = rotated[0].length;

            for (int x = 0; x <= engine.boardWidth() - w; x++) {
                Board clone = new Board(engine.boardWidth(), engine.boardHeight());
                clone.copyFrom(engine.snapshotField());

                int y = 0;
                while (clone.isValidPosition(rotated, x, y + 1)) y++;
                if (!clone.isValidPosition(rotated, x, y)) continue;

                clone.fixShape(rotated, x, y, engine.snapshotKind().color());
                int cleared = clone.clearFullRows();

                double score = evaluateBoard(clone, cleared);
                if (score > bestScore) {
                    bestScore = score;
                    bestRot = rot;
                    bestX = x;
                }
            }
        }
        return new Move(bestX, bestRot);
    }

    private double evaluateBoard(Board b, int cleared) {
        int holes = countHoles(b);
        int aggH  = aggregateHeight(b);
        int bump  = bumpiness(b);

        return cleared * 1200 - holes * 600 - aggH * 5 - bump * 5;
    }

    private int countHoles(Board b) {
        int h = b.getHeight(), w = b.getWidth();
        int[][] f = b.getField();
        int holes = 0;
        for (int x = 0; x < w; x++) {
            boolean seen = false;
            for (int y = 0; y < h; y++) {
                if (f[y][x] == 1) seen = true;
                else if (seen) holes++;
            }
        }
        return holes;
    }

    private int aggregateHeight(Board b) {
        int h = b.getHeight(), w = b.getWidth();
        int[][] f = b.getField();
        int sum = 0;
        for (int x = 0; x < w; x++) {
            int colH = 0;
            for (int y = 0; y < h; y++) {
                if (f[y][x] == 1) { colH = h - y; break; }
            }
            sum += colH;
        }
        return sum;
    }

    private int bumpiness(Board b) {
        int h = b.getHeight(), w = b.getWidth();
        int[][] f = b.getField();
        int[] heights = new int[w];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (f[y][x] == 1) { heights[x] = h - y; break; }
            }
        }
        int s = 0;
        for (int i = 0; i < w - 1; i++) s += Math.abs(heights[i] - heights[i+1]);
        return s;
    }

    private int[][] rotateShape(int[][] shape, int times) {
        int[][] r = shape;
        for (int i = 0; i < times; i++) r = rot90(r);
        return r;
    }
    private int[][] rot90(int[][] m) {
        int H = m.length, W = m[0].length;
        int[][] out = new int[W][H];
        for (int i = 0; i < H; i++)
            for (int j = 0; j < W; j++)
                out[j][H - 1 - i] = m[i][j];
        return out;
    }

    private static class Move { final int x, rot; Move(int x, int rot) { this.x = x; this.rot = rot; } }
}
