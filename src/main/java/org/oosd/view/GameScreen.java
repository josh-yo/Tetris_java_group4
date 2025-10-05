package org.oosd.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.oosd.controller.GameEngine;
import org.oosd.controller.InputController;
import org.oosd.model.Board;
import org.oosd.model.Config;
import org.oosd.model.ScoreEntry;
import org.oosd.service.ConfigObserver;
import org.oosd.service.ConfigService;
import org.oosd.service.MusicPlayer;
import org.oosd.service.ScoreService;
import org.oosd.ai.AiDriver;
import org.oosd.model.PlayerType;

import java.util.List;

public class GameScreen implements ConfigObserver {
    private final ScreenManager sm;

    private BorderPane gamePane;
    private StackPane  playfield;       // wraps the canvas
    private Rectangle  clipRect;        // clips any overflow
    private Canvas     canvas;
    private GameEngine engine;
    private Board      board;
    private Label statusLabel;
    // stats sidebar labels
    private Label lblPlayerType;
    private Label lblInitialLevel;
    private Label lblCurrentLevel;
    private Label lblLines;
    private Label lblScore;
    private Canvas nextPreview;

    // with other fields
    private org.oosd.ai.AiDriver bot;

    private final MusicPlayer bg = new MusicPlayer();

    // prevent double dialog / double save
    private boolean gameOverHandled = false;

    public GameScreen(ScreenManager sm) { this.sm = sm; }

    @Override
    public void onConfigChanged(Config oldCfg, Config newCfg) {
        boolean sizeChanged = oldCfg.getFieldWidth() != newCfg.getFieldWidth()
                || oldCfg.getFieldHeight() != newCfg.getFieldHeight();
        if (sizeChanged) {
            engine.stop();
            if (bot != null) bot.stop();
            buildGame(newCfg);
            canvas.requestFocus();
        }

        if (oldCfg.getLevel() != newCfg.getLevel()) {
            engine.setLevel(newCfg.getLevel());
        }

        if (newCfg.isMusicOn()) {
            if (!bg.isPlaying()) bg.start("/audio/background.mp3", true);
        } else {
            bg.stop();
        }
        refreshStatusLabel();
    }
    private void refreshStatusLabel() {
        if (statusLabel == null) return;
        boolean mus = ConfigService.getInstance().get().isMusicOn();
        boolean sfx = ConfigService.getInstance().get().isSoundOn();
        statusLabel.setText("Music: " + (mus ? "ON" : "OFF") + "    Sound: " + (sfx ? "ON" : "OFF"));
    }

