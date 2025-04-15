package com.nvs.task.slideshow.controller;

import com.nvs.task.slideshow.model.Slideshow;
import com.nvs.task.slideshow.service.SlideshowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing slideshow operations.
 */
@RestController
@RequestMapping("/api")
public class SlideshowController {

    @Autowired
    private SlideshowService slideshowService;

    /**
     * Adds a new slideshow to the system.
     *
     * @param slideshow The Slideshow object to be added
     * @return A Mono&lt;Slideshow&gt; representing the added slideshow
     */
    @PostMapping("/addSlideshow")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Slideshow> addSlideshow(@RequestBody Slideshow slideshow) {
        return slideshowService.addSlideshow(slideshow);
    }

    /**
     * Deletes a slideshow from the system.
     *
     * @param id The ID of the slideshow to be deleted
     * @return A Mono&lt;Void&gt; indicating the completion of the delete operation
     */
    @DeleteMapping("/deleteSlideshow/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteSlideshow(@PathVariable Long id) {
        return slideshowService.deleteSlideshow(id);
    }

    /**
     * Retrieves the order of slides in a specific slideshow.
     *
     * @param id The ID of the slideshow
     * @return A Mono&lt;Slideshow&gt; containing the slideshow with its order
     */
    @GetMapping("/slideShow/{id}/slideshowOrder")
    public Mono<Slideshow> getSlideshowOrder(@PathVariable Long id) {
        return slideshowService.getSlideshow(id);
    }

    /**
     * Records a proof of play for a specific image in a slideshow.
     *
     * @param id      The ID of the slideshow
     * @param imageId The ID of the image for which proof of play is being recorded
     * @return A Mono&lt;Void&gt; indicating the completion of the recording operation
     */
    @PostMapping("/slideShow/{id}/proof-of-play/{imageId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> recordProofOfPlay(@PathVariable Long id, @PathVariable Long imageId) {
        return slideshowService.recordProofOfPlay(id, imageId);
    }

    /**
     * Retrieves all slideshows in the system.
     *
     * @return A Flux&lt;Slideshow&gt; containing all slideshows
     */
    @GetMapping("/slideshows")
    public Flux<Slideshow> getAllSlideshows() {
        return slideshowService.getAllSlideshows();
    }

    /**
     * Updates an existing slideshow.
     *
     * @param id        The ID of the slideshow to be updated
     * @param slideshow The updated Slideshow object
     * @return A Mono&lt;Slideshow&gt; representing the updated slideshow
     */
    @PutMapping("/slideshow/{id}")
    public Mono<Slideshow> updateSlideshow(@PathVariable Long id, @RequestBody Slideshow slideshow) {
        return slideshowService.updateSlideshow(id, slideshow);
    }
}