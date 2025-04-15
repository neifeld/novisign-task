package com.nvs.task.slideshow.repository;

import com.nvs.task.slideshow.model.Image;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * Repository interface for managing Image entities.
 * Extends ReactiveCrudRepository to provide reactive CRUD operations for Image objects.
 */
public interface ImageRepository extends ReactiveCrudRepository<Image, Long> {

    /**
     * Finds images whose URL contains the given keyword, ignoring case.
     *
     * @param keyword The search term to look for in image URLs (case-insensitive)
     * @return A Flux emitting Image objects whose URLs contain the given keyword
     */
    Flux<Image> findByUrlContainingIgnoreCase(String keyword);
}