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

public class tetris extends Application {

    // Game board settings
    private static final int TILE = 30, WIDTH = 10, HEIGHT = 20;

    private final int[][][] SHAPES = {
        { // L-block
            {1, 0},
            {1, 0},
            {1, 1}
        },
        { // T-block
            {0, 1, 0},
            {1, 1, 1}
        },
        { // O-block
            {1, 1},
            {1, 1}
        },
        { // I-block
            {1, 1, 1, 1}
        },
        { // S/Z-block
            {1, 1, 0},
            {0, 1, 1}
        }
    };

    // Block colors for each shape
    private final Color[] SHAPE_COLORS = {
        Color.BLUE,
        Color.GREEN,
        Color.YELLOW,
        Color.CYAN,
        Color.ORANGE
    };

    // Game state
    private boolean isGameOver = false;
    private boolean isPaused = false;

    // The game field/grid and its color mapping
    private int[][] field = new int[HEIGHT][WIDTH];
    private Color[][] fieldColor = new Color[HEIGHT][WIDTH];

    // Current active block
    private int[][] currentBlock;
    private int blockX = 3, blockY = 0;
    private int shapeType;

    // JavaFX animation timer for auto-drop
    private Timeline timeline;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        // Set up canvas and rendering context
        Canvas canvas = new Canvas(WIDTH * TILE, HEIGHT * TILE);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);

        // Start spawning block
        spawnBlock();
        draw(gc);

        timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            moveBlock(0, 1);
            draw(gc);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        scene.setOnKeyPressed(e -> {

            // Toggle pause when the 'P' key is pressed
            if (e.getCode() == KeyCode.P){
                 isPaused = !isPaused;
                 updateTimeline();
                 draw(gc);
                }

            if (isGameOver || isPaused ) return; // Ignore input if game is over or paused

            // Handle block movement and rotation based on arrow keys
            if (e.getCode() == KeyCode.LEFT) moveBlock(-1, 0);
                else if (e.getCode() == KeyCode.RIGHT) moveBlock(1, 0);
                else if (e.getCode() == KeyCode.DOWN) moveBlock(0, 1);
                else if (e.getCode() == KeyCode.UP) rotateBlock();
                draw(gc);

        });

        // Setup window
        stage.setTitle("Tetris");
        stage.setScene(scene);
        stage.show();
    }

    // Randomly choose and spawn a new block at the top of the screen
    private void spawnBlock() {
        shapeType = (int) (Math.random() * SHAPES.length);
        currentBlock = SHAPES[shapeType];
        blockX = (WIDTH - currentBlock[0].length) / 2;
        blockY = 0;

        // If new block overlaps existing blocks, end the game
        if (!canMove(0, 0, currentBlock)) {
            isGameOver = true;
            timeline.stop(); // Stop falling animation
        }
    }

    // Move the block by dx and dy if the space is valid
    private void moveBlock(int dx, int dy) {
        if (isGameOver) return;

        if (canMove(dx, dy, currentBlock)) {
            blockX += dx;
            blockY += dy;
        } else if (dy == 1) {
            // Block has landed → merge into field
            for (int i = 0; i < currentBlock.length; i++) {
                for (int j = 0; j < currentBlock[i].length; j++) {
                    if (currentBlock[i][j] == 1) {
                        field[blockY + i][blockX + j] = 1;
                        fieldColor[blockY + i][blockX + j] = SHAPE_COLORS[shapeType];
                    }
                }
            }
            // Try to spawn a new block
            spawnBlock();
        }
    }

    // Check if the block can move to the given offset
    private boolean canMove(int dx, int dy, int[][] shape) {
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    int newX = blockX + j + dx;
                    int newY = blockY + i + dy;

                    // Out of bounds or hits filled block
                    if (newX < 0 || newX >= WIDTH || newY >= HEIGHT || (newY >= 0 && field[newY][newX] == 1)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Rotate the block clockwise
    private void rotateBlock() {
        int rows = currentBlock.length;
        int cols = currentBlock[0].length;
        int[][] rotated = new int[cols][rows];

        // Transpose + reverse each row = rotate right
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                rotated[j][rows - 1 - i] = currentBlock[i][j];

        // Only apply rotation if space is valid
        if (canMove(0, 0, rotated))
            currentBlock = rotated;
    }

    // Draw the game field and current block
    private void draw(GraphicsContext gc) {
        // Clear the canvas
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH * TILE, HEIGHT * TILE);

        // Draw current falling block
        gc.setFill(SHAPE_COLORS[shapeType]);
        for (int i = 0; i < currentBlock.length; i++) {
            for (int j = 0; j < currentBlock[i].length; j++) {
                if (currentBlock[i][j] == 1) {
                    gc.fillRect((blockX + j) * TILE, (blockY + i) * TILE, TILE - 1, TILE - 1);
                }
            }
        }

        // Draw blocks already merged in the field
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (field[i][j] == 1) {
                    gc.setFill(fieldColor[i][j] != null ? fieldColor[i][j] : Color.GRAY);
                    gc.fillRect(j * TILE, i * TILE, TILE - 1, TILE - 1);
                }
            }
        }

        // If the game is over, show "GAME OVER" message
        if (isGameOver) {
            gc.setFill(Color.WHITE);
            gc.setFont(new javafx.scene.text.Font(30));
            gc.fillText("GAME OVER", WIDTH * TILE / 2 - 90, HEIGHT * TILE / 2);
        }
        // If the game is currently paused and not over, show "Game is Paused!"
        if (isPaused && !isGameOver) {
            gc.setFill(Color.WHITE);
            gc.setFont(new javafx.scene.text.Font(28));
            gc.fillText("Game is Paused!", WIDTH * TILE / 2 - 80, HEIGHT * TILE / 2);
            gc.fillText("Press P to Continue.", WIDTH * TILE / 2 - 100, HEIGHT * TILE / 2 + 40);
        }
    }
    // Method to start or stop the game timeline depending on pause state
    private void updateTimeline() {
        if(isPaused){
            timeline.stop();
        }
        else {
            timeline.play();
        }
    }
}