    public void show() {
        gameOverHandled = false; // new game, reset flag

        Config cfg = ConfigService.getInstance().get();

        gamePane = new BorderPane();
        gamePane.setPadding(new Insets(10));

        // ---- Top bar (always visible) ----
        Button backButton = new Button("Back");
        backButton.setFocusTraversable(false);
        HBox topBar = new HBox(backButton);
        topBar.setAlignment(Pos.CENTER_LEFT);

        statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        VBox topBox = new VBox(
                new HBox(){ { setAlignment(Pos.CENTER); getChildren().add(new Label("Play")); } },
                new HBox(){ { setAlignment(Pos.CENTER); getChildren().add(statusLabel); } },
                topBar
        );
        topBox.setSpacing(2);
        gamePane.setTop(topBox);
        refreshStatusLabel();

        // Build the game area
        buildGame(cfg);

        // Background music
        if (cfg.isMusicOn()) bg.start("/audio/background.mp3", true);

        // Back confirmation
        backButton.setOnAction(e -> {
            engine.stop();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit to Main Menu");
            alert.setHeaderText(null);
            alert.setContentText("Leave the current game?");
            ButtonType no  = new ButtonType("No",  ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(no, yes);
            alert.showAndWait().ifPresent(r -> {
                if (r == yes) {
                    bg.stop();
                    if (bot != null) bot.stop();
                    ConfigService.getInstance().removeObserver(this);
                    sm.showMainMenu();
                } else {
                    engine.start(canvas.getGraphicsContext2D());
                    canvas.requestFocus();
                }
            });
        });

        sm.getRoot().getChildren().setAll(gamePane);

        // Ensure first fit after layout
        Platform.runLater(this::fitCanvas);

        // React to window size changes
        sm.getScene().widthProperty().addListener((o, ov, nv) -> fitCanvas());
        sm.getScene().heightProperty().addListener((o, ov, nv) -> fitCanvas());

        // React when the top bar height changes
        if (gamePane.getTop() != null) {
            gamePane.getTop().layoutBoundsProperty().addListener((o, ov, nv) -> fitCanvas());
        }

        ConfigService.getInstance().addObserver(this);

        // === GAME OVER HANDLER ===
        engine.setOnGameOver(() -> {
            if (gameOverHandled) return;
            gameOverHandled = true;

            bg.stop();
            Platform.runLater(this::handleGameOverFlow);
        });
    }

    // ---------- Build/rebuild board & canvas ----------
    private void buildGame(Config cfg) {
        board  = new Board(cfg.getFieldWidth(), cfg.getFieldHeight());
        canvas = new Canvas(board.getWidth() * Board.TILE, board.getHeight() * Board.TILE);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        engine = new GameEngine(board);
        engine.setLevel(cfg.getLevel());

        if (cfg.getPlayer1Type() == PlayerType.AI) {
            bot = new AiDriver(engine, gc);
            bot.start();
            engine.setOnGameOver(() -> { bg.stop(); bot.stop(); });
        } else {
            engine.setOnGameOver(() -> bg.stop());
        }

        InputController input = new InputController(engine);

        engine.start(gc);
        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.M) {
                ConfigService.getInstance().update(c -> c.setMusicOn(!c.isMusicOn()));
                return;
            }
            if (code == KeyCode.S) {
                ConfigService.getInstance().update(c -> c.setSoundOn(!c.isSoundOn()));
                return;
            }
            input.handle(code, gc);
        });

        // Wrap the canvas so scaling does not push layout
        playfield = new StackPane(canvas);
        playfield.setPadding(new Insets(0));
        playfield.setStyle("-fx-background-color: #000000; -fx-border-color: #708993; -fx-border-width: 2;");

        clipRect = new Rectangle(1, 1);
        playfield.setClip(clipRect);

        // Sidebar
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(12));
        sidebar.setStyle("-fx-border-color: #708993; -fx-border-width: 2; -fx-background-color: white;");

        Label title = new Label("Game Info (Player 1)");
        title.setStyle("-fx-font-weight: bold;");

        lblPlayerType  = new Label("Player Type: " + cfg.getPlayer1Type().name());
        lblInitialLevel= new Label("Initial Level: " + cfg.getLevel());
        lblCurrentLevel= new Label("Current Level: " + ConfigService.getInstance().get().getLevel());
        lblLines       = new Label("Line Erased: 0");
        lblScore       = new Label("Score: 0");
        lblScore.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox info = new VBox(6, title,
                new Label(""),
                lblPlayerType,
                new Label(""),
                lblInitialLevel,
                lblCurrentLevel,
                lblLines,
                new Label(""),
                lblScore,
                new Label(""),
                new Label("Next Tetromino:")
        );

        // Next tetromino preview
        nextPreview = new Canvas(90, 70);
        StackPane nextPane = new StackPane(nextPreview);
        nextPane.setStyle("-fx-border-color: #708993; -fx-border-width: 2; -fx-background-color: #f7f7f7;");
        nextPane.setPadding(new Insets(6));

        sidebar.getChildren().addAll(info, nextPane);

        // Divider
        Region divider = new Region();
        divider.setPrefWidth(6);
        divider.setMinWidth(6);
        divider.setMaxWidth(6);
        divider.setStyle("-fx-background-color: #c0c0c0;");
        divider.setMaxHeight(Double.MAX_VALUE);

        // Main area
        HBox mainArea;

