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
    private AnimationTimer timer;

    private double dx = 3;
    private double dy = 3;

    private final double fieldWidth = 500;
    private final double fieldHeight = 400;

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
        splashImage.setFitWidth(600);   // Set desired width
        splashImage.setFitHeight(500);  // Set desired height
        splashImage.setPreserveRatio(true); // Maintain aspect ratio
        splashImage.setSmooth(true);        // Optional: smooth scaling

        Label groupID = new Label("Group 4");
        Label courseCode = new Label("7010ICT");
        Label loadingLabel = new Label("Loading, please wait...");

        String boldStyle = "-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white;";
        groupID.setStyle(boldStyle);
        courseCode.setStyle(boldStyle);
        loadingLabel.setStyle(boldStyle);

        StackPane.setAlignment(groupID, Pos.TOP_CENTER);
        StackPane.setMargin(groupID, new Insets(20, 0, 0, 0));

        StackPane.setAlignment(courseCode, Pos.CENTER); // default

        StackPane.setAlignment(loadingLabel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(loadingLabel, new Insets(0, 0, 20, 0));


        StackPane splashLayout = new StackPane(splashImage,groupID,courseCode,loadingLabel);
        Scene splashScene = new Scene(splashLayout, 500, 400);  // Adjust size as needed

        splashStage.setScene(splashScene);
        splashStage.show();

        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Simulate some work (e.g., 3 seconds)
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
    private void showMainScreen()
    {
        VBox mainScreen = new VBox(10);
        mainScreen.setPadding(new Insets(20));

        Label label = new Label("Main Screen");
        Button startButton = new Button("Play");
        Button configButton = new Button("Configuration");
        Button scoreButton = new Button("High Scores");
        Button exitButton = new Button("Exit");

        startButton.setOnAction(e -> showGameScreen());
        configButton.setOnAction(e -> showConfigScreen());
        scoreButton.setOnAction(e -> showScoreScreen());
        exitButton.setOnAction(e ->showExitConfirmation());

        mainScreen.getChildren().addAll(label,startButton,configButton,scoreButton,exitButton);
        root.getChildren().setAll(mainScreen);
    }
    private void showScoreScreen()
    {
        VBox scoreScreen = new VBox(20);
        scoreScreen.setPadding(new Insets(30));
        //HIGH SCORE Heading
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
        header.getChildren().addAll(nameHeader,scoreHeader);

        String[][] data = {
                {"Anand", "969313"},
                {"Antony", "755659"},
                {"Yeongjoo", "642871"},
                {"Josh", "540820"},
                {"Siddharth", "537728"}
        };

        VBox scoreList = new VBox(8);
        for(String[] entry: data)
        {
            Label name = new Label(entry[0]);
            name.setMinWidth(200);
            Label score = new Label(entry[1]);
            score.setMinWidth(200);

            HBox row = new HBox(50);
            row.getChildren().addAll(name, score);
            scoreList.getChildren().add(row);
        }

        Button back = new Button("Back");
        back.setOnAction(e->showMainScreen());

        scoreScreen.getChildren().addAll(titleBox,header,scoreList,back);
        root.getChildren().setAll(scoreScreen);

    }
    private void showConfigScreen()
    {
        VBox configScreen = new VBox(10);
        configScreen.setPadding(new Insets(20));

        Label label = new Label("Configuration");

        //Game Width Slider
        Label widthLabel = new Label("Game Width (No of cells): ");
        widthLabel.setMinWidth(150);
        Slider widthSlider = new Slider(5,15,10);
        widthSlider.setPrefWidth(250);
        widthSlider.setShowTickLabels(true);
        widthSlider.setShowTickMarks(true);
        widthSlider.setMajorTickUnit(1);
        widthSlider.setMinorTickCount(0);
        Label currentWidth = new Label();
        currentWidth.setText(String.valueOf((int) widthSlider.getValue()));
        widthSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentWidth.setText(String.valueOf(newVal.intValue()));
        });
        HBox wBox = new HBox(10);
        wBox.getChildren().addAll(widthLabel,widthSlider,currentWidth);  //Horizontal box to include the Game Width elements

        //Game Height Slider
        Label heightLabel = new Label("Game Height (No of cells):");
        heightLabel.setMinWidth(150);
        Slider heightSlider = new Slider(15,30,20);
        heightSlider.setPrefWidth(250);
        heightSlider.setShowTickLabels(true);
        heightSlider.setShowTickMarks(true);
        heightSlider.setMajorTickUnit(1);
        heightSlider.setMinorTickCount(0);
        Label currentHeight = new Label();
        currentHeight.setText(String.valueOf((int) heightSlider.getValue()));
        heightSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentHeight.setText(String.valueOf(newVal.intValue()));
        });
        HBox hBox = new HBox(10); //Horizontal box to include the Game Height elements
        hBox.getChildren().addAll(heightLabel,heightSlider,currentHeight);

        //Game Level Slider
        Label level = new Label("Game Level:");
        level.setMinWidth(150);
        Slider levelSlider = new Slider(1,10,1);
        levelSlider.setPrefWidth(250);
        levelSlider.setShowTickLabels(true);
        levelSlider.setShowTickMarks(true);
        levelSlider.setMajorTickUnit(1);
        levelSlider.setMinorTickCount(0);
        Label currentLevel = new Label();
        currentLevel.setText(String.valueOf((int) levelSlider.getValue()));
        levelSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentLevel.setText(String.valueOf(newVal.intValue()));
        });
        HBox levelBox = new HBox(10);  //Horizontal box to include the Game Level elements
        levelBox.getChildren().addAll(level,levelSlider,currentLevel);

        //Music Checkbox
        Label music = new Label("Music (On/Off):");
        music.setMinWidth(150);
        CheckBox musicBox = new CheckBox();
        musicBox.setPrefWidth(250);
        HBox mBox = new HBox(10);
        Label currentMusicStatus = new Label();
        if (musicBox.isSelected()) {
            currentMusicStatus.setText("On");
        } else {
            currentMusicStatus.setText("Off");
        }
        musicBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                currentMusicStatus.setText("On");
            } else {
                currentMusicStatus.setText("Off");
            }
        });
        mBox.getChildren().addAll(music,musicBox,currentMusicStatus); //Horizontal box to include the Music elements

        //Sound Effect Checkbox
        Label sound = new Label("Sound Effect (On/Off):");
        sound.setMinWidth(150);
        CheckBox soundBox = new CheckBox();
        soundBox.setPrefWidth(250);
        HBox sBox = new HBox(10);
        Label currentSoundStatus = new Label();
        if (soundBox.isSelected()) {
            currentSoundStatus.setText("On");
        } else {
            currentSoundStatus.setText("Off");
        }
        soundBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                currentSoundStatus.setText("On");
            } else {
                currentSoundStatus.setText("Off");
            }
        });
        sBox.getChildren().addAll(sound,soundBox,currentSoundStatus); //Horizontal box to include the Sound elements

        //AI Play Checkbox
        Label ai = new Label("AI Play (On/Off):");
        ai.setMinWidth(150);
        CheckBox aiBox = new CheckBox();
        aiBox.setPrefWidth(250);
        HBox aiPlayBox = new HBox(10);
        Label currentAIStatus = new Label();
        if (aiBox.isSelected()) {
            currentAIStatus.setText("On");
        } else {
            currentAIStatus.setText("Off");
        }
        aiBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                currentAIStatus.setText("On");
            } else {
                currentAIStatus.setText("Off");
            }
        });
        aiPlayBox.getChildren().addAll(ai,aiBox,currentAIStatus); //Horizontal box to include the AI Play elements

        //Extend Mode Checkbox
        Label extend = new Label("Extend Mode (On/Off):");
        extend.setMinWidth(150);
        CheckBox exBox = new CheckBox();
        exBox.setPrefWidth(250);
        HBox exModeBox = new HBox(10);
        Label currentExtendStatus = new Label();
        if (exBox.isSelected()) {
            currentExtendStatus.setText("On");
        } else {
            currentExtendStatus.setText("Off");
        }
        exBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                currentExtendStatus.setText("On");
            } else {
                currentExtendStatus.setText("Off");
            }
        });
        exModeBox.getChildren().addAll(extend,exBox,currentExtendStatus); //Horizontal box to include the extend Mode elements

        Button back = new Button("Back");
        back.setOnAction(e->showMainScreen());

        configScreen.getChildren().addAll(label,wBox,hBox,levelBox,mBox,sBox,aiPlayBox,exModeBox,back); //included all the configuration screen components in the vertical box
        root.getChildren().setAll(configScreen);
    }
    private void showGameScreen()
    {
        Pane gamePane = new Pane();

        Rectangle field = new Rectangle(0,0,fieldWidth,fieldHeight); //This is the area in which the game takes place
        field.setFill(Color.TRANSPARENT);
        field.setStroke(Color.BLACK);

        Circle ball = new Circle(10,Color.RED);
        ball.setCenterX(fieldWidth/2);
        ball.setCenterY(fieldHeight/2);

        Button backButton = new Button("Back");
        backButton.setLayoutX(10);
        backButton.setLayoutY(10);
        backButton.setOnAction(e -> {
            timer.stop();
            showMainScreen();
        });

        gamePane.getChildren().addAll(field,ball,backButton);

        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.UP){
                dx = 0;
                dy = -3;
            } else if(e.getCode() == KeyCode.DOWN){
                dx = 0;
                dy = 3;
            } else if(e.getCode() == KeyCode.LEFT){
                dx = -3;
                dy = 0;
            } else if (e.getCode() == KeyCode.RIGHT) {
                dx = 3;
                dy = 0;
            }
        });

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double nextX = ball.getCenterX() + dx;
                double nextY = ball.getCenterY() + dy;

                if(nextX - ball.getRadius() < 0 || nextX + ball.getRadius() > fieldWidth){
                    dx = -dx;
                }
                if(nextY - ball.getRadius() < 0 || nextY + ball.getRadius() > fieldHeight){
                    dy = -dy;
                }

                ball.setCenterX(ball.getCenterX() + dx);
                ball.setCenterY(ball.getCenterY() + dy);
            }
        };

        timer.start();

        root.getChildren().setAll(gamePane);
        gamePane.requestFocus();

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
            // If No -> do nothing, stay on current screen
        });
    }
    public static void main(String[] args) {
        launch(args);
    }
}