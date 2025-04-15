package com.nvs.task.slideshow.service;

import com.nvs.task.slideshow.exception.BadRequestException;
import com.nvs.task.slideshow.exception.ResourceNotFoundException;
import com.nvs.task.slideshow.model.Slideshow;
import com.nvs.task.slideshow.repository.SlideshowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service class for managing slideshow operations.
 * This class provides methods for creating, retrieving, updating, and deleting slideshows,
 * as well as recording proof of play for images within slideshows.
 */
@Service
public class SlideshowService {

    @Autowired
    private SlideshowRepository slideshowRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    /**
     * Adds a new slideshow to the repository.
     *
     * @param slideshow The Slideshow object to be added
     * @return A Mono emitting the saved Slideshow object
     */
    public Mono<Slideshow> addSlideshow(Slideshow slideshow) {
        return slideshowRepository.save(slideshow);
    }

    /**
     * Deletes a slideshow from the repository by its ID.
     *
     * @param id The ID of the slideshow to be deleted
     * @return A Mono that completes when the deletion is done
     */
    public Mono<Void> deleteSlideshow(Long id) {
        return slideshowRepository.deleteById(id);
    }

    /**
     * Retrieves a slideshow from the repository by its ID.
     *
     * @param id The ID of the slideshow to be retrieved
     * @return A Mono emitting the found Slideshow object, or an error if not found
     */
    public Mono<Slideshow> getSlideshow(Long id) {
        return slideshowRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Slideshow", id)));
    }

    /**
     * Retrieves all slideshows from the repository.
     *
     * @return A Flux emitting all Slideshow objects in the repository
     */
    public Flux<Slideshow> getAllSlideshows() {
        return slideshowRepository.findAll();
    }

    /**
     * Updates an existing slideshow in the repository.
     *
     * @param id        The ID of the slideshow to be updated
     * @param slideshow The Slideshow object containing updated information
     * @return A Mono emitting the updated Slideshow object, or an error if not found
     */
    public Mono<Slideshow> updateSlideshow(Long id, Slideshow slideshow) {
        return slideshowRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Slideshow", id)))
                .flatMap(existingSlideshow -> {
                    existingSlideshow.setName(slideshow.getName());
                    existingSlideshow.setImageIds(slideshow.getImageIds());
                    return slideshowRepository.save(existingSlideshow);
                });
    }

    /**
     * Records a proof of play for an image in a slideshow.
     * 1. Updates the slideshow entity with the proof of play record
     * 2. Sends a Kafka event with the proof of play information
     *
     * @param slideshowId The ID of the slideshow
     * @param imageId     The ID of the image that was displayed
     * @return A Mono that completes when both operations are done
     * @throws ResourceNotFoundException if the slideshow is not found
     * @throws BadRequestException       if the image is not part of the slideshow
     */
    public Mono<Void> recordProofOfPlay(Long slideshowId, Long imageId) {
        return slideshowRepository.findById(slideshowId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Slideshow", slideshowId)))
                .flatMap(slideshow -> {
                    if (!slideshow.getImageIds().contains(imageId)) {
                        return Mono.error(new BadRequestException("Image with id " + imageId +
                                " is not part of slideshow with id " + slideshowId));
                    }

                    return kafkaProducerService.sendProofOfPlayEvent(slideshowId, imageId);
                });
    }
}