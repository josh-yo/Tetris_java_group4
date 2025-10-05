package org.oosd.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.oosd.model.Config;
import org.oosd.model.ScoreEntry;
import org.oosd.service.ScoreService;

import java.util.List;

public class HighScoresScreen {
    private final ScreenManager sm;
    public HighScoresScreen(ScreenManager sm){ this.sm = sm; }

    public void show() {
        VBox root = new VBox(18);
        root.setPadding(new Insets(28));

        Label title = new Label("High Scores");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");
        HBox titleRow = new HBox(title);
        titleRow.setAlignment(Pos.TOP_CENTER);

        Button clearBtn = new Button("Clear High Scores");
        clearBtn.setOnAction(e -> {
            ScoreService.getInstance().clearAll();
            show(); // refresh
        });
        HBox topBar = new HBox(clearBtn);
        topBar.setAlignment(Pos.CENTER_RIGHT);

        // Header
        HBox header = row("#", "Name", "Score", "Config");
        header.setStyle("-fx-font-weight: bold; -fx-padding: 6 0 6 0;");

        VBox table = new VBox(6);
        List<ScoreEntry> entries = ScoreService.getInstance().topN(10);
        int i = 1;
        for (ScoreEntry s : entries) {
            String cfgText = "-";
            Config cfg = s.getConfigSnapshot();
            if (cfg != null) {
                StringBuilder sb = new StringBuilder()
                        .append(cfg.getFieldWidth()).append("x").append(cfg.getFieldHeight())
                        .append(" L").append(cfg.getLevel());
                if (cfg.isAiOn())     sb.append(" AI");
                if (cfg.isExtendOn()) sb.append(" EXT");
                cfgText = sb.toString();
            }
            table.getChildren().add(row("(" + (i++) + ")", s.getName(),
                    Integer.toString(s.getScore()), cfgText));
        }

        Button back = new Button("Back");
        back.setOnAction(e -> sm.showMainMenu());

        root.getChildren().addAll(titleRow, topBar, header, table, back);
        sm.getRoot().getChildren().setAll(root);
    }

    private HBox row(String c1, String c2, String c3, String c4) {
        Label l1 = new Label(c1); l1.setMinWidth(60);
        Label l2 = new Label(c2); l2.setMinWidth(200);
        Label l3 = new Label(c3); l3.setMinWidth(120);
        Label l4 = new Label(c4); l4.setMinWidth(200);
        HBox r = new HBox(40, l1, l2, l3, l4);
        r.setAlignment(Pos.CENTER_LEFT);
        return r;
    }
}
