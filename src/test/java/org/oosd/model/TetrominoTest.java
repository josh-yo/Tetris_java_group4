package org.oosd.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TetrominoTest {

    @Test
    void testMoveDown() {
        // Moving down should increase Y coordinate
        Tetromino t = new Tetromino(TetrominoKind.O, TetrominoKind.O.baseShape(), 0, 0);

        int initialY = t.getY();
        t.move(0, 1);

        assertEquals(initialY + 1, t.getY(), "Y should increase by 1 after moving down");
    }

    @Test
    void testRotation() {
        // Rotating should produce a valid new shape
        Tetromino t = new Tetromino(TetrominoKind.I, TetrominoKind.I.baseShape(), 0, 0);
        int[][] rotated = t.rotatedShape();

        assertNotNull(rotated, "Rotation should return a valid shape");
        assertTrue(rotated.length > 0, "Rotated shape should have at least one row");
    }
}
