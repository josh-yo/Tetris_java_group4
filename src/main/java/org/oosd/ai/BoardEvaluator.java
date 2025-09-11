package org.oosd.ai;

public class BoardEvaluator {
    public int evaluate(int[][] board) {
        int h   = height(board);
        int holes = holes(board);
        int lines = fullLines(board);
        int bump  = bumpiness(board);
        // weights inspired by the handout
        return (-4 * h) + (3 * lines) - (5 * holes) - (2 * bump);
    }

    private int height(int[][] b) {
        int H = b.length, W = b[0].length, max = 0;
        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++) {
                if (b[y][x] != 0) { max = Math.max(max, H - y); break; }
            }
        }
        return max;
    }

    private int holes(int[][] b) {
        int H = b.length, W = b[0].length, holes = 0;
        for (int x = 0; x < W; x++) {
            boolean seen = false;
            for (int y = 0; y < H; y++) {
                if (b[y][x] != 0) seen = true;
                else if (seen) holes++;
            }
        }
        return holes;
    }

    private int fullLines(int[][] b) {
        int H = b.length, W = b[0].length, cnt = 0;
        for (int y = 0; y < H; y++) {
            boolean full = true;
            for (int x = 0; x < W; x++) if (b[y][x] == 0) { full = false; break; }
            if (full) cnt++;
        }
        return cnt;
    }

    private int bumpiness(int[][] b) {
        int W = b[0].length, sum = 0;
        for (int x = 0; x < W - 1; x++) {
            sum += Math.abs(colHeight(b, x) - colHeight(b, x + 1));
        }
        return sum;
    }

    private int colHeight(int[][] b, int x) {
        int H = b.length;
        for (int y = 0; y < H; y++) if (b[y][x] != 0) return H - y;
        return 0;
    }
}
