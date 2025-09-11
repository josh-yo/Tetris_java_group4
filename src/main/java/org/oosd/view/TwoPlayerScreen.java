package org.oosd.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.oosd.ai.AiDriver;
import org.oosd.controller.GameEngine;
import org.oosd.model.Board;
import org.oosd.model.Config;
import org.oosd.model.PlayerType;
import org.oosd.model.PieceFactory;
import org.oosd.model.ScoreEntry;
import org.oosd.service.ConfigObserver;
import org.oosd.service.ConfigService;
import org.oosd.service.Effect;
import org.oosd.service.MusicPlayer;
import org.oosd.service.ScoreService;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import java.util.List;
import java.util.Random;



public class TwoPlayerScreen implements ConfigObserver {

    private final ScreenManager sm;

    private BorderPane root;
    private StackPane  leftPane, rightPane;
    private Rectangle  leftClip, rightClip;
    private Canvas     canvas1, canvas2;
    private GameEngine engine1, engine2;
    private Board      board1,  board2;
    private Label statusLabel;


    private final MusicPlayer bg = new MusicPlayer();

    // Scene-level key filter for Human controls
    private javafx.event.EventHandler<KeyEvent> keyFilter;

    // Per-player state
    private boolean p1Over = false, p2Over = false;
    private boolean p1Prompted = false, p2Prompted = false;

    // Bots (start if that player is AI)
    private AiDriver bot1, bot2;


    public TwoPlayerScreen(ScreenManager sm) { this.sm = sm; }

    private void refreshStatusLabel() {
        if (statusLabel == null) return;
        boolean mus = ConfigService.getInstance().get().isMusicOn();
        boolean sfx = ConfigService.getInstance().get().isSoundOn();
        statusLabel.setText("Music: " + (mus ? "ON" : "OFF") + "    Sound: " + (sfx ? "ON" : "OFF"));
    }
    // ---------------- Screen lifecycle ----------------
    public void show() {
        p1Over = p2Over = p1Prompted = p2Prompted = false;
        bot1 = bot2 = null;

        Config cfg = ConfigService.getInstance().get();

        root = new BorderPane();
        root.setPadding(new Insets(10));

        Button backButton = new Button("Back");
        backButton.setFocusTraversable(false);

        HBox backRow = new HBox(12, backButton);
        backRow.setAlignment(Pos.CENTER_LEFT);
        backRow.setPadding(new Insets(0, 0, 6, 0));

        statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        VBox topBox = new VBox(
                new HBox(){ { setAlignment(Pos.CENTER); getChildren().add(new Label("Play")); } },
                new HBox(){ { setAlignment(Pos.CENTER); getChildren().add(statusLabel); } },
                backRow
        );
        topBox.setSpacing(2);
        root.setTop(topBox);

        refreshStatusLabel(); // initial display


        buildGames(cfg); // build both boards, start engines, maybe start bots

        if (cfg.isMusicOn()) bg.start("/audio/background.mp3", true);

        // Back confirm
        backButton.setOnAction(e -> {
            stopEngines();
            stopBots();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit to Main Menu");
            alert.setHeaderText(null);
            alert.setContentText("Leave the current game?");
            ButtonType no  = new ButtonType("No",  ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(no, yes);
            alert.showAndWait().ifPresent(r -> {
                if (r == yes) {
                    teardown();
                    sm.showMainMenu();
                } else {
                    startEngines();
                }
            });
        });

        sm.getRoot().getChildren().setAll(root);

        // Fit after first layout
        Platform.runLater(this::fitBoth);

        // Refit on window resize or top bar height changes
        sm.getScene().widthProperty().addListener((o, ov, nv) -> fitBoth());
        sm.getScene().heightProperty().addListener((o, ov, nv) -> fitBoth());
        if (root.getTop() != null) {
            root.getTop().layoutBoundsProperty().addListener((o, ov, nv) -> fitBoth());
        }

        // Observe config changes
        ConfigService.getInstance().addObserver(this);

        // Human keyboard mapping (FILTER so arrows/rotate repeat)
        keyFilter = this::handleKeys;
        sm.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyFilter);
    }

    private void teardown() {
        ConfigService.getInstance().removeObserver(this);
        if (keyFilter != null) sm.getScene().removeEventFilter(KeyEvent.KEY_PRESSED, keyFilter);
        stopBots();
        bg.stop();
    }

