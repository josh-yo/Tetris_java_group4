package org.oosd;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Please input your name:");
        TextField input = new TextField();
        Button btn = new Button("Click Me");
        StackPane root = new StackPane();
        root.getChildren().addAll(label, input, btn);
        btn.setOnAction(e -> {
//            int x = 10;
//            int y = x + 5;
//            System.out.println(x + y);
            label.setText("Hello, " + input.getText() + "!");
        });
        StackPane.setAlignment(label, Pos.TOP_CENTER);
        StackPane.setAlignment(input, Pos.CENTER);
        StackPane.setAlignment(btn, Pos.BOTTOM_CENTER);
        Scene scene = new Scene(root, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Demo");
        primaryStage.show();
    }
}