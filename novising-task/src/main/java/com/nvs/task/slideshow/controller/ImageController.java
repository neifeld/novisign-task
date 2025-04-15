package com.nvs.task.slideshow.controller;

import com.nvs.task.slideshow.model.Image;
import com.nvs.task.slideshow.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing Image resources.
 */
@RestController
@RequestMapping("/api")  // Base path for all endpoints in this controller
public class ImageController {

    private final ImageService imageService;

    /**
     * Constructs a new ImageController with the specified ImageService.
     *
     * @param imageService the service to handle image-related operations
     */
    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Adds a new image to the system.
     *
     * @param image the Image object to be added
     * @return a Mono emitting the added Image
     */
    @PostMapping("/addImage")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Image> addImage(@RequestBody Image image) {
        return imageService.addImage(image);
    }

    /**
     * Deletes an image from the system by its ID.
     *
     * @param id the ID of the image to be deleted
     * @return a Mono completing when the deletion is done
     */
    @DeleteMapping("/images/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteImage(@PathVariable Long id) {
        return imageService.deleteImage(id);
    }

    /**
     * Searches for images based on a keyword.
     *
     * @param keyword the search term to find matching images
     * @return a Flux emitting Images that match the search keyword
     */
    @GetMapping("/images/search")
    public Flux<Image> searchImages(@RequestParam String keyword) {
        return imageService.searchImages(keyword.toLowerCase());
    }

    /**
     * Retrieves all images from the system.
     *
     * @return a Flux emitting all Images in the system
     */
    @GetMapping("/images")
    public Flux<Image> getAllImages() {
        return imageService.getAllImages();
    }

    /**
     * Retrieves a specific image by its ID.
     *
     * @param id the ID of the image to retrieve
     * @return a Mono emitting a ResponseEntity containing the Image if found, or a not found status
     */
    @GetMapping("/images/{id}")
    public Mono<ResponseEntity<Image>> getImage(@PathVariable Long id) {
        return imageService.getImage(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Updates an existing image in the system.
     *
     * @param id    the ID of the image to update
     * @param image the updated Image object
     * @return a Mono emitting the updated Image
     */
    @PutMapping("/images/{id}")
    public Mono<Image> updateImage(@PathVariable Long id, @RequestBody Image image) {
        return imageService.updateImage(id, image);
    }
}