    // ---------------- Build both boards ----------------
    private void buildGames(Config cfg) {
        // Boards & canvases
        board1  = new Board(cfg.getFieldWidth(), cfg.getFieldHeight());
        board2  = new Board(cfg.getFieldWidth(), cfg.getFieldHeight());

        canvas1 = new Canvas(board1.getWidth() * Board.TILE, board1.getHeight() * Board.TILE);
        canvas2 = new Canvas(board2.getWidth() * Board.TILE, board2.getHeight() * Board.TILE);

        GraphicsContext gc1 = canvas1.getGraphicsContext2D();
        GraphicsContext gc2 = canvas2.getGraphicsContext2D();

        // Same-seed piece factories so both get identical sequences
        long seed = System.nanoTime();
        PieceFactory f1 = new PieceFactory(new Random(seed));
        PieceFactory f2 = new PieceFactory(new Random(seed));

        // Engines
        engine1 = new GameEngine(board1, f1);
        engine2 = new GameEngine(board2, f2);
        engine1.setLevel(cfg.getLevel());
        engine2.setLevel(cfg.getLevel());

        // Start engines
        engine1.start(gc1);
        engine2.start(gc2);

        // Start bots if needed (store refs!)
        bot1 = null; bot2 = null;
        if (cfg.getPlayer1Type() == PlayerType.AI) {
            bot1 = new AiDriver(engine1, gc1);
            bot1.start();
        }
        if (cfg.getPlayer2Type() == PlayerType.AI) {
            bot2 = new AiDriver(engine2, gc2);
            bot2.start();
        }


        // Per-player game over: stop only that player's bot/engine, handle HS, let other continue
        engine1.setOnGameOver(() -> {
            if (!p1Over) {
                if (bot1 != null) bot1.stop();
                p1Over = true;
                engine1.stop();
                promptIfTopScore(1, engine1.getScore());
                maybeFinish();
            }
        });
        engine2.setOnGameOver(() -> {
            if (!p2Over) {
                if (bot2 != null) bot2.stop();
                p2Over = true;
                engine2.stop();
                promptIfTopScore(2, engine2.getScore());
                maybeFinish();
            }
        });

        // Panes + clipping so nothing overflows
        leftPane  = new StackPane(canvas1);
        rightPane = new StackPane(canvas2);
        leftPane.setPadding(new Insets(0));
        rightPane.setPadding(new Insets(0));
        leftPane.setStyle("-fx-background-color: transparent;");
        rightPane.setStyle("-fx-background-color: transparent;");

        leftClip  = new Rectangle(1, 1);
        rightClip = new Rectangle(1, 1);
        leftPane.setClip(leftClip);
        rightPane.setClip(rightClip);

        HBox center = new HBox(8, leftPane, rightPane);
        root.setCenter(center);
    }

    private void startEngines() {
        if (!p1Over) engine1.start(canvas1.getGraphicsContext2D());
        if (!p2Over) engine2.start(canvas2.getGraphicsContext2D());
        if (bot1 != null && !p1Over) bot1.start();
        if (bot2 != null && !p2Over) bot2.start();
    }
    private void stopEngines() {
        engine1.stop();
        engine2.stop();
    }
    private void stopBots() {
        if (bot1 != null) bot1.stop();
        if (bot2 != null) bot2.stop();
    }


    // ---------------- Fit both canvases side-by-side ----------------
    private void fitBoth() {
        if (root == null || leftPane == null || rightPane == null) return;

        double padL = root.getPadding() != null ? root.getPadding().getLeft()   : 0;
        double padR = root.getPadding() != null ? root.getPadding().getRight()  : 0;
        double padT = root.getPadding() != null ? root.getPadding().getTop()    : 0;
        double padB = root.getPadding() != null ? root.getPadding().getBottom() : 0;

        double sceneW = sm.getScene().getWidth();
        double sceneH = sm.getScene().getHeight();

        double topH = 0;
        if (root.getTop() instanceof Region r) topH = r.getHeight();

        double availW = Math.max(1, sceneW - padL - padR);
        double availH = Math.max(1, sceneH - topH - padT - padB);

        double sideSpacing = 8;
        double eachW = Math.max(1, (availW - sideSpacing) / 2.0);

        double logicalW = board1.getWidth()  * Board.TILE;
        double logicalH = board1.getHeight() * Board.TILE;

        double scale = Math.min(eachW / logicalW, availH / logicalH);
        scale = Math.min(1.0, scale);

        canvas1.setScaleX(scale); canvas1.setScaleY(scale);
        canvas2.setScaleX(scale); canvas2.setScaleY(scale);

        leftPane .setMinSize(eachW, availH);
        leftPane .setPrefSize(eachW, availH);
        leftPane .setMaxSize(eachW, availH);

        rightPane.setMinSize(eachW, availH);
        rightPane.setPrefSize(eachW, availH);
        rightPane.setMaxSize(eachW, availH);

        leftClip .setWidth(eachW);  leftClip .setHeight(availH);
        rightClip.setWidth(eachW);  rightClip.setHeight(availH);
    }

