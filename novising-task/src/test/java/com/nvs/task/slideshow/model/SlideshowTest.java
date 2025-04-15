package com.nvs.task.slideshow.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the Slideshow model.
 * This class contains unit tests to verify the functionality of the Slideshow class.
 */
class SlideshowTest {

    /**
     * Tests the addProofOfPlay method of the Slideshow class.
     * This test verifies that proof of plays can be added to a slideshow
     * and that the proofs are correctly stored and retrieved.
     * <p>
     * The test performs the following steps:
     * 1. Creates a new Slideshow instance.
     * 2. Verifies that the initial state has no proof of plays.
     * 3. Adds a proof of play and verifies it was added correctly.
     * 4. Adds a second proof of play and verifies both proofs are present.
     * <p>
     * No parameters are required for this test method.
     */
    @Test
    void addProofOfPlay() {
        // Create a slideshow
        Slideshow slideshow = new Slideshow();
        slideshow.setId(1L);
        slideshow.setName("Test Slideshow");

        // Initial state - no proof of plays
        assertTrue(slideshow.getProofOfPlays().isEmpty());

        // Add a proof of play
        LocalDateTime timestamp = LocalDateTime.now();
        slideshow.addProofOfPlay(101L, timestamp);

        // Verify proof of play was added
        assertEquals(1, slideshow.getProofOfPlays().size());
        Slideshow.ProofOfPlay proof = slideshow.getProofOfPlays().get(0);
        assertEquals(101L, proof.getImageId());
        assertEquals(timestamp, proof.getTimestamp());

        // Add another proof of play
        LocalDateTime timestamp2 = LocalDateTime.now().plusMinutes(5);
        slideshow.addProofOfPlay(102L, timestamp2);

        // Verify second proof of play was added
        assertEquals(2, slideshow.getProofOfPlays().size());
        Slideshow.ProofOfPlay proof2 = slideshow.getProofOfPlays().get(1);
        assertEquals(102L, proof2.getImageId());
        assertEquals(timestamp2, proof2.getTimestamp());
    }
}