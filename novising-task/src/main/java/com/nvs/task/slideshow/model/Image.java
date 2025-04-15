package com.nvs.task.slideshow.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Represents an image entity in the slideshow application.
 * This class is mapped to the "images" table in the database.
 */
@Data
@NoArgsConstructor
@Table("images")
public class Image {
    /**
     * The unique identifier for the image.
     */
    @Id
    private Long id;

    /**
     * The name of the image.
     */
    private String name;

    /**
     * The URL where the image can be accessed.
     */
    private String url;

    /**
     * A description of the image.
     */
    private String description;

    /**
     * The duration (in seconds) for which the image should be displayed.
     */
    private int duration;

    /**
     * Constructs a new Image with the specified ID and URL.
     *
     * @param imageId The unique identifier for the image.
     * @param url     The URL where the image can be accessed.
     */
    public Image(Long imageId, String url) {
        this.id = imageId;
        this.url = url;
    }
}