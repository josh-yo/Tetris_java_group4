package org.oosd.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class HighScoresScreen {
    private final ScreenManager sm;
    public HighScoresScreen(ScreenManager sm){ this.sm = sm; }

    public void show() {
        VBox scoreScreen = new VBox(20);
        scoreScreen.setPadding(new Insets(30));

        Label title = new Label("High Scores");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        HBox titleBox = new HBox(title); titleBox.setAlignment(javafx.geometry.Pos.TOP_CENTER);

        Label nameHeader = new Label("Name"); nameHeader.setMinWidth(200); nameHeader.setStyle("-fx-font-weight: bold");
        Label scoreHeader = new Label("Score"); scoreHeader.setMinWidth(200); scoreHeader.setStyle("-fx-font-weight: bold");
        HBox header = new HBox(50, nameHeader, scoreHeader);

        String[][] data = {
                {"Anand", "969313"}, {"Antony", "755659"}, {"Yeongjoo", "642871"},
                {"Josh", "540820"}, {"Siddharth", "537728"}, {"Bob", "531328"},
                {"Alice", "499000"}, {"Max", "485078"}, {"Tom", "460078"}, {"Pearl", "345678"}
        };

        VBox scoreList = new VBox(8);
        for (String[] entry : data) {
            Label name = new Label(entry[0]); name.setMinWidth(200);
            Label score = new Label(entry[1]); score.setMinWidth(200);
            scoreList.getChildren().add(new HBox(50, name, score));
        }

        Button back = new Button("Back"); back.setOnAction(e -> sm.showMainMenu());
        scoreScreen.getChildren().addAll(titleBox, header, scoreList, back);
        sm.getRoot().getChildren().setAll(scoreScreen);
    }
}
