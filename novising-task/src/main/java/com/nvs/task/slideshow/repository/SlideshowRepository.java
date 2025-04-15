package com.nvs.task.slideshow.repository;

import com.nvs.task.slideshow.model.Slideshow;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * Repository interface for managing Slideshow entities.
 * This interface extends R2dbcRepository to provide CRUD operations for Slideshow objects.
 * It uses reactive programming paradigms for database interactions.
 */
public interface SlideshowRepository extends R2dbcRepository<Slideshow, Long> {
}