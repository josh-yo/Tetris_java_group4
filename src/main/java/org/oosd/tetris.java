import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
public class tetris extends Application {

    private static final int TILE = 30, WIDTH = 10, HEIGHT = 20;
    private final int[][][] SHAPES = {
            // L-block
            {
                    {1, 0},
                    {1, 0},
                    {1, 1}
            },
            // T-block
            {
                    {0, 1, 0},
                    {1, 1, 1}
            },
            // O-block
            {
                    {1,1},
                    {1,1}
            },
            // I-block
            {
                    {1,1,1,1}
            },
            // 2-block
            {
                    {1,1,0},
                    {0,1,1}
            }


    };
    private final Color[] SHAPE_COLORS = {
            Color.BLUE,
            Color.GREEN,
            Color.YELLOW,
            Color.CYAN,
            Color.ORANGE
    };


    private int[][] field = new int[HEIGHT][WIDTH];
    private Color[][] fieldColor = new Color[HEIGHT][WIDTH];
    private int[][] currentBlock;
    private int blockX = 3, blockY = 0;
    private int shapeType;
    private Timeline timeline;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH * TILE, HEIGHT * TILE);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);

        spawnBlock();
        draw(gc);

        timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            moveBlock(0, 1);
            draw(gc);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) moveBlock(-1, 0);
            else if (e.getCode() == KeyCode.RIGHT) moveBlock(1, 0);
            else if (e.getCode() == KeyCode.DOWN) moveBlock(0, 1);
            else if (e.getCode() == KeyCode.UP) rotateBlock();
            draw(gc);
        });

        stage.setTitle("Tetris");
        stage.setScene(scene);
        stage.show();
    }

    private void spawnBlock() {
        shapeType = (int) (Math.random() * SHAPES.length);
        currentBlock = SHAPES[shapeType];
        blockX = 3;
        blockY = 0;

    }

    private void moveBlock(int dx, int dy) {
        if (canMove(dx, dy, currentBlock)) {
            blockX += dx;
            blockY += dy;
        } else if (dy == 1) {
            // Merge into field
            for (int i = 0; i < currentBlock.length; i++) {
                for (int j = 0; j < currentBlock[i].length; j++) {
                    if (currentBlock[i][j] == 1) {
                        field[blockY + i][blockX + j] = 1;
                        fieldColor[blockY + i][blockX + j] = SHAPE_COLORS[shapeType];
                    }
                }
            }
            spawnBlock();
        }
    }

    private boolean canMove(int dx, int dy, int[][] shape) {
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    int newX = blockX + j + dx;
                    int newY = blockY + i + dy;
                    if (newX < 0 || newX >= WIDTH || newY >= HEIGHT || (newY >= 0 && field[newY][newX] == 1))
                        return false;
                }
            }
        }
        return true;
    }

    private void rotateBlock() {
        int rows = currentBlock.length;
        int cols = currentBlock[0].length;
        int[][] rotated = new int[cols][rows];

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                rotated[j][rows - 1 - i] = currentBlock[i][j];

        if (canMove(0, 0, rotated))
            currentBlock = rotated;
    }

    private void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH * TILE, HEIGHT * TILE);

        // Draw current block
        gc.setFill(SHAPE_COLORS[shapeType]);
        for (int i = 0; i < currentBlock.length; i++) {
            for (int j = 0; j < currentBlock[i].length; j++) {
                if (currentBlock[i][j] == 1) {
                    gc.fillRect((blockX + j) * TILE, (blockY + i) * TILE, TILE - 1, TILE - 1);
                }
            }
        }

        // Draw field
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (field[i][j] == 1) {
                    gc.setFill(fieldColor[i][j] != null ? fieldColor[i][j] : Color.GRAY);
                    gc.fillRect(j * TILE, i * TILE, TILE - 1, TILE - 1);
                }
            }
        }

    }
}
