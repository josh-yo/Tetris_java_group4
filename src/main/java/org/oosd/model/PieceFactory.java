package org.oosd.model;

import java.util.Random;

public class PieceFactory {
    private final Random random = new Random();

    /** Create a new tetromino centered horizontally based on the board width */
    public Tetromino createRandom(int boardWidth) {
        TetrominoKind kind = TetrominoKind.values()[random.nextInt(TetrominoKind.values().length)];
        int[][] shape = copy(kind.baseShape());
        int startX = (boardWidth - shape[0].length) / 2;
        return new Tetromino(kind, shape, startX, 0);
    }

    private static int[][] copy(int[][] src) {
        int[][] dst = new int[src.length][];
        for (int i = 0; i < src.length; i++) dst[i] = src[i].clone();
        return dst;
    }
}