// If player 2 exists and is not HUMAN, treat as 2-player mode
        if (cfg.getPlayer2Type() != null && cfg.getPlayer2Type() != PlayerType.HUMAN) {
            // 2-player mode → normal layout
            mainArea = new HBox(10, sidebar, divider, playfield, new Label("P2 board TBD"));
            mainArea.setAlignment(Pos.CENTER_LEFT);
        } else {
            // 1-player mode → CENTER sidebar + playfield
            HBox inner = new HBox(10, sidebar, divider, playfield);
            inner.setAlignment(Pos.CENTER);
            inner.setMaxWidth(Region.USE_PREF_SIZE);

            StackPane centered = new StackPane(inner);
            centered.setAlignment(Pos.CENTER);

            mainArea = new HBox(centered);
            mainArea.setAlignment(Pos.CENTER);
        }



        VBox framed = new VBox(mainArea);
        framed.setPadding(new Insets(20));
        framed.setStyle("-fx-border-color: #708993; -fx-border-width: 2; -fx-background-color: transparent;");

        StackPane centerWrap = new StackPane(framed);
        centerWrap.setPadding(new Insets(16));
        StackPane.setAlignment(framed, Pos.CENTER);
        gamePane.setCenter(centerWrap);

        // periodic UI refresh
        javafx.animation.Timeline stats = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(200), e -> {
                    lblLines.setText("Line Erased: " + engine.getLinesCleared());
                    lblScore.setText("Score: " + engine.getScore());
                    lblCurrentLevel.setText("Current Level: " + ConfigService.getInstance().get().getLevel());
                    drawNextPreview();
                })
        );
        stats.setCycleCount(javafx.animation.Animation.INDEFINITE);
        stats.play();
    }

    private void drawNextPreview() {
        if (nextPreview == null) return;
        var gc = nextPreview.getGraphicsContext2D();
        gc.clearRect(0,0,nextPreview.getWidth(), nextPreview.getHeight());
        int[][] s = engine.snapshotNextShape();
        if (s == null) return;
        int rows = s.length;
        int cols = s[0].length;
        double pad = 6;
        double cell = Math.min((nextPreview.getWidth() - 2*pad) / cols,
                (nextPreview.getHeight()- 2*pad) / rows);
        double totalW = cols * cell;
        double totalH = rows * cell;
        double ox = (nextPreview.getWidth()  - totalW) / 2.0;
        double oy = (nextPreview.getHeight() - totalH) / 2.0;
        gc.setFill(engine.nextColor());
        for (int i=0;i<rows;i++) {
            for (int j=0;j<cols;j++) {
                if (s[i][j]==1) {
                    gc.fillRect(ox + j*cell, oy + i*cell, cell-1, cell-1);
                }
            }
        }
    }

    private void fitCanvas() {
        if (playfield == null || canvas == null || gamePane == null || board == null) return;

        double logicalW = board.getWidth()  * Board.TILE;
        double logicalH = board.getHeight() * Board.TILE;

        double padL = gamePane.getPadding() != null ? gamePane.getPadding().getLeft()   : 0;
        double padR = gamePane.getPadding() != null ? gamePane.getPadding().getRight()  : 0;
        double padT = gamePane.getPadding() != null ? gamePane.getPadding().getTop()    : 0;
        double padB = gamePane.getPadding() != null ? gamePane.getPadding().getBottom() : 0;

        double sceneW = sm.getScene().getWidth();
        double sceneH = sm.getScene().getHeight();

        double topH = 0;
        if (gamePane.getTop() instanceof Region r) topH = r.getHeight();

        double availW = Math.max(1, sceneW - padL - padR);
        double availH = Math.max(1, sceneH - topH - padT - padB);

        double scale = Math.min(availW / (logicalW), availH / (logicalH));
        scale = Math.min(1.0, scale);

        canvas.setScaleX(scale);
        canvas.setScaleY(scale);

        double scaledW = logicalW * scale;
        double scaledH = logicalH * scale;
        playfield.setMinSize(scaledW, scaledH);
        playfield.setPrefSize(scaledW, scaledH);
        playfield.setMaxSize(scaledW, scaledH);

        clipRect.setWidth(scaledW);
        clipRect.setHeight(scaledH);

        canvas.requestFocus();
    }

    private void handleGameOverFlow() {
        int score = engine.getScore();

        TextInputDialog d = new TextInputDialog();
        d.setTitle("High Score");
        d.setHeaderText("Game Over! Your score: " + score + "\nEnter your name to continue:");
        d.setContentText("Name:");
        d.showAndWait().ifPresentOrElse(name -> {
            String finalName = (name == null || name.isBlank()) ? "Player" : name.trim();
            if (qualifiesTop10(score)) {
                Config snapshot = cloneConfig(ConfigService.getInstance().get());
                ScoreService.getInstance().addScore(finalName, score, snapshot);
            }
            exitToMainMenu();
        }, this::exitToMainMenu);
    }

    private void exitToMainMenu() {
        ConfigService.getInstance().removeObserver(this);
        sm.showMainMenu();
    }

    private boolean qualifiesTop10(int candidateScore) {
        List<ScoreEntry> top = ScoreService.getInstance().topN(10);
        if (top.size() < 10) return true;
        int lastScore = top.get(top.size() - 1).getScore();
        return candidateScore > lastScore;
    }

    private Config cloneConfig(Config c) {
        Config copy = new Config();
        copy.setFieldWidth(c.getFieldWidth());
        copy.setFieldHeight(c.getFieldHeight());
        copy.setLevel(c.getLevel());
        copy.setMusicOn(c.isMusicOn());
        copy.setSoundOn(c.isSoundOn());
        copy.setAiOn(c.isAiOn());
        copy.setExtendOn(c.isExtendOn());
        return copy;
    }
}

