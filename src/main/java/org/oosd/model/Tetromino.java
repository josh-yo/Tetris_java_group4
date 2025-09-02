package org.oosd.model;

public class Tetromino {
    private final TetrominoKind kind;
    private int[][] shape;
    private int x;
    private int y;

    public Tetromino(TetrominoKind kind, int[][] shape, int startX, int startY) {
        this.kind = kind;
        this.shape = shape;
        this.x = startX;
        this.y = startY;
    }

    public TetrominoKind getKind() { return kind; }
    public int[][] getShape() { return shape; }
    public int getX() { return x; }
    public int getY() { return y; }

    public void move(int dx, int dy) { x += dx; y += dy; }

    /** Return a rotated version of the current shape (not applied yet) */
    public int[][] rotatedShape() {
        int rows = shape.length, cols = shape[0].length;
        int[][] rotated = new int[cols][rows];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                rotated[j][rows - 1 - i] = shape[i][j];
        return rotated;
    }

    /** Apply the given rotated shape to this tetromino */
    public void applyRotation(int[][] rotated) { this.shape = rotated; }
}
