package org.oosd.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.oosd.service.ConfigService;

import org.oosd.model.PlayerType;


public class ConfigScreen {
    private final ScreenManager sm;
    public ConfigScreen(ScreenManager sm){ this.sm = sm; }

    public void show(){
        VBox configScreen = new VBox(30); configScreen.setPadding(new Insets(20));

        Label label = new Label("Configuration");
        HBox top = new HBox(label); top.setAlignment(javafx.geometry.Pos.CENTER);
        top.setPadding(new Insets(20,0,10,0)); label.setStyle("-fx-font-weight: bold");

        // Width
        Label widthLabel = new Label("Game Width (No of cells): "); widthLabel.setMinWidth(180); widthLabel.setStyle("-fx-font-weight: bold");
        Slider widthSlider = new Slider(5,15, ConfigService.getInstance().get().getFieldWidth());
        widthSlider.setShowTickLabels(true); widthSlider.setShowTickMarks(true);
        widthSlider.setMajorTickUnit(1); widthSlider.setMinorTickCount(0); widthSlider.setSnapToTicks(true);
        Label currentWidth = new Label(Integer.toString((int)widthSlider.getValue()));
        widthSlider.valueProperty().addListener((obs, o, n) -> {
            currentWidth.setText(String.valueOf(n.intValue()));
            ConfigService.getInstance().update(c -> c.setFieldWidth(n.intValue()));
        });
        HBox wBox = new HBox(10, widthLabel, widthSlider, currentWidth);

        // Height
        Label heightLabel = new Label("Game Height (No of cells):"); heightLabel.setMinWidth(180); heightLabel.setStyle("-fx-font-weight: bold");
        Slider heightSlider = new Slider(15,30, ConfigService.getInstance().get().getFieldHeight());
        heightSlider.setShowTickLabels(true); heightSlider.setShowTickMarks(true);
        heightSlider.setMajorTickUnit(1); heightSlider.setMinorTickCount(0); heightSlider.setSnapToTicks(true);
        Label currentHeight = new Label(Integer.toString((int)heightSlider.getValue()));
        heightSlider.valueProperty().addListener((obs, o, n) -> {
            currentHeight.setText(String.valueOf(n.intValue()));
            ConfigService.getInstance().update(c -> c.setFieldHeight(n.intValue()));
        });
        HBox hBox = new HBox(10, heightLabel, heightSlider, currentHeight);

        // Level
        Label level = new Label("Game Level:"); level.setMinWidth(180); level.setStyle("-fx-font-weight: bold");
        Slider levelSlider = new Slider(1,10, ConfigService.getInstance().get().getLevel());
        levelSlider.setShowTickLabels(true); levelSlider.setShowTickMarks(true);
        levelSlider.setMajorTickUnit(1); levelSlider.setMinorTickCount(0); levelSlider.setSnapToTicks(true);
        Label currentLevel = new Label(Integer.toString((int)levelSlider.getValue()));
        levelSlider.valueProperty().addListener((obs, o, n) -> {
            currentLevel.setText(String.valueOf(n.intValue()));
            ConfigService.getInstance().update(c -> c.setLevel(n.intValue()));
        });
        HBox levelBox = new HBox(10, level, levelSlider, currentLevel);

        // Music
        Label music = new Label("Music (On/Off):"); music.setMinWidth(180); music.setStyle("-fx-font-weight: bold");
        CheckBox musicBox = new CheckBox(); musicBox.setSelected(ConfigService.getInstance().get().isMusicOn());
        Label currentMusicStatus = new Label(musicBox.isSelected()? "On":"Off");
        musicBox.selectedProperty().addListener((obs, ov, nv) -> {
            currentMusicStatus.setText(nv? "On":"Off");
            ConfigService.getInstance().update(c -> c.setMusicOn(nv));
        });
        HBox mBox = new HBox(10, music, musicBox, currentMusicStatus);

        // Sound
        Label sound = new Label("Sound Effect (On/Off):"); sound.setMinWidth(180); sound.setStyle("-fx-font-weight: bold");
        CheckBox soundBox = new CheckBox(); soundBox.setSelected(ConfigService.getInstance().get().isSoundOn());
        Label currentSoundStatus = new Label(soundBox.isSelected()? "On":"Off");
        soundBox.selectedProperty().addListener((obs, ov, nv) -> {
            currentSoundStatus.setText(nv? "On":"Off");
            ConfigService.getInstance().update(c -> c.setSoundOn(nv));
        });
        HBox sBox = new HBox(10, sound, soundBox, currentSoundStatus);

        // AI
        Label ai = new Label("AI Play (On/Off):"); ai.setMinWidth(180); ai.setStyle("-fx-font-weight: bold");
        CheckBox aiBox = new CheckBox(); aiBox.setSelected(ConfigService.getInstance().get().isAiOn());
        Label currentAIStatus = new Label(aiBox.isSelected()? "On":"Off");
        aiBox.selectedProperty().addListener((obs, ov, nv) -> {
            currentAIStatus.setText(nv? "On":"Off");
            ConfigService.getInstance().update(c -> c.setAiOn(nv));
        });
        HBox aiPlayBox = new HBox(10, ai, aiBox, currentAIStatus);

        // Extend
        Label extend = new Label("Extend Mode (On/Off):"); extend.setMinWidth(180); extend.setStyle("-fx-font-weight: bold");
        CheckBox exBox = new CheckBox(); exBox.setSelected(ConfigService.getInstance().get().isExtendOn());
        Label currentExtendStatus = new Label(exBox.isSelected()? "On":"Off");
        exBox.selectedProperty().addListener((obs, ov, nv) -> {
            currentExtendStatus.setText(nv? "On":"Off");
            ConfigService.getInstance().update(c -> c.setExtendOn(nv));
        });
        HBox exModeBox = new HBox(10, extend, exBox, currentExtendStatus);

        // ---- Player One Type ----
        Label p1Label = new Label("Player One Type:");
        p1Label.setMinWidth(180);
        p1Label.setStyle("-fx-font-weight: bold");

        ToggleGroup p1Group = new ToggleGroup();
        RadioButton p1Human   = new RadioButton("Human");
        RadioButton p1AI      = new RadioButton("AI");
        RadioButton p1External= new RadioButton("External");
        p1Human.setToggleGroup(p1Group);
        p1AI.setToggleGroup(p1Group);
        p1External.setToggleGroup(p1Group);

// set initial selection from config
        switch (ConfigService.getInstance().get().getPlayer1Type()) {
            case HUMAN   -> p1Human.setSelected(true);
            case AI      -> p1AI.setSelected(true);
            case EXTERNAL-> p1External.setSelected(true);
        }

// on change → update config JSON
        p1Group.selectedToggleProperty().addListener((obs, ov, nv) -> {
            if (nv == null) return;
            PlayerType newType = nv == p1Human ? PlayerType.HUMAN
                    : nv == p1AI ? PlayerType.AI
                    : PlayerType.EXTERNAL;
            ConfigService.getInstance().update(c -> c.setPlayer1Type(newType));
        });

        HBox p1Box = new HBox(16, p1Label, p1Human, p1AI, p1External);

        // ---- Player Two Type ----
        Label p2Label = new Label("Player Two Type:");
        p2Label.setMinWidth(180);
        p2Label.setStyle("-fx-font-weight: bold");

        ToggleGroup p2Group = new ToggleGroup();
        RadioButton p2Human   = new RadioButton("Human");
        RadioButton p2AI      = new RadioButton("AI");
        RadioButton p2External= new RadioButton("External");
        p2Human.setToggleGroup(p2Group);
        p2AI.setToggleGroup(p2Group);
        p2External.setToggleGroup(p2Group);

// initial selection from config
        switch (ConfigService.getInstance().get().getPlayer2Type()) {
            case HUMAN   -> p2Human.setSelected(true);
            case AI      -> p2AI.setSelected(true);
            case EXTERNAL-> p2External.setSelected(true);
        }

// on change → update JSON
        p2Group.selectedToggleProperty().addListener((obs, ov, nv) -> {
            if (nv == null) return;
            PlayerType newType = nv == p2Human ? PlayerType.HUMAN
                    : nv == p2AI ? PlayerType.AI
                    : PlayerType.EXTERNAL;
            ConfigService.getInstance().update(c -> c.setPlayer2Type(newType));
        });

        HBox p2Box = new HBox(16, p2Label, p2Human, p2AI, p2External);

// tie visibility/enabled to Extend Mode checkbox `exBox`
        boolean extendNow = ConfigService.getInstance().get().isExtendOn();
        p2Box.setDisable(!extendNow);
        p2Box.setOpacity(extendNow ? 1.0 : 0.5);

// when extend toggled, enable/disable Player Two row
        exBox.selectedProperty().addListener((obs, ov, nv) -> {
            p2Box.setDisable(!nv);
            p2Box.setOpacity(nv ? 1.0 : 0.5);
            // persist extend flag already handled above in your exBox listener
        });


        Button back = new Button("Back"); back.setOnAction(e -> sm.showMainMenu());

        configScreen.getChildren().addAll(top, wBox, hBox, levelBox, mBox, sBox, exModeBox,p1Box, p2Box, back);
        sm.getRoot().getChildren().setAll(configScreen);
    }
}
