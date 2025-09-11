package org.oosd.model;

import java.util.Random;

public class PieceFactory {
    private final Random random;

    /** Uses a new random seed (single-player or legacy use). */
    public PieceFactory() {
        this(new Random());
    }

    /** Inject a specific Random to control the sequence/seed. */
    public PieceFactory(Random random) {
        this.random = random;
    }

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
