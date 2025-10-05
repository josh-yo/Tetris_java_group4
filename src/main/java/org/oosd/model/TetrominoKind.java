package org.oosd.model;

import javafx.scene.paint.Color;

public enum TetrominoKind {
    L(new int[][]{{1,0},{1,0},{1,1}}, Color.BLUE),
    T(new int[][]{{0,1,0},{1,1,1}},   Color.GREEN),
    O(new int[][]{{1,1},{1,1}},       Color.YELLOW),
    I(new int[][]{{1,1,1,1}},         Color.CYAN),
    S(new int[][]{{1,1,0},{0,1,1}},   Color.ORANGE);

    private final int[][] shape;
    private final Color color;

    TetrominoKind(int[][] shape, Color color) {
        this.shape = shape;
        this.color = color;
    }

    public int[][] baseShape() { return shape; }
    public Color color() { return color; }
}
