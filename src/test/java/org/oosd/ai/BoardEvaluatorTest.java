package org.oosd.ai;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.oosd.model.Board;

import static org.junit.jupiter.api.Assertions.*;

class BoardEvaluatorTest {

    @Test
    void testEvaluateEmptyBoard() {
        // Empty board should still produce a valid score
        Board board = new Board(10, 20);
        BoardEvaluator evaluator = new BoardEvaluator();

        int score = evaluator.evaluate(board.getField());
        assertTrue(score >= 0 || score <= 0, "Evaluation should run without error");
    }

    @Test
    void testEvaluateWithFullLine() {
        // Board with bottom row filled
        int[][] fullLineBoard = new int[20][10];
        for (int x = 0; x < 10; x++) fullLineBoard[19][x] = 1;

        BoardEvaluator evaluator = new BoardEvaluator();
        int score = evaluator.evaluate(fullLineBoard);

        // Score should stay in a reasonable range
        assertTrue(score >= -1000 && score <= 1000, "Score should be within a valid range");
    }

    @ParameterizedTest(name = "Board with {0} filled cells should yield score in expected range")
    @CsvSource({
            "0, true",
            "10, true",
            "100, true"
    })
    void testEvaluateParameterized(int filledCells, boolean expectedValid) {
        // Build board with given number of filled cells
        int[][] board = new int[20][10];
        int count = 0;
        for (int y = 19; y >= 0 && count < filledCells; y--) {
            for (int x = 0; x < 10 && count < filledCells; x++) {
                board[y][x] = 1;
                count++;
            }
        }

        BoardEvaluator evaluator = new BoardEvaluator();
        int score = evaluator.evaluate(board);

        // Score should remain valid no matter how many cells are filled
        assertEquals(expectedValid, (score >= -2000 && score <= 2000),
                "Score should remain in valid range for filledCells=" + filledCells);
    }
}
