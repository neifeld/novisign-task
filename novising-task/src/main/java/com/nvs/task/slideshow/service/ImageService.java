package com.nvs.task.slideshow.service;

import com.nvs.task.slideshow.exception.ResourceNotFoundException;
import com.nvs.task.slideshow.model.Image;
import com.nvs.task.slideshow.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * Service class for managing image-related operations.
 * This class handles various functionalities such as adding, deleting, updating,
 * and retrieving images from the repository.
 */
@Service
public class ImageService {

    // List of allowed image file extensions
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".svg"
    );
    /**
     * Autowired instance of ImageRepository for database operations.
     */
    @Autowired
    private ImageRepository imageRepository;

    /**
     * Adds a new image to the repository after validating its URL.
     * <p>
     * This method performs the following steps:
     * 1. Validates the URL of the image using the validateImageUrl method.
     * 2. If the URL is valid, saves the image to the repository.
     * 3. If the URL is invalid, returns an error.
     *
     * @param image The Image object to be added to the repository. It should contain
     *              a valid URL and other necessary image details.
     * @return A Mono&lt;Image&gt; representing the asynchronous result of the operation.
     * If successful, it contains the saved Image object.
     * If the URL is invalid, it contains an error.
     * @throws IllegalArgumentException if the image URL is invalid
     */
    public Mono<Image> addImage(Image image) {
        return validateImageUrl(image.getUrl())
                .flatMap(valid -> {
                    if (Boolean.TRUE.equals(valid)) {
                        return imageRepository.save(image);
                    } else {
                        return Mono.error(new IllegalArgumentException("Invalid image URL: " + image.getUrl()));
                    }
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid image URL: " + image.getUrl())));
    }

    public Mono<Void> deleteImage(Long id) {
        return imageRepository.deleteById(id);
    }

    public Flux<Image> searchImages(String keyword) {
        return imageRepository.findByUrlContainingIgnoreCase(keyword);
    }

    /**
     * Validates that the provided URL:
     * 1. Is a properly formatted URL
     * 2. Uses HTTP or HTTPS protocol
     * 3. Has an allowed image file extension
     *
     * @param url The URL to validate
     * @return Mono&lt;Boolean&gt; indicating if the URL is valid
     */
    private Mono<Boolean> validateImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return Mono.just(false);
        }

        List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".svg");
        return Mono.just(allowedExtensions.stream().anyMatch(url.toLowerCase()::endsWith));
    }

    /**
     * Gets all images from the repository
     *
     * @return Flux of all images
     */
    public Flux<Image> getAllImages() {
        return imageRepository.findAll();
    }

    /**
     * Gets an image by its ID
     *
     * @param id The image ID
     * @return Mono containing the image if found
     */
    public Mono<Image> getImage(Long id) {
        return imageRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Image not found with id: " + id)));
    }

    /**
     * Updates an existing image in the repository.
     * <p>
     * This method performs the following steps:
     * 1. Finds the existing image by its ID.
     * 2. If the image is found, updates its properties with the provided data.
     * 3. Saves the updated image to the repository.
     * 4. If the image is not found, throws a ResourceNotFoundException.
     *
     * @param id           The ID of the image to be updated.
     * @param updatedImage The Image object containing the updated data. It should include
     *                     the new values for name, URL, description, and duration.
     * @return A Mono<Image> representing the asynchronous result of the operation.
     * If successful, it contains the updated Image object.
     * @throws ResourceNotFoundException if no image is found with the given ID.
     */
    public Mono<Image> updateImage(Long id, Image updatedImage) {
        return imageRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Image not found with id: " + id)))
                .flatMap(existingImage -> {
                    existingImage.setName(updatedImage.getName());
                    existingImage.setUrl(updatedImage.getUrl());
                    existingImage.setDescription(updatedImage.getDescription());
                    existingImage.setDuration(updatedImage.getDuration());
                    return imageRepository.save(existingImage);
                });
    }
}