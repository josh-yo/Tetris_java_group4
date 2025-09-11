package org.oosd.view;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.oosd.service.ConfigService;

public class ScreenManager {
    private final Stage stage;
    private final StackPane root = new StackPane();
    // Keep a comfortable fixed scene size; our screens scale canvases to fit.
    private final Scene scene = new Scene(root, 900, 650);

    public ScreenManager(Stage stage) {
        this.stage = stage;
        this.stage.setScene(scene);
        this.stage.setResizable(true);
    }

    public void showMainMenu() { new MainMenuScreen(this).show(); }
    public void showConfig()    { new ConfigScreen(this).show(); }
    public void showScores()    { new HighScoresScreen(this).show(); }

    public void showGame() {
        if (ConfigService.getInstance().get().isExtendOn()) {
            new TwoPlayerScreen(this).show();   // NEW: 2-player screen
        } else {
            new GameScreen(this).show();        // existing 1-player screen
        }
    }

    public void showExitConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to exit?");
        ButtonType no  = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(no, yes);
        alert.showAndWait().ifPresent(r -> { if (r == yes) javafx.application.Platform.exit(); });
    }

    public StackPane getRoot() { return root; }
    public Stage getStage()    { return stage; }
    public Scene getScene()    { return scene; }
}
