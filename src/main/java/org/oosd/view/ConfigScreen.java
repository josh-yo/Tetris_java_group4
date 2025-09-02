package org.oosd.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ConfigScreen {
    private final ScreenManager sm;
    public ConfigScreen(ScreenManager sm){ this.sm = sm; }

    public void show(){
        VBox configScreen = new VBox(30); configScreen.setPadding(new Insets(20));

        Label label = new Label("Configuration");
        HBox top = new HBox(label); top.setAlignment(javafx.geometry.Pos.CENTER);
        top.setPadding(new Insets(20,0,10,0)); label.setStyle("-fx-font-weight: bold");

        // === Below is the body of Main.showConfigScreen copied as-is ===
        Label widthLabel = new Label("Game Width (No of cells): "); widthLabel.setMinWidth(150); widthLabel.setStyle("-fx-font-weight: bold");
        Slider widthSlider = new Slider(5,15,10); widthSlider.setPrefWidth(250); widthSlider.setShowTickLabels(true); widthSlider.setShowTickMarks(true); widthSlider.setMajorTickUnit(1); widthSlider.setMinorTickCount(0);
        Label currentWidth = new Label(String.valueOf((int) widthSlider.getValue()));
        widthSlider.valueProperty().addListener((obs, o, n) -> currentWidth.setText(String.valueOf(n.intValue())));
        HBox wBox = new HBox(10, widthLabel, widthSlider, currentWidth);

        Label heightLabel = new Label("Game Height (No of cells):"); heightLabel.setMinWidth(150); heightLabel.setStyle("-fx-font-weight: bold");
        Slider heightSlider = new Slider(15,30,20); heightSlider.setPrefWidth(250); heightSlider.setShowTickLabels(true); heightSlider.setShowTickMarks(true); heightSlider.setMajorTickUnit(1); heightSlider.setMinorTickCount(0);
        Label currentHeight = new Label(String.valueOf((int) heightSlider.getValue()));
        heightSlider.valueProperty().addListener((obs, o, n) -> currentHeight.setText(String.valueOf(n.intValue())));
        HBox hBox = new HBox(10, heightLabel, heightSlider, currentHeight);

        Label level = new Label("Game Level:"); level.setMinWidth(150); level.setStyle("-fx-font-weight: bold");
        Slider levelSlider = new Slider(1,10,1); levelSlider.setPrefWidth(250); levelSlider.setShowTickLabels(true); levelSlider.setShowTickMarks(true); levelSlider.setMajorTickUnit(1); levelSlider.setMinorTickCount(0);
        Label currentLevel = new Label(String.valueOf((int) levelSlider.getValue()));
        levelSlider.valueProperty().addListener((obs, o, n) -> currentLevel.setText(String.valueOf(n.intValue())));
        HBox levelBox = new HBox(10, level, levelSlider, currentLevel);

        Label music = new Label("Music (On/Off):"); music.setMinWidth(150); music.setStyle("-fx-font-weight: bold");
        CheckBox musicBox = new CheckBox(); musicBox.setPrefWidth(250);
        Label currentMusicStatus = new Label(musicBox.isSelected()? "On":"Off");
        musicBox.selectedProperty().addListener((obs, ov, nv) -> currentMusicStatus.setText(nv? "On":"Off"));
        HBox mBox = new HBox(10, music, musicBox, currentMusicStatus);

        Label sound = new Label("Sound Effect (On/Off):"); sound.setMinWidth(150); sound.setStyle("-fx-font-weight: bold");
        CheckBox soundBox = new CheckBox(); soundBox.setPrefWidth(250);
        Label currentSoundStatus = new Label(soundBox.isSelected()? "On":"Off");
        soundBox.selectedProperty().addListener((obs, ov, nv) -> currentSoundStatus.setText(nv? "On":"Off"));
        HBox sBox = new HBox(10, sound, soundBox, currentSoundStatus);

        Label ai = new Label("AI Play (On/Off):"); ai.setMinWidth(150); ai.setStyle("-fx-font-weight: bold");
        CheckBox aiBox = new CheckBox(); aiBox.setPrefWidth(250);
        Label currentAIStatus = new Label(aiBox.isSelected()? "On":"Off");
        aiBox.selectedProperty().addListener((obs, ov, nv) -> currentAIStatus.setText(nv? "On":"Off"));
        HBox aiPlayBox = new HBox(10, ai, aiBox, currentAIStatus);

        Label extend = new Label("Extend Mode (On/Off):"); extend.setMinWidth(150); extend.setStyle("-fx-font-weight: bold");
        CheckBox exBox = new CheckBox(); exBox.setPrefWidth(250);
        Label currentExtendStatus = new Label(exBox.isSelected()? "On":"Off");
        exBox.selectedProperty().addListener((obs, ov, nv) -> currentExtendStatus.setText(nv? "On":"Off"));
        HBox exModeBox = new HBox(10, extend, exBox, currentExtendStatus);

        Button back = new Button("Back"); back.setOnAction(e -> sm.showMainMenu());

        configScreen.getChildren().addAll(top, wBox, hBox, levelBox, mBox, sBox, aiPlayBox, exModeBox, back);
        sm.getRoot().getChildren().setAll(configScreen);
    }
}