    // ---------------- Human key mapping ----------------
    private void handleKeys(KeyEvent e) {
        KeyCode code = e.getCode();

        if (code == KeyCode.M) {
            ConfigService.getInstance().update(c -> c.setMusicOn(!c.isMusicOn()));
            return;
        }
        if (code == KeyCode.S) {
            ConfigService.getInstance().update(c -> c.setSoundOn(!c.isSoundOn()));
            return;
        }

        // PLAYER 1 (left): ,  .  SPACE  L   (ignore if over or AI)
        if (!p1Over && ConfigService.getInstance().get().getPlayer1Type() == PlayerType.HUMAN) {
            switch (code) {
                case COMMA -> { engine1.moveLeft (canvas1.getGraphicsContext2D()); playMove(); }
                case PERIOD-> { engine1.moveRight(canvas1.getGraphicsContext2D()); playMove(); }
                case SPACE ->  { engine1.softDrop (canvas1.getGraphicsContext2D()); }
                case L     ->  { engine1.rotate   (canvas1.getGraphicsContext2D()); playMove(); }
                default -> {}
            }
        }

        // PLAYER 2 (right): arrows  (ignore if over or AI)
        if (!p2Over && ConfigService.getInstance().get().getPlayer2Type() == PlayerType.HUMAN) {
            switch (code) {
                case LEFT  -> { engine2.moveLeft (canvas2.getGraphicsContext2D()); playMove(); }
                case RIGHT -> { engine2.moveRight(canvas2.getGraphicsContext2D()); playMove(); }
                case DOWN  -> { engine2.softDrop (canvas2.getGraphicsContext2D()); }
                case UP    -> { engine2.rotate   (canvas2.getGraphicsContext2D()); playMove(); }
                default -> {}
            }
        }

        // Optional: pause both
        if (code == KeyCode.P) {
            if (!p1Over) engine1.togglePause();
            if (!p2Over) engine2.togglePause();
        }
    }



    private void playMove() {
        if (ConfigService.getInstance().get().isSoundOn()) {
            Effect.MOVE.play();
        }
    }

    // ---------------- Game Over & High Score per player ----------------
    private void promptIfTopScore(int playerIdx, int score) {
        if (!qualifiesTop10(score)) return;

        TextInputDialog d = new TextInputDialog();
        d.setTitle("High Score");
        d.setHeaderText("Player " + playerIdx + " made the Top Scores!\nEnter name:");
        d.setContentText("Name:");
        d.showAndWait().ifPresent(name -> {
            String finalName = (name == null || name.isBlank()) ? "Player " + playerIdx : name.trim();
            Config snapshot = cloneConfig(ConfigService.getInstance().get());
            ScoreService.getInstance().addScore(finalName, score, snapshot);
        });
        if (playerIdx == 1) p1Prompted = true; else p2Prompted = true;
    }

    private boolean qualifiesTop10(int candidateScore) {
        List<ScoreEntry> top = ScoreService.getInstance().topN(10);
        if (top.size() < 10) return true;
        int lastScore = top.get(top.size() - 1).getScore();
        return candidateScore > lastScore;
    }

    private void maybeFinish() {
        if (p1Over && p2Over) {
            teardown();
            sm.showMainMenu();
        }
    }

    // Clone config so later changes don't mutate stored snapshot
    private Config cloneConfig(Config c) {
        Config copy = new Config();
        copy.setFieldWidth(c.getFieldWidth());
        copy.setFieldHeight(c.getFieldHeight());
        copy.setLevel(c.getLevel());
        copy.setMusicOn(c.isMusicOn());
        copy.setSoundOn(c.isSoundOn());
        copy.setAiOn(c.isAiOn());
        copy.setExtendOn(c.isExtendOn());
        copy.setPlayer1Type(c.getPlayer1Type());
        copy.setPlayer2Type(c.getPlayer2Type());
        return copy;
    }

    // ---------------- ConfigObserver ----------------
    @Override
    public void onConfigChanged(Config oldCfg, Config newCfg) {
        boolean sizeChanged = oldCfg.getFieldWidth() != newCfg.getFieldWidth()
                || oldCfg.getFieldHeight() != newCfg.getFieldHeight();
        if (sizeChanged) {
            stopEngines();
            stopBots();
            buildGames(newCfg);
            if (p1Over) engine1.stop();
            if (p2Over) engine2.stop();
            Platform.runLater(this::fitBoth);
        }
        if (oldCfg.getLevel() != newCfg.getLevel()) {
            if (!p1Over) engine1.setLevel(newCfg.getLevel());
            if (!p2Over) engine2.setLevel(newCfg.getLevel());
        }

        if (newCfg.isMusicOn()) {
            if (!bg.isPlaying()) bg.start("/audio/background.mp3", true);
        } else {
            bg.stop();
        }
        refreshStatusLabel();
    }
}
