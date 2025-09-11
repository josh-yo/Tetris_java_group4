package org.oosd.ai;

public class TetrisAI {
    public static final class Plan {
        public final int targetX;      // desired left-most X
        public final int rotations;    // number of CW rotations (0..3)
        public Plan(int x, int r){ targetX = x; rotations = r; }
    }

    private final BoardEvaluator eval = new BoardEvaluator();

    public Plan findBest(int[][] field, int[][] shape, int startX, int startY) {
        // field: HxW (0/1), shape: rh x rw (0/1)
        int H = field.length, W = field[0].length;
        int bestScore = Integer.MIN_VALUE;
        Plan best = new Plan(startX, 0);

        int[][] s = copy2(shape);
        for (int r = 0; r < 4; r++) {
            int rw = s[0].length;
            for (int x = -2; x <= W - rw + 1; x++) { // try a few out-of-bounds to allow wall kicks lite
                int dropY = dropRow(field, s, x, startY);
                if (dropY == Integer.MIN_VALUE) continue;

                int[][] sim = copy(field);
                place(sim, s, x, dropY);
                int score = eval.evaluate(sim);
                if (score > bestScore) { bestScore = score; best = new Plan(x, r); }
            }
            s = rotateCW(s);
        }
        return best;
    }

    // ----- helpers -----
    private int dropRow(int[][] f, int[][] s, int x, int yStart) {
        int y = yStart;
        while (canPlace(f, s, x, y + 1)) y++;
        return canPlace(f, s, x, y) ? y : Integer.MIN_VALUE;
    }
    private boolean canPlace(int[][] f, int[][] s, int ox, int oy) {
        int H = f.length, W = f[0].length;
        for (int i = 0; i < s.length; i++)
            for (int j = 0; j < s[i].length; j++)
                if (s[i][j] == 1) {
                    int x = ox + j, y = oy + i;
                    if (x < 0 || x >= W || y < 0 || y >= H || f[y][x] != 0) return false;
                }
        return true;
    }
    private void place(int[][] f, int[][] s, int ox, int oy) {
        for (int i = 0; i < s.length; i++)
            for (int j = 0; j < s[i].length; j++)
                if (s[i][j] == 1) f[oy + i][ox + j] = 1;
    }
    private int[][] rotateCW(int[][] a) {
        int r = a.length, c = a[0].length;
        int[][] b = new int[c][r];
        for (int i=0;i<r;i++) for (int j=0;j<c;j++) b[j][r-1-i] = a[i][j];
        return b;
    }
    private int[][] copy(int[][] src){
        int[][] d = new int[src.length][src[0].length];
        for (int i=0;i<src.length;i++) System.arraycopy(src[i],0,d[i],0,src[i].length);
        return d;
    }
    private int[][] copy2(int[][] src){
        int[][] d = new int[src.length][];
        for (int i=0;i<src.length;i++) d[i] = src[i].clone();
        return d;
    }
}
