package org.oosd.service;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class MusicPlayer {
    private MediaPlayer player;

    public boolean isPlaying() {
        return player != null && player.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public void start(String resourcePath, boolean loop) {
        stop();
        URL url = getClass().getResource(resourcePath);
        if (url == null) {
            System.err.println("[MusicPlayer] not found: " + resourcePath);
            return;
        }
        Media m = new Media(url.toExternalForm());
        player = new MediaPlayer(m);
        if (loop) player.setCycleCount(MediaPlayer.INDEFINITE);
        player.setVolume(0.25); // gentle
        player.play();
    }

    public void stop() {
        if (player != null) {
            player.stop();
            player.dispose();
            player = null;
        }
    }
}
