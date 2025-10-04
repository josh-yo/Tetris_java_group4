package org.oosd.service;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MusicPlayerTest {

    private MusicPlayer musicPlayer;

    @BeforeEach
    void setUp() {
        musicPlayer = new MusicPlayer();
    }

    @Test
    @DisplayName("Music should start and set loop correctly")
    void testStartAndLoop() {
        // Mock Media and MediaPlayer so no real audio is needed
        try (MockedConstruction<Media> mockedMedia = mockConstruction(Media.class);
             MockedConstruction<MediaPlayer> mockedPlayer = mockConstruction(MediaPlayer.class,
                     (mock, ctx) -> when(mock.getStatus()).thenReturn(MediaPlayer.Status.PLAYING))) {

            String path = "/audio/background.mp3"; // fake resource path
            musicPlayer.start(path, true);

            // MediaPlayer should be created and configured
            assertFalse(mockedPlayer.constructed().isEmpty(), "MediaPlayer should be constructed");
            MediaPlayer mp = mockedPlayer.constructed().get(0);

            // Verify playback settings
            verify(mp).setCycleCount(MediaPlayer.INDEFINITE);
            verify(mp).setVolume(0.25);
            verify(mp).play();

            assertTrue(musicPlayer.isPlaying(), "Music should be marked as playing");
        }
    }

    @Test
    @DisplayName("Stop should stop and dispose MediaPlayer")
    void testStop() {
        try (MockedConstruction<Media> mockedMedia = mockConstruction(Media.class);
             MockedConstruction<MediaPlayer> mockedPlayer = mockConstruction(MediaPlayer.class,
                     (mock, ctx) -> when(mock.getStatus()).thenReturn(MediaPlayer.Status.PLAYING))) {

            musicPlayer.start("/audio/background.mp3", false);
            MediaPlayer mp = mockedPlayer.constructed().get(0);

            musicPlayer.stop();

            // After stop, MediaPlayer should be shut down
            verify(mp).stop();
            verify(mp).dispose();
            assertFalse(musicPlayer.isPlaying(), "After stop, should not be playing");
        }
    }

    @Test
    @DisplayName("isPlaying returns false if no MediaPlayer")
    void testIsPlayingWithoutPlayer() {
        assertFalse(musicPlayer.isPlaying(), "No player should mean not playing");
    }
}
