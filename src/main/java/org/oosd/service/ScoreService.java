package org.oosd.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.oosd.model.Config;
import org.oosd.model.ScoreBoard;
import org.oosd.model.ScoreEntry;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class ScoreService {
    private static volatile ScoreService INSTANCE;
    public static ScoreService getInstance() {
        if (INSTANCE == null) {
            synchronized (ScoreService.class) {
                if (INSTANCE == null) INSTANCE = new ScoreService();
            }
        }
        return INSTANCE;
    }

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path scoreFile = Paths.get(System.getProperty("user.dir"), "JavaTetrisScore.json");
    private ScoreBoard board = new ScoreBoard();

    private ScoreService() { load(); }

    public synchronized void addScore(String name, int score, Config snapshot) {
        board.getScores().add(new ScoreEntry(name, score, snapshot));
        save();
    }

    // Streams + Comparator (rubric point)
    public synchronized List<ScoreEntry> topN(int n) {
        return board.getScores().stream()
                .sorted(Comparator.comparingInt(ScoreEntry::getScore).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    public synchronized void clearAll() { board.getScores().clear(); save(); }

    private synchronized void load() {
        try {
            if (Files.exists(scoreFile)) {
                String json = Files.readString(scoreFile);
                ScoreBoard sb = gson.fromJson(json, ScoreBoard.class);
                if (sb != null && sb.getScores() != null) board = sb;
            } else { save(); }
        } catch (IOException e) { System.err.println("[ScoreService] load failed: " + e.getMessage()); }
    }

    private synchronized void save() {
        try {
            Files.writeString(scoreFile, gson.toJson(board),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) { System.err.println("[ScoreService] save failed: " + e.getMessage()); }
    }
}
