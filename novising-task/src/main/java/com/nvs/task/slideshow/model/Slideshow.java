package com.nvs.task.slideshow.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a slideshow entity in the application.
 * This class is mapped to the "slideshows" table in the database.
 * It contains information about the slideshow, including its ID, name,
 * associated image IDs, and proof of play records.
 * <p>
 * The @Data annotation from Lombok automatically generates getters, setters,
 * equals, hashCode, and toString methods.
 * <p>
 * The @Table annotation specifies the name of the database table this class maps to.
 */
@Data
@Table("slideshows")
public class Slideshow {
    @Id
    private Long id;
    private String name;
    private List<Long> imageIds;

    private List<ProofOfPlay> proofOfPlays = new ArrayList<>();

    /**
     * Adds a new proof of play record to the slideshow.
     * This method creates a new ProofOfPlay object with the given image ID and timestamp,
     * and adds it to the list of proof of plays for this slideshow.
     *
     * @param imageId   The ID of the image that was played
     * @param timestamp The date and time when the image was played
     */
    public void addProofOfPlay(Long imageId, LocalDateTime timestamp) {
        ProofOfPlay proof = new ProofOfPlay(imageId, timestamp);
        this.proofOfPlays.add(proof);
    }

    // Inner class to represent a proof of play record
    @Data
    public static class ProofOfPlay {
        private Long imageId;
        private LocalDateTime timestamp;

        public ProofOfPlay(Long imageId, LocalDateTime timestamp) {
            this.imageId = imageId;
            this.timestamp = timestamp;
        }

    }
}