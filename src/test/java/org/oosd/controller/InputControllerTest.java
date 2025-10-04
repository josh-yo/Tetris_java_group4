package org.oosd.controller;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class InputControllerTest {

    private GameEngine engine;
    private GraphicsContext gc;
    private InputController controller;

    @BeforeEach
    void setUp() {
        // Use mocks so we only check method calls, not real game logic
        engine = mock(GameEngine.class);
        gc = mock(GraphicsContext.class);
        controller = new InputController(engine);
    }

    @Test
    @DisplayName("Pressing LEFT should call moveLeft on engine")
    void testHandleLeft() {
        controller.handle(KeyCode.LEFT, gc);
        verify(engine).moveLeft(gc);
    }

    @Test
    @DisplayName("Pressing RIGHT should call moveRight on engine")
    void testHandleRight() {
        controller.handle(KeyCode.RIGHT, gc);
        verify(engine).moveRight(gc);
    }

    @Test
    @DisplayName("Pressing DOWN should call softDrop on engine")
    void testHandleDown() {
        controller.handle(KeyCode.DOWN, gc);
        verify(engine).softDrop(gc);
    }

    @Test
    @DisplayName("Pressing UP should call rotate on engine")
    void testHandleUp() {
        controller.handle(KeyCode.UP, gc);
        verify(engine).rotate(gc);
    }

    @Test
    @DisplayName("Pressing SPACE should call dropDown on engine")
    void testHandleSpace() {
        controller.handle(KeyCode.SPACE, gc);
        verify(engine).dropDown(gc);
    }

    @Test
    @DisplayName("Pressing P should toggle pause")
    void testHandlePause() {
        controller.handle(KeyCode.P, gc);
        verify(engine).togglePause();
    }
}
