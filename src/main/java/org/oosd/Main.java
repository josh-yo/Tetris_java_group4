package org.oosd;

import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.concurrent.Task;

import javafx.geometry.Pos;

public class Main extends Application {

    private StackPane root;
    private Scene scene;
    private AnimationTimer timer; // (unused now, kept to minimize changes)

    private double dx = 3; // (unused now)
    private double dy = 3; // (unused now)

    private final double fieldWidth = 500;
    private final double fieldHeight = 650;

    @Override
    public void start(Stage primaryStage) {
        root = new StackPane();
        scene = new Scene(root, fieldWidth, fieldHeight);

        showMainScreen();

        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Multi-Screen Game");
        primaryStage.show();

        Stage splashStage = new Stage(StageStyle.UNDECORATED);

        ImageView splashImage = new ImageView(new Image(getClass().getResource("/tetris.png").toExternalForm()));
        splashImage.setFitWidth(600);
        splashImage.setFitHeight(500);
        splashImage.setPreserveRatio(true);
        splashImage.setSmooth(true);

        Label courseCode = new Label("Group 4- 7010ICT");
        Label loadingLabel = new Label("Loading, please wait...");

        String boldStyle = "-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: red;";
        courseCode.setStyle(boldStyle);
        loadingLabel.setStyle(boldStyle);

        StackPane.setAlignment(courseCode, Pos.CENTER);
        StackPane.setAlignment(loadingLabel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(loadingLabel, new Insets(0, 0, 20, 0));

        StackPane splashLayout = new StackPane(splashImage, courseCode, loadingLabel);
        Scene splashScene = new Scene(splashLayout, 500, 400);

        splashStage.setScene(splashScene);
        splashStage.show();

        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(3000);
                return null;
            }
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    splashStage.close();
                    showMainScreen();
                });
            }
        };
        new Thread(loadTask).start();
    }

    private void showMainScreen() {
        VBox mainScreen = new VBox(10);
        mainScreen.setPadding(new Insets(20));

        Label label = new Label("Main Screen");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        HBox top = new HBox(label);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(20, 0, 10, 0));

        Button startButton = new Button("Play");
        Button configButton = new Button("Configuration");
        Button scoreButton = new Button("High Scores");
        Button exitButton = new Button("Exit");

        double BTN_WIDTH = 220;
        double BTN_HEIGHT = 36;
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

        HBox header = new HBox(50);
        header.getChildren().addAll(nameHeader, scoreHeader);

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

            HBox row = new HBox(50);
            row.getChildren().addAll(name, score);
            scoreList.getChildren().add(row);
        }

        Button back = new Button("Back");
        back.setOnAction(e -> showMainScreen());

        scoreScreen.getChildren().addAll(titleBox, header, scoreList, back);
        root.getChildren().setAll(scoreScreen);
    }

    private void showConfigScreen() {
        VBox configScreen = new VBox(10);
        configScreen.setPadding(new Insets(20));

        Label label = new Label("Configuration");
        HBox top = new HBox(label);
        top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(20, 0, 10, 0));
        label.setStyle("-fx-font-weight: bold");

        // Width
        Label widthLabel = new Label("Game Width (No of cells): ");
        widthLabel.setMinWidth(150);
        widthLabel.setStyle("-fx-font-weight: bold");
        Slider widthSlider = new Slider(5, 15, 10);
        widthSlider.setPrefWidth(250);
        widthSlider.setShowTickLabels(true);
        widthSlider.setShowTickMarks(true);
        widthSlider.setMajorTickUnit(1);
        widthSlider.setMinorTickCount(0);
        Label currentWidth = new Label(String.valueOf((int) widthSlider.getValue()));
        widthSlider.valueProperty().addListener((obs, oldVal, newVal) -> currentWidth.setText(String.valueOf(newVal.intValue())));
        HBox wBox = new HBox(10, widthLabel, widthSlider, currentWidth);

        // Height
        Label heightLabel = new Label("Game Height (No of cells):");
        heightLabel.setMinWidth(150);
        heightLabel.setStyle("-fx-font-weight: bold");
        Slider heightSlider = new Slider(15, 30, 20);
        heightSlider.setPrefWidth(250);
        heightSlider.setShowTickLabels(true);
        heightSlider.setShowTickMarks(true);
        heightSlider.setMajorTickUnit(1);
        heightSlider.setMinorTickCount(0);
        Label currentHeight = new Label(String.valueOf((int) heightSlider.getValue()));
        heightSlider.valueProperty().addListener((obs, oldVal, newVal) -> currentHeight.setText(String.valueOf(newVal.intValue())));
        HBox hBox = new HBox(10, heightLabel, heightSlider, currentHeight);

        // Level
        Label level = new Label("Game Level:");
        level.setMinWidth(150);
        level.setStyle("-fx-font-weight: bold");
        Slider levelSlider = new Slider(1, 10, 1);
        levelSlider.setPrefWidth(250);
        levelSlider.setShowTickLabels(true);
        levelSlider.setShowTickMarks(true);
        levelSlider.setMajorTickUnit(1);
        levelSlider.setMinorTickCount(0);
        Label currentLevel = new Label(String.valueOf((int) levelSlider.getValue()));
        levelSlider.valueProperty().addListener((obs, oldVal, newVal) -> currentLevel.setText(String.valueOf(newVal.intValue())));
        HBox levelBox = new HBox(10, level, levelSlider, currentLevel);

        // Music
        Label music = new Label("Music (On/Off):");
        music.setMinWidth(150);
        music.setStyle("-fx-font-weight: bold");
        CheckBox musicBox = new CheckBox();
        musicBox.setPrefWidth(250);
        Label currentMusicStatus = new Label(musicBox.isSelected() ? "On" : "Off");
        musicBox.selectedProperty().addListener((obs, ov, nv) -> currentMusicStatus.setText(nv ? "On" : "Off"));
        HBox mBox = new HBox(10, music, musicBox, currentMusicStatus);

        // Sound
        Label sound = new Label("Sound Effect (On/Off):");
        sound.setMinWidth(150);
        sound.setStyle("-fx-font-weight: bold");
        CheckBox soundBox = new CheckBox();
        soundBox.setPrefWidth(250);
        Label currentSoundStatus = new Label(soundBox.isSelected() ? "On" : "Off");
        soundBox.selectedProperty().addListener((obs, ov, nv) -> currentSoundStatus.setText(nv ? "On" : "Off"));
        HBox sBox = new HBox(10, sound, soundBox, currentSoundStatus);

        // AI
        Label ai = new Label("AI Play (On/Off):");
        ai.setMinWidth(150);
        ai.setStyle("-fx-font-weight: bold");
        CheckBox aiBox = new CheckBox();
        aiBox.setPrefWidth(250);
        Label currentAIStatus = new Label(aiBox.isSelected() ? "On" : "Off");
        aiBox.selectedProperty().addListener((obs, ov, nv) -> currentAIStatus.setText(nv ? "On" : "Off"));
        HBox aiPlayBox = new HBox(10, ai, aiBox, currentAIStatus);

        // Extend
        Label extend = new Label("Extend Mode (On/Off):");
        extend.setMinWidth(150);
        extend.setStyle("-fx-font-weight: bold");
        CheckBox exBox = new CheckBox();
        exBox.setPrefWidth(250);
        Label currentExtendStatus = new Label(exBox.isSelected() ? "On" : "Off");
        exBox.selectedProperty().addListener((obs, ov, nv) -> currentExtendStatus.setText(nv ? "On" : "Off"));
        HBox exModeBox = new HBox(10, extend, exBox, currentExtendStatus);

        Button back = new Button("Back");
        back.setOnAction(e -> showMainScreen());

        configScreen.getChildren().addAll(top, wBox, hBox, levelBox, mBox, sBox, aiPlayBox, exModeBox, back);
        root.getChildren().setAll(configScreen);
    }

    private void showGameScreen() {
        BorderPane gamePane = new BorderPane();
        gamePane.setPadding(new Insets(10));

        Button backButton = new Button("Back");
        backButton.setFocusTraversable(false); // prevents arrow keys focusing this button
        HBox topBar = new HBox(backButton);
        topBar.setSpacing(10);
        gamePane.setTop(topBar);

        StackPane center = new StackPane();
        center.setStyle("-fx-background-color: #f4f4f4;");
        center.setPadding(new Insets(10));

        // Embed backend Tetris
        Tetris game = new Tetris();
        Pane gameNode = game.createEmbedded(); // get the Pane from backend
        center.getChildren().add(gameNode);
        //StackPane.setAlignment(gameNode, Pos.CENTER);
        game.startEmbedded();

        // Back button: pauses game, asks confirmation, exits or resumes
        backButton.setOnAction(e -> {
            game.toggleGame();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit to Main Menu");
            alert.setHeaderText(null);
            alert.setContentText("Leave the current game?");
            ButtonType no  = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(no, yes);
            alert.showAndWait().ifPresent(r -> {
                if (r == yes) {
                    game.stopEmbedded(); // stop timeline
                    showMainScreen();
                }
                else {
                    game.toggleGame();
                }
            });
        });

        gamePane.setCenter(center);
        root.getChildren().setAll(gamePane);

        // Ensure keyboard focus is on the game
        gameNode.requestFocus();
    }

    private void showExitConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to exit?");

        ButtonType no  = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(no, yes);

        alert.showAndWait().ifPresent(result -> {
            if (result == yes) {
                Platform.exit();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}