package org.oosd.service;

import org.junit.jupiter.api.*;
import org.oosd.model.Config;
import org.oosd.model.ScoreEntry;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoreServiceTest {

    private ScoreService service;

    @BeforeEach
    void setUp() {
        // Use the singleton instance and start fresh
        service = ScoreService.getInstance();
        service.clearAll();
    }

    @Test
    @DisplayName("Adding a score should persist it")
    void testAddScore() {
        Config dummyConfig = new Config();
        service.addScore("Alice", 1200, dummyConfig);

        List<ScoreEntry> scores = service.topN(10);

        assertEquals(1, scores.size());
        assertEquals("Alice", scores.get(0).getName());
        assertEquals(1200, scores.get(0).getScore());
    }

    @Test
    @DisplayName("TopN should return highest scores first")
    void testTopN() {
        Config c = new Config();
        service.addScore("Bob", 500, c);
        service.addScore("Alice", 1500, c);
        service.addScore("Charlie", 1000, c);

        List<ScoreEntry> top2 = service.topN(2);

        // Highest scores should be sorted descending
        assertEquals(2, top2.size());
        assertEquals("Alice", top2.get(0).getName());
        assertEquals("Charlie", top2.get(1).getName());
    }

    @Test
    @DisplayName("Clearing all scores should remove everything")
    void testClearAll() {
        Config c = new Config();
        service.addScore("Bob", 700, c);

        service.clearAll();

        assertTrue(service.topN(10).isEmpty());
    }
}
