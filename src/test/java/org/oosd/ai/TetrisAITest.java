package org.oosd.ai;

import org.junit.jupiter.api.Test;
import org.oosd.model.Board;
import org.oosd.model.PieceFactory;

import static org.junit.jupiter.api.Assertions.*;

class TetrisAITest {

    @Test
    void testFindBestDoesNotCrash() {
        // AI should handle a fresh board and random piece without errors
        Board board = new Board(10, 20);
        PieceFactory factory = new PieceFactory();
        TetrisAI ai = new TetrisAI();

        assertDoesNotThrow(() ->
                ai.findBest(board.getField(), factory.createRandom(board.getWidth()).getShape(), 0, 0)
        );
    }
}
