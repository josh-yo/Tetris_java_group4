package org.oosd.model;

import org.oosd.model.PlayerType;


public class Config {
    private int fieldWidth  = 10;  // 5..15
    private int fieldHeight = 20;  // 15..30
    private int level       = 1;   // 1..10

    private PlayerType player1Type = PlayerType.HUMAN;
    private PlayerType player2Type = PlayerType.HUMAN;

    private boolean musicOn  = false;
    private boolean soundOn  = false;
    private boolean aiOn     = false;
    private boolean extendOn = false;

    public int getFieldWidth() { return fieldWidth; }
    public void setFieldWidth(int fieldWidth) { this.fieldWidth = Math.max(5, Math.min(15, fieldWidth)); }

    public int getFieldHeight() { return fieldHeight; }
    public void setFieldHeight(int fieldHeight) { this.fieldHeight = Math.max(15, Math.min(30, fieldHeight)); }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = Math.max(1, Math.min(10, level)); }

    public boolean isMusicOn() { return musicOn; }
    public void setMusicOn(boolean musicOn) { this.musicOn = musicOn; }

    public boolean isSoundOn() { return soundOn; }
    public void setSoundOn(boolean soundOn) { this.soundOn = soundOn; }

    public boolean isAiOn() { return aiOn; }
    public void setAiOn(boolean aiOn) { this.aiOn = aiOn; }

    public boolean isExtendOn() { return extendOn; }
    public void setExtendOn(boolean extendOn) { this.extendOn = extendOn; }

    public PlayerType getPlayer1Type() { return player1Type; }
    public void setPlayer1Type(PlayerType t) { this.player1Type = (t == null ? PlayerType.HUMAN : t); }

    public PlayerType getPlayer2Type() { return player2Type; }
    public void setPlayer2Type(PlayerType t) { this.player2Type = (t == null ? PlayerType.HUMAN : t); }

}
