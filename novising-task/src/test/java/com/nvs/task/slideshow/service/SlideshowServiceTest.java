package com.nvs.task.slideshow.service;

import com.nvs.task.slideshow.exception.BadRequestException;
import com.nvs.task.slideshow.exception.ResourceNotFoundException;
import com.nvs.task.slideshow.model.Slideshow;
import com.nvs.task.slideshow.repository.SlideshowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Test class for SlideshowService.
 * This class contains unit tests for the SlideshowService, testing various operations
 * such as adding, deleting, retrieving, and updating slideshows, as well as recording proof of play.
 * It uses Mockito for mocking dependencies and JUnit 5 for test execution.
 */
@ExtendWith(MockitoExtension.class)
class SlideshowServiceTest {

    @Mock
    private SlideshowRepository slideshowRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private SlideshowService slideshowService;

    private Slideshow slideshow;

    @BeforeEach
    void setUp() {
        slideshow = new Slideshow();
        slideshow.setId(1L);
        slideshow.setName("Test Slideshow");
        slideshow.setImageIds(Arrays.asList(101L, 102L, 103L));
    }

    /**
     * Tests the addition of a new slideshow.
     * Verifies that the slideshow is correctly saved and returned.
     */
    @Test
    void addSlideshow() {
        when(slideshowRepository.save(any(Slideshow.class))).thenReturn(Mono.just(slideshow));

        StepVerifier.create(slideshowService.addSlideshow(slideshow))
                .expectNext(slideshow)
                .verifyComplete();

        verify(slideshowRepository, times(1)).save(any(Slideshow.class));
    }

    /**
     * Tests the deletion of a slideshow.
     * Verifies that the deleteById method is called with the correct ID.
     */
    @Test
    void deleteSlideshow() {
        when(slideshowRepository.deleteById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(slideshowService.deleteSlideshow(1L))
                .verifyComplete();

        verify(slideshowRepository, times(1)).deleteById(1L);
    }

    /**
     * Tests retrieving a slideshow by its ID.
     * Verifies that the correct slideshow is returned when found.
     */
    @Test
    void getSlideshow() {
        when(slideshowRepository.findById(anyLong())).thenReturn(Mono.just(slideshow));

        StepVerifier.create(slideshowService.getSlideshow(1L))
                .expectNext(slideshow)
                .verifyComplete();

        verify(slideshowRepository, times(1)).findById(1L);
    }

    /**
     * Tests the scenario when a slideshow is not found.
     * Verifies that a ResourceNotFoundException is thrown with the correct message.
     */
    @Test
    void getSlideshowNotFound() {
        when(slideshowRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(slideshowService.getSlideshow(1L))
                .expectErrorMatches(throwable ->
                        throwable instanceof ResourceNotFoundException &&
                                throwable.getMessage().contains("Slideshow not found with id: 1"))
                .verify();

        verify(slideshowRepository, times(1)).findById(1L);
    }

    /**
     * Tests retrieving all slideshows.
     * Verifies that all slideshows are correctly returned.
     */
    @Test
    void getAllSlideshows() {
        Slideshow slideshow2 = new Slideshow();
        slideshow2.setId(2L);
        slideshow2.setName("Test Slideshow 2");

        when(slideshowRepository.findAll()).thenReturn(Flux.just(slideshow, slideshow2));

        StepVerifier.create(slideshowService.getAllSlideshows())
                .expectNext(slideshow)
                .expectNext(slideshow2)
                .verifyComplete();

        verify(slideshowRepository, times(1)).findAll();
    }

    /**
     * Tests updating an existing slideshow.
     * Verifies that the slideshow is correctly updated and saved.
     */
    @Test
    void updateSlideshow() {
        Slideshow updatedSlideshow = new Slideshow();
        updatedSlideshow.setName("Updated Slideshow");
        updatedSlideshow.setImageIds(Arrays.asList(201L, 202L));

        when(slideshowRepository.findById(anyLong())).thenReturn(Mono.just(slideshow));
        when(slideshowRepository.save(any(Slideshow.class))).thenReturn(Mono.just(updatedSlideshow));

        StepVerifier.create(slideshowService.updateSlideshow(1L, updatedSlideshow))
                .expectNext(updatedSlideshow)
                .verifyComplete();

        verify(slideshowRepository, times(1)).findById(1L);
        verify(slideshowRepository, times(1)).save(any(Slideshow.class));
    }

    /**
     * Tests the scenario when trying to update a non-existent slideshow.
     * Verifies that a ResourceNotFoundException is thrown with the correct message.
     */
    @Test
    void updateSlideshowNotFound() {
        Slideshow updatedSlideshow = new Slideshow();
        updatedSlideshow.setName("Updated Slideshow");

        when(slideshowRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(slideshowService.updateSlideshow(1L, updatedSlideshow))
                .expectErrorMatches(throwable ->
                        throwable instanceof ResourceNotFoundException &&
                                throwable.getMessage().contains("Slideshow not found with id: 1"))
                .verify();

        verify(slideshowRepository, times(1)).findById(1L);
        verify(slideshowRepository, never()).save(any(Slideshow.class));
    }

    /**
     * Tests recording a proof of play for a slideshow.
     * Verifies that the proof of play event is correctly sent to Kafka.
     */
    @Test
    void recordProofOfPlay() {
        when(slideshowRepository.findById(anyLong())).thenReturn(Mono.just(slideshow));
        when(kafkaProducerService.sendProofOfPlayEvent(anyLong(), anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(slideshowService.recordProofOfPlay(1L, 101L))
                .verifyComplete();

        verify(slideshowRepository).findById(1L);
        verify(kafkaProducerService).sendProofOfPlayEvent(1L, 101L);
    }

    /**
     * Tests the scenario when trying to record a proof of play for a non-existent slideshow.
     * Verifies that a ResourceNotFoundException is thrown with the correct message.
     */
    @Test
    void recordProofOfPlaySlideshowNotFound() {
        when(slideshowRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(slideshowService.recordProofOfPlay(1L, 101L))
                .expectErrorMatches(throwable ->
                        throwable instanceof ResourceNotFoundException &&
                                throwable.getMessage().contains("Slideshow not found with id: 1"))
                .verify();

        verify(slideshowRepository, times(1)).findById(1L);
        verify(slideshowRepository, never()).save(any(Slideshow.class));
    }

    /**
     * Tests the scenario when trying to record a proof of play for an image not in the slideshow.
     * Verifies that a BadRequestException is thrown with the correct message.
     */
    @Test
    void recordProofOfPlayImageNotInSlideshow() {
        when(slideshowRepository.findById(anyLong())).thenReturn(Mono.just(slideshow));

        StepVerifier.create(slideshowService.recordProofOfPlay(1L, 999L))
                .expectErrorMatches(throwable ->
                        throwable instanceof BadRequestException &&
                                throwable.getMessage().contains("Image with id 999 is not part of slideshow with id 1"))
                .verify();

        verify(slideshowRepository, times(1)).findById(1L);
        verify(slideshowRepository, never()).save(any(Slideshow.class));
        verify(kafkaProducerService, never()).sendProofOfPlayEvent(anyLong(), anyLong());
    }
}