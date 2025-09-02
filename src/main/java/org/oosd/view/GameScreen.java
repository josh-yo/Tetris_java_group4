package org.oosd.view;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.oosd.controller.GameEngine;
import org.oosd.controller.InputController;
import org.oosd.model.Board;

public class GameScreen {
    private final ScreenManager sm;

    public GameScreen(ScreenManager sm) { this.sm = sm; }

    public void show() {
        BorderPane gamePane = new BorderPane();
        gamePane.setPadding(new Insets(10));

        Button backButton = new Button("Back");
        backButton.setFocusTraversable(false);
        HBox topBar = new HBox(backButton);
        gamePane.setTop(topBar);

        // Model & Canvas
        Board board = new Board(10, 20);
        Canvas canvas = new Canvas(board.getWidth() * Board.TILE, board.getHeight() * Board.TILE);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Controller
        GameEngine engine = new GameEngine(board);
        InputController input = new InputController(engine);

        // Start game loop
        engine.start(gc);

        // Keyboard input
        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(e -> input.handle(e.getCode(), gc));

        // Back button
        backButton.setOnAction(e -> {
            engine.stop();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit to Main Menu");
            alert.setHeaderText(null);
            alert.setContentText("Leave the current game?");
            ButtonType no  = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(no, yes);
            alert.showAndWait().ifPresent(r -> {
                if (r == yes) {
                    sm.showMainMenu();
                } else {
                    engine.start(gc);
                    canvas.requestFocus();
                }
            });
        });

        gamePane.setCenter(canvas);
        sm.getRoot().getChildren().setAll(gamePane);
        canvas.requestFocus();
    }
}
