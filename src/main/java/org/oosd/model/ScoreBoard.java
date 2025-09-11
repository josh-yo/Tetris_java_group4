package org.oosd.model;

import java.util.ArrayList;
import java.util.List;

public class ScoreBoard {
    private List<ScoreEntry> scores = new ArrayList<>();
    public List<ScoreEntry> getScores() { return scores; }
    public void setScores(List<ScoreEntry> scores) { this.scores = scores; }
}
