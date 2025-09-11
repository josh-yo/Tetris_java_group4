package org.oosd.service;

import javafx.scene.media.AudioClip;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

public enum Effect {
    MOVE, ERASE, LEVEL_UP, GAME_FINISH;

    private static final Map<Effect, String> PATHS = new EnumMap<>(Effect.class);
    private static final Map<Effect, AudioClip> CACHE = new EnumMap<>(Effect.class);

    static {
        PATHS.put(MOVE,       "/audio/move-turn.wav");
        PATHS.put(ERASE,      "/audio/erase-line.wav");
        PATHS.put(LEVEL_UP,   "/audio/level-up.wav");
        PATHS.put(GAME_FINISH,"/audio/game-finish.wav");
    }

    public void play() {
        AudioClip c = CACHE.computeIfAbsent(this, Effect::load);
        if (c != null) c.play();
    }

    private static AudioClip load(Effect e) {
        String p = PATHS.get(e);
        URL url = Effect.class.getResource(p);
        if (url == null) {
            System.err.println("[Effect] resource not found: " + p);
            return null;
        }
        AudioClip clip = new AudioClip(url.toExternalForm());
        clip.setVolume(0.6);
        return clip;
    }
}
