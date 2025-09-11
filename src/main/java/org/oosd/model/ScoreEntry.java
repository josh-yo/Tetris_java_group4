package org.oosd.model;

public class ScoreEntry {
    private String name;
    private int score;
    private Config configSnapshot;

    public ScoreEntry() {}

    public ScoreEntry(String name, int score, Config cfg) {
        this.name = name;
        this.score = score;
        this.configSnapshot = cfg;
    }

    public String getName() { return name; }
    public int getScore() { return score; }
    public Config getConfigSnapshot() { return configSnapshot; }

    // Extra helper: return config summary as string (for High Scores table)
    public String getConfigSummary() {
        if (configSnapshot == null) return "-";
        StringBuilder sb = new StringBuilder();
        sb.append(configSnapshot.getFieldWidth())
                .append("x")
                .append(configSnapshot.getFieldHeight())
                .append(" L")
                .append(configSnapshot.getLevel());

        if (configSnapshot.isAiOn()) sb.append(" AI");
        if (configSnapshot.isExtendOn()) sb.append(" EXT");

        return sb.toString();
    }
}
