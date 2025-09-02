package org.oosd.model;

import javafx.scene.paint.Color;

public class Board {
    public static final int TILE = 30;

    private final int width;
    private final int height;
    private final int[][] field;       // 0 = empty, 1 = occupied
    private final Color[][] fieldColor;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.field = new int[height][width];
        this.fieldColor = new Color[height][width];
    }

    public int getWidth()  { return width; }
    public int getHeight() { return height; }
    public int[][] getField() { return field; }
    public Color[][] getFieldColor() { return fieldColor; }

    /** Check if the shape can be placed at position (x, y) */
    public boolean isValidPosition(int[][] shape, int x, int y) {
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 1) continue;
                int nx = x + j;
                int ny = y + i;
                if (nx < 0 || nx >= width || ny >= height) return false;
                if (ny >= 0 && field[ny][nx] == 1) return false;
            }
        }
        return true;
    }

    /** Fix the current falling block onto the board */
    public void fixShape(int[][] shape, int x, int y, Color color) {
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 1) continue;
                int nx = x + j;
                int ny = y + i;
                if (ny >= 0 && ny < height && nx >= 0 && nx < width) {
                    field[ny][nx] = 1;
                    fieldColor[ny][nx] = color;
                }
            }
        }
    }

    /** Remove all completed rows and shift above rows down */
    public void clearFullRows() {
        for (int r = 0; r < height; r++) {
            boolean full = true;
            for (int c = 0; c < width; c++) {
                if (field[r][c] == 0) { full = false; break; }
            }
            if (full) {
                for (int rr = r; rr > 0; rr--) {
                    System.arraycopy(field[rr - 1], 0, field[rr], 0, width);
                    System.arraycopy(fieldColor[rr - 1], 0, fieldColor[rr], 0, width);
                }
                for (int c = 0; c < width; c++) {
                    field[0][c] = 0;
                    fieldColor[0][c] = null;
                }
                r--; // re-check the same row after shifting
            }
        }
    }
}
