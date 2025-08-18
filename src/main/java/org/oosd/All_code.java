package org.oosd;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class All_code extends Application {

    private StackPane root;
    private Scene scene;

    private int cfgWidth = 10;
    private int cfgHeight = 20;
    private int cfgLevel = 1;

    @Override
    public void start(Stage primaryStage) {
        root = new StackPane();
        scene = new Scene(root, 600, 700);

        showMainScreen();

        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Multi-Screen Game");
        primaryStage.show();

        showSplashThenMain();
    }

    private void showSplashThenMain() {
        Stage splashStage = new Stage(StageStyle.UNDECORATED);

        Label courseCode = new Label("Group 4 — 7010ICT");
        Label loadingLabel = new Label("Loading, please wait...");
        String boldStyle = "-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: red;";
        courseCode.setStyle(boldStyle);
        loadingLabel.setStyle(boldStyle);

        StackPane.setAlignment(courseCode, Pos.CENTER);
        StackPane.setAlignment(loadingLabel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(loadingLabel, new Insets(0, 0, 20, 0));

        StackPane splashLayout = new StackPane(courseCode, loadingLabel);

        Scene splashScene = new Scene(splashLayout, 600, 500);
        splashStage.setScene(splashScene);
        splashStage.show();

        new Thread(() -> {
            try { Thread.sleep(1500); } catch (InterruptedException ignore) {}
            Platform.runLater(() -> {
                splashStage.close();
                showMainScreen();
            });
        }).start();
    }

    private void showMainScreen() {
        VBox mainScreen = new VBox(10);
        mainScreen.setPadding(new Insets(20));

        Label label = new Label("Main Menu");
        label.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        HBox top = new HBox(label);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(20, 0, 10, 0));

        Button startButton = new Button("Play");
        Button configButton = new Button("Configuration");
        Button scoreButton = new Button("High Scores");
        Button exitButton = new Button("Exit");

        double BTN_WIDTH = 240, BTN_HEIGHT = 40;
        for (Button b : new Button[]{startButton, configButton, scoreButton, exitButton}) {
            b.setPrefWidth(BTN_WIDTH);
            b.setMinHeight(BTN_HEIGHT);
        }

        startButton.setOnAction(e -> showGameScreen());
        configButton.setOnAction(e -> showConfigScreen());
        scoreButton.setOnAction(e -> showScoreScreen());
        exitButton.setOnAction(e -> showExitConfirmation());

        VBox buttonsCol = new VBox(14, startButton, configButton, scoreButton, exitButton);
        buttonsCol.setAlignment(Pos.CENTER);

        BorderPane layout = new BorderPane();
        layout.setTop(top);
        layout.setCenter(buttonsCol);
        layout.setPadding(new Insets(10));

        root.getChildren().setAll(layout);
    }

    private void showScoreScreen() {
        VBox scoreScreen = new VBox(20);
        scoreScreen.setPadding(new Insets(30));

        Label title = new Label("High Scores");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.TOP_CENTER);

        Label nameHeader = new Label("Name");
        nameHeader.setMinWidth(200);
        Label scoreHeader = new Label("Score");
        scoreHeader.setMinWidth(200);
        nameHeader.setStyle("-fx-font-weight: bold");
        scoreHeader.setStyle("-fx-font-weight: bold");

        HBox header = new HBox(50, nameHeader, scoreHeader);

        String[][] data = {
                {"Anand", "969313"},
                {"Antony", "755659"},
                {"Yeongjoo", "642871"},
                {"Josh", "540820"},
                {"Siddharth", "537728"},
                {"Bob", "531328"},
                {"Alice", "499000"},
                {"Max", "485078"},
                {"Tom", "460078"},
                {"Pearl", "345678"}
        };

        VBox scoreList = new VBox(8);
        for (String[] entry : data) {
            Label name = new Label(entry[0]);
            name.setMinWidth(200);
            Label score = new Label(entry[1]);
            score.setMinWidth(200);
            scoreList.getChildren().add(new HBox(50, name, score));
        }

        Button back = new Button("Back");
        back.setOnAction(e -> showMainScreen());

        scoreScreen.getChildren().addAll(titleBox, header, scoreList, back);
        root.getChildren().setAll(scoreScreen);
    }

    private void showConfigScreen() {
        VBox configScreen = new VBox(14);
        configScreen.setPadding(new Insets(20));

        Label label = new Label("Configuration");
        label.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        HBox top = new HBox(label);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(0, 0, 10, 0));

        // Width
        Label widthLabel = new Label("Game Width (cells):");
        widthLabel.setMinWidth(180);
        widthLabel.setStyle("-fx-font-weight: bold");
        Slider widthSlider = new Slider(5, 15, cfgWidth);
        widthSlider.setMajorTickUnit(1);
        widthSlider.setMinorTickCount(0);
        widthSlider.setShowTickLabels(true);
        widthSlider.setShowTickMarks(true);
        widthSlider.setSnapToTicks(true);
        Label currentWidth = new Label(String.valueOf(cfgWidth));
        widthSlider.valueProperty().addListener((obs, o, n) -> currentWidth.setText(String.valueOf(n.intValue())));

        // Height
        Label heightLabel = new Label("Game Height (cells):");
        heightLabel.setMinWidth(180);
        heightLabel.setStyle("-fx-font-weight: bold");
        Slider heightSlider = new Slider(15, 30, cfgHeight);
        heightSlider.setMajorTickUnit(1);
        heightSlider.setMinorTickCount(0);
        heightSlider.setShowTickLabels(true);
        heightSlider.setShowTickMarks(true);
        heightSlider.setSnapToTicks(true);
        Label currentHeight = new Label(String.valueOf(cfgHeight));
        heightSlider.valueProperty().addListener((obs, o, n) -> currentHeight.setText(String.valueOf(n.intValue())));

        // Level
        Label levelLabel = new Label("Game Level:");
        levelLabel.setMinWidth(180);
        levelLabel.setStyle("-fx-font-weight: bold");
        Slider levelSlider = new Slider(1, 10, cfgLevel);
        levelSlider.setMajorTickUnit(1);
        levelSlider.setMinorTickCount(0);
        levelSlider.setShowTickLabels(true);
        levelSlider.setShowTickMarks(true);
        levelSlider.setSnapToTicks(true);
        Label currentLevel = new Label(String.valueOf(cfgLevel));
        levelSlider.valueProperty().addListener((obs, o, n) -> currentLevel.setText(String.valueOf(n.intValue())));

        HBox wBox = new HBox(10, widthLabel, widthSlider, currentWidth);
        HBox hBox = new HBox(10, heightLabel, heightSlider, currentHeight);
        HBox lBox = new HBox(10, levelLabel, levelSlider, currentLevel);

        Button save = new Button("Save");
        Button back = new Button("Back");
        save.setOnAction(e -> {
            cfgWidth = (int) widthSlider.getValue();
            cfgHeight = (int) heightSlider.getValue();
            cfgLevel = (int) levelSlider.getValue();
            showMainScreen();
        });
        back.setOnAction(e -> showMainScreen());

        HBox btns = new HBox(10, save, back);
        btns.setAlignment(Pos.CENTER);

        configScreen.getChildren().addAll(top, wBox, hBox, lBox, btns);
        root.getChildren().setAll(configScreen);
    }

    private void showGameScreen() {
        BorderPane gameLayout = new BorderPane();

        TetrisGame game = new TetrisGame(cfgWidth, cfgHeight, cfgLevel);

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(10);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("Back");
        backButton.setFocusTraversable(false);
        Label info = new Label("Controls: ← → move, ↑ rotate, ↓ soft drop, SPACE hard drop, P pause");
        info.setStyle("-fx-font-size: 12px; -fx-text-fill: #333;");

        backButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit Game");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to go back to main menu?");
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(no, yes);

            alert.showAndWait().ifPresent(result -> {
                if (result == yes) {
                    game.stop();
                    showMainScreen();
                }
            });
        });

        topBar.getChildren().addAll(backButton, info);
        gameLayout.setTop(topBar);

        StackPane centerPane = new StackPane(game);
        centerPane.setAlignment(Pos.CENTER);
        gameLayout.setCenter(centerPane);

        root.getChildren().setAll(gameLayout);

        Platform.runLater(game::requestFocus);
    }

    private void showExitConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to exit?");

        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(no, yes);

        alert.showAndWait().ifPresent(result -> {
            if (result == yes) Platform.exit();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    static class TetrisGame extends Pane {
        private static final int TILE = 30;

        private final int WIDTH;
        private final int HEIGHT;

        private final int[][][] SHAPES = {
                {{1, 0}, {1, 0}, {1, 1}},
                {{0, 1, 0}, {1, 1, 1}},
                {{1, 1}, {1, 1}},
                {{1, 1, 1, 1}},
                {{1, 1, 0}, {0, 1, 1}}
        };

        private final Color[] SHAPE_COLORS = {
                Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN, Color.ORANGE
        };

        private boolean isGameOver = false;
        private boolean isPaused = false;

        private int[][] field;
        private Color[][] fieldColor;

        private int[][] currentBlock;
        private int blockX = 3, blockY = 0;
        private int shapeType;

        private Timeline timeline;
        private GraphicsContext gc;

        private int score = 0;
        private int linesCleared = 0;
        private final int level;
        private final int dropMillis;

        public TetrisGame(int widthCells, int heightCells, int level) {
            this.WIDTH = widthCells;
            this.HEIGHT = heightCells;
            this.level = Math.max(1, Math.min(10, level));
            this.dropMillis = Math.max(70, 650 - (this.level - 1) * 60);

            setPrefSize(WIDTH * TILE, HEIGHT * TILE);

            Canvas canvas = new Canvas(WIDTH * TILE, HEIGHT * TILE);
            gc = canvas.getGraphicsContext2D();
            getChildren().add(canvas);

            field = new int[HEIGHT][WIDTH];
            fieldColor = new Color[HEIGHT][WIDTH];

            spawnBlock();
            draw();

            timeline = new Timeline(new KeyFrame(Duration.millis(dropMillis), e -> {
                moveBlock(0, 1);
                draw();
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

            setFocusTraversable(true);
            setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.P) {
                    isPaused = !isPaused;
                    updateTimeline();
                    draw();
                    return;
                }
                if (isGameOver || isPaused) return;

                if (e.getCode() == KeyCode.LEFT) moveBlock(-1, 0);
                else if (e.getCode() == KeyCode.RIGHT) moveBlock(1, 0);
                else if (e.getCode() == KeyCode.DOWN) moveBlock(0, 1);
                else if (e.getCode() == KeyCode.UP) rotateBlock();
                else if (e.getCode() == KeyCode.SPACE) hardDrop();

                draw();
            });

            Rectangle border = new Rectangle(WIDTH * TILE, HEIGHT * TILE);
            border.setFill(Color.TRANSPARENT);
            border.setStroke(Color.DARKGRAY);
            getChildren().add(border);

            requestFocus();
        }

        public void stop() { if (timeline != null) timeline.stop(); }

        private void spawnBlock() {
            shapeType = (int) (Math.random() * SHAPES.length);
            currentBlock = SHAPES[shapeType];
            blockX = Math.max(0, (WIDTH - currentBlock[0].length) / 2);
            blockY = 0;

            if (!canMove(0, 0, currentBlock)) {
                isGameOver = true;
                if (timeline != null) timeline.stop();
            }
        }

        private void moveBlock(int dx, int dy) {
            if (isGameOver) return;

            if (canMove(dx, dy, currentBlock)) {
                blockX += dx;
                blockY += dy;
            } else if (dy == 1) {
                for (int i = 0; i < currentBlock.length; i++) {
                    for (int j = 0; j < currentBlock[i].length; j++) {
                        if (currentBlock[i][j] == 1) {
                            int fy = blockY + i;
                            int fx = blockX + j;
                            if (fy >= 0 && fy < HEIGHT && fx >= 0 && fx < WIDTH) {
                                field[fy][fx] = 1;
                                fieldColor[fy][fx] = SHAPE_COLORS[shapeType];
                            }
                        }
                    }
                }
                eraseRow();
                spawnBlock();
            }
        }

        private boolean canMove(int dx, int dy, int[][] shape) {
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[i].length; j++) {
                    if (shape[i][j] == 1) {
                        int newX = blockX + j + dx;
                        int newY = blockY + i + dy;
                        if (newX < 0 || newX >= WIDTH || newY >= HEIGHT) return false;
                        if (newY >= 0 && field[newY][newX] == 1) return false;
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
            if (canMove(0, 0, rotated)) currentBlock = rotated;
        }

        private void hardDrop() {
            while (canMove(0, 1, currentBlock)) blockY++;
            moveBlock(0, 1);
        }

        private void draw() {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, WIDTH * TILE, HEIGHT * TILE);

            for (int i = 0; i < HEIGHT; i++)
                for (int j = 0; j < WIDTH; j++)
                    if (field[i][j] == 1) {
                        gc.setFill(fieldColor[i][j] != null ? fieldColor[i][j] : Color.GRAY);
                        gc.fillRect(j * TILE, i * TILE, TILE - 1, TILE - 1);
                    }

            gc.setFill(SHAPE_COLORS[shapeType]);
            for (int i = 0; i < currentBlock.length; i++)
                for (int j = 0; j < currentBlock[i].length; j++)
                    if (currentBlock[i][j] == 1)
                        gc.fillRect((blockX + j) * TILE, (blockY + i) * TILE, TILE - 1, TILE - 1);

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(16));
            gc.fillText("Score: " + score, 8, 20);
            gc.fillText("Lines: " + linesCleared, 8, 40);
            gc.fillText("Level: " + level, 8, 60);

            if (isGameOver) {
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font(30));
                gc.fillText("GAME OVER !", WIDTH * TILE / 2.0 - 90, HEIGHT * TILE / 2.0);
            } else if (isPaused) {
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font(28));
                gc.fillText("Game is Paused!", WIDTH * TILE / 2.0 - 100, HEIGHT * TILE / 2.0);
                gc.setFont(Font.font(18));
                gc.fillText("Press P to Continue.", WIDTH * TILE / 2.0 - 90, HEIGHT * TILE / 2.0 + 30);
            }
        }

        private void eraseRow() {
            for (int i = HEIGHT - 1; i >= 0; i--) {
                boolean full = true;
                for (int j = 0; j < WIDTH; j++)
                    if (field[i][j] == 0) full = false;
                if (full) {
                    for (int k = i; k > 0; k--)
                        for (int j = 0; j < WIDTH; j++) {
                            field[k][j] = field[k - 1][j];
                            fieldColor[k][j] = fieldColor[k - 1][j];
                        }
                    for (int j = 0; j < WIDTH; j++) {
                        field[0][j] = 0;
                        fieldColor[0][j] = null;
                    }
                    linesCleared++;
                    score += 100;
                    i++; // check same row again after shift
                }
            }
        }

        private void updateTimeline() {
            if (timeline != null) timeline.stop();
            if (!isPaused && !isGameOver) timeline.play();
        }
    }
}
