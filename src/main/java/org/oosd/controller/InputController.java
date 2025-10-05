package org.oosd.controller;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import org.oosd.service.ConfigService;
import org.oosd.service.Effect;

public class InputController {
    private final GameEngine engine;

    public InputController(GameEngine engine) { this.engine = engine; }

    private void playMoveSfx() {
        if (ConfigService.getInstance().get().isSoundOn()) {
            Effect.MOVE.play();
        }
    }

    public void handle(KeyCode code, GraphicsContext gc) {
        switch (code) {
            case LEFT  -> { engine.moveLeft(gc);  playMoveSfx(); }
            case RIGHT -> { engine.moveRight(gc); playMoveSfx(); }
            case DOWN  -> {
                engine.softDrop(gc);
            }
            case UP    -> { engine.rotate(gc);    playMoveSfx(); }
            case SPACE -> { engine.dropDown(gc); /* usually 1 sound enough */ }
            case P     -> engine.togglePause();
        }
    }
}
