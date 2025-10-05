package org.oosd.model;

import java.util.Random;

public class PieceFactory {
    private final Random random;
    private Tetromino next; // for preview & consistent spawning

    public PieceFactory() { this(new Random()); }
    public PieceFactory(Random random) { this.random = random; }

    private static int[][] copy(int[][] src) {
        int[][] dst = new int[src.length][];
        for (int i = 0; i < src.length; i++) dst[i] = src[i].clone();
        return dst;
    }

    private Tetromino makeRandom(int boardWidth) {
        TetrominoKind kind = TetrominoKind.values()[random.nextInt(TetrominoKind.values().length)];
        int[][] shape = copy(kind.baseShape());
        int startX = (boardWidth - shape[0].length) / 2;
        return new Tetromino(kind, shape, startX, 0);
    }

    /** Produce the current 'next', and roll a new next for later (supports preview). */
    public Tetromino createRandom(int boardWidth) {
        if (next == null) next = makeRandom(boardWidth);
        Tetromino out = next;
        next = makeRandom(boardWidth);
        return out;
    }

    /** For preview panel. */
    public Tetromino peekNext() { return next; }
}
