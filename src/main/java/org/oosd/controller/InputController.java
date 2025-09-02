package org.oosd.controller;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

public class InputController {
    private final GameEngine engine;

    public InputController(GameEngine engine) { this.engine = engine; }

    public void handle(KeyCode code, GraphicsContext gc) {
        switch (code) {
            case LEFT  -> engine.moveLeft(gc);
            case RIGHT -> engine.moveRight(gc);
            case DOWN  -> engine.moveDown(gc);
            case UP    -> engine.rotate(gc);
            case SPACE -> engine.dropDown(gc);
            case P     -> engine.togglePause();
        }
    }
}
