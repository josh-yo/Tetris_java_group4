package org.oosd.ai;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.canvas.GraphicsContext;
import org.oosd.controller.GameEngine;

public class AiDriver {
    private final GameEngine engine;
    private final GraphicsContext gc;
    private final TetrisAI ai = new TetrisAI();
    private Timeline tl;


    // current plan
    private Integer targetX = null;
    private int rotations = 0;

    public AiDriver(GameEngine engine, GraphicsContext gc) {
        this.engine = engine; this.gc = gc;
    }

    public void start() {
        if (tl != null) tl.stop();
        tl = new Timeline(new KeyFrame(Duration.millis(70), e -> tick()));
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();
    }

    public void stop() { if (tl != null) tl.stop(); }

    private void tick() {
        if (engine.isGameOver()) { stop(); return; }

        // If no active plan, compute one from snapshots
        if (targetX == null) {
            int[][] field = engine.snapshotField();
            int[][] shape = engine.snapshotShape();
            int cx = engine.snapshotX();
            int cy = engine.snapshotY();

            TetrisAI.Plan plan = ai.findBest(field, shape, cx, cy);
            targetX = plan.targetX;
            rotations = plan.rotations;
        }

        // Execute plan: rotate first
        if (rotations > 0) {
            engine.rotate(gc);
            rotations--;
            return;
        }
        // then move horizontally to targetX
        int cx = engine.snapshotX();
        if (cx < targetX) { engine.moveRight(gc); return; }
        if (cx > targetX) { engine.moveLeft(gc);  return; }

        // reached column -> let gravity do it or accelerate
        engine.softDrop(gc); // gentle step down; engine will lock when needed
        // once new piece spawns, our snapshots will change -> recompute next tick
        if (engine.pieceJustLocked()) { targetX = null; }
    }
}
