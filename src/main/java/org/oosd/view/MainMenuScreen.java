package org.oosd.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MainMenuScreen {
    private final ScreenManager sm;
    public MainMenuScreen(ScreenManager sm){ this.sm = sm; }

    public void show(){
        VBox mainScreen = new VBox(30);
        mainScreen.setPadding(new Insets(20));

        Label label = new Label("Main Screen");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        HBox top = new HBox(label); top.setAlignment(Pos.CENTER);
        top.setPadding(new Insets(20, 0, 10, 0));

        Button startButton = new Button("Play");
        Button configButton = new Button("Configuration");
        Button scoreButton = new Button("High Scores");
        Button exitButton = new Button("Exit");

        double BTN_WIDTH = 220, BTN_HEIGHT = 36;
        for (Button b : new Button[]{startButton, configButton, scoreButton, exitButton}) {
            b.setPrefWidth(BTN_WIDTH); b.setMinHeight(BTN_HEIGHT);
        }

        startButton.setOnAction(e -> sm.showGame());
        configButton.setOnAction(e -> sm.showConfig());
        scoreButton.setOnAction(e -> sm.showScores());
        exitButton.setOnAction(e -> sm.showExitConfirmation());

        VBox buttonsCol = new VBox(14, startButton, configButton, scoreButton, exitButton);
        buttonsCol.setAlignment(Pos.CENTER);

        BorderPane layout = new BorderPane();
        layout.setTop(top);
        layout.setCenter(buttonsCol);
        layout.setPadding(new Insets(10));

        sm.getRoot().getChildren().setAll(layout);
    }
}
