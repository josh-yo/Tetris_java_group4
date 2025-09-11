package org.oosd.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.oosd.service.ConfigService;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        ConfigService.getInstance().load();
        // ScreenManager sets up the Stage and initial Scene.
        ScreenManager sm = new ScreenManager(primaryStage);

        // Set a default title on the primary stage first.
        primaryStage.setTitle("JavaFX Multi-Screen Game");
        primaryStage.show(); // Keep the main Stage visible, separate from the splash.

        // ---------- Splash Screen ----------
        Stage splashStage = new Stage(StageStyle.UNDECORATED);

        // Load resources (with a safe null check).
        URL url = Main.class.getResource("/tetris.png");
        ImageView splashImage = new ImageView();
        if (url != null) {
            splashImage.setImage(new Image(url.toExternalForm(), true));
            splashImage.setFitWidth(600);
            splashImage.setFitHeight(500);
            splashImage.setPreserveRatio(true);
            splashImage.setSmooth(true);
        } else {
            System.err.println("[WARN] Could not find /tetris.png on classpath");
        }

        Label courseCode = new Label("Group 4 - 7010ICT");
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

        // Loading task (close splash after 3 seconds and show the main menu).
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
                    sm.showMainMenu(); // Display the main menu screen.
                });
            }
        };
        new Thread(loadTask).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
