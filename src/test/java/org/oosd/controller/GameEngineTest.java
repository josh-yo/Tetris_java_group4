package org.oosd.controller;

import javafx.scene.canvas.GraphicsContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.oosd.model.Board;
import org.oosd.model.PieceFactory;
import org.oosd.model.Tetromino;
import org.oosd.model.TetrominoKind;

class GameEngineTest {

    // Stub factory to always return the same piece (O-block) for predictability
    static class StubPieceFactory extends PieceFactory {
        @Override
        public Tetromino createRandom(int boardWidth) {
            return new Tetromino(TetrominoKind.O, TetrominoKind.O.baseShape(), 0, 0);
        }
    }

    @Test
    @DisplayName("GameEngine starts correctly in test mode")
    void testStartGameWithStubFactory() {
        Board board = new Board(10, 20);
        StubPieceFactory stubFactory = new StubPieceFactory();
        GameEngine engine = new GameEngine(board, stubFactory, true);

        GraphicsContext gc = mock(GraphicsContext.class); // fake canvas to avoid UI calls
        engine.start(gc);

        // Engine should initialize with a board and an active piece
        assertNotNull(engine.getBoard(), "Engine should have a valid board");
        assertNotNull(engine.getCurrentPiece(), "Engine should have an active piece");
    }
}
