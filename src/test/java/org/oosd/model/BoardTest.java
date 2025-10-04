package org.oosd.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void testBoardInitialization() {
        // New board should have correct dimensions
        Board board = new Board(10, 20);
        assertEquals(10, board.getWidth());
        assertEquals(20, board.getHeight());
    }

    @Test
    void testClearFullLines() {
        // Fill the bottom row completely, then clear
        Board board = new Board(4, 4);
        int[][] field = board.getField();
        for (int j = 0; j < 4; j++) {
            field[3][j] = 1;
        }

        int cleared = board.clearFullRows();
        assertEquals(1, cleared, "Should clear one full line");
    }
}
