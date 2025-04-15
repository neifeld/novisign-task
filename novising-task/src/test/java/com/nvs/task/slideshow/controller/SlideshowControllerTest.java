package com.nvs.task.slideshow.controller;

import com.nvs.task.slideshow.config.TestConfig;
import com.nvs.task.slideshow.exception.ResourceNotFoundException;
import com.nvs.task.slideshow.model.ErrorResponse;
import com.nvs.task.slideshow.model.Slideshow;
import com.nvs.task.slideshow.service.SlideshowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for SlideshowController.
 * This class contains unit tests for the SlideshowController using WebFluxTest.
 */
@WebFluxTest(SlideshowController.class)
@Import(TestConfig.class)
class SlideshowControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private SlideshowService slideshowService;

    private Slideshow slideshow;
    private String slideshowJson;
    private String updateSlideshowJson;

    /**
     * Sets up the test environment before each test method.
     * This method creates a sample slideshow, loads test payloads from JSON files,
     * and resets the mock SlideshowService.
     *
     * @throws IOException If there's an error reading the JSON files.
     */
    @BeforeEach
    void setUp() throws IOException {
        // Create a sample slideshow for testing
        slideshow = new Slideshow();
        slideshow.setId(1L);
        slideshow.setName("Test Slideshow");
        slideshow.setImageIds(Arrays.asList(101L, 102L, 103L));

        // Load test payloads from files
        ClassPathResource addResource = new ClassPathResource("payloads/add-slideshow.json");
        slideshowJson = new String(Files.readAllBytes(addResource.getFile().toPath()));

        ClassPathResource updateResource = new ClassPathResource("payloads/update-slideshow.json");
        updateSlideshowJson = new String(Files.readAllBytes(updateResource.getFile().toPath()));

        // Reset and configure the mock for each test
        reset(slideshowService);
    }

    /**
     * Tests the addition of a new slideshow.
     * This method verifies that the controller correctly handles POST requests
     * to add a new slideshow and returns the expected response.
     */
    @Test
    void testAddSlideshow() {
        // Create a slideshow to be returned by the service
        Slideshow savedSlideshow = new Slideshow();
        savedSlideshow.setId(1L);
        savedSlideshow.setName("New Slideshow");
        savedSlideshow.setImageIds(Arrays.asList(101L, 102L));

        // Mock the service response
        when(slideshowService.addSlideshow(any(Slideshow.class))).thenReturn(Mono.just(savedSlideshow));

        // Perform the test
        webTestClient.post()
                .uri("/api/addSlideshow")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(slideshowJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Slideshow.class)
                .isEqualTo(savedSlideshow);

        // Verify the service method was called
        verify(slideshowService, times(1)).addSlideshow(any(Slideshow.class));
    }

    /**
     * Tests the deletion of a slideshow.
     * This method verifies that the controller correctly handles DELETE requests
     * to remove a slideshow and returns the expected response.
     */
    @Test
    void testDeleteSlideshow() {
        // Mock the service response
        when(slideshowService.deleteSlideshow(anyLong())).thenReturn(Mono.empty());

        // Perform the test
        webTestClient.delete()
                .uri("/api/deleteSlideshow/1")
                .exchange()
                .expectStatus().isNoContent();

        // Verify the service method was called with the correct parameter
        verify(slideshowService, times(1)).deleteSlideshow(1L);
    }

    /**
     * Tests retrieving the order of a slideshow.
     * This method verifies that the controller correctly handles GET requests
     * to retrieve a slideshow's order and returns the expected response.
     */
    @Test
    void testGetSlideshowOrder() {
        // Mock the service response
        when(slideshowService.getSlideshow(anyLong())).thenReturn(Mono.just(slideshow));

        // Perform the test
        webTestClient.get()
                .uri("/api/slideShow/1/slideshowOrder")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Slideshow.class)
                .isEqualTo(slideshow);

        // Verify the service method was called with the correct parameter
        verify(slideshowService, times(1)).getSlideshow(1L);
    }

    /**
     * Tests recording a proof of play for a slideshow image.
     * This method verifies that the controller correctly handles POST requests
     * to record a proof of play and returns the expected response.
     */
    @Test
    void testRecordProofOfPlay() {
        // Mock the service response
        when(slideshowService.recordProofOfPlay(anyLong(), anyLong())).thenReturn(Mono.empty());

        // Perform the test
        webTestClient.post()
//                .uri("/api/slideShow/1/proof-of-play/101")
                .uri("/api/slideShow/{id}/proof-of-play/{imageId}", 1L, 101L)
                .exchange()
                .expectStatus().isCreated();

        // Verify the service method was called with the correct parameters
        verify(slideshowService, times(1)).recordProofOfPlay(1L, 101L);
    }

    /**
     * Tests retrieving all slideshows.
     * This method verifies that the controller correctly handles GET requests
     * to retrieve all slideshows and returns the expected response.
     */
    @Test
    void testGetAllSlideshows() {
        // Create test slideshows
        Slideshow slideshow1 = new Slideshow();
        slideshow1.setId(1L);
        slideshow1.setName("Test Slideshow 1");

        Slideshow slideshow2 = new Slideshow();
        slideshow2.setId(2L);
        slideshow2.setName("Test Slideshow 2");

        // Mock the service response
        when(slideshowService.getAllSlideshows()).thenReturn(Flux.just(slideshow1, slideshow2));

        // Perform the test
        webTestClient.get()
                .uri("/api/slideshows")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Slideshow.class)
                .hasSize(2)
                .contains(slideshow1, slideshow2);

        // Verify the service method was called
        verify(slideshowService, times(1)).getAllSlideshows();
    }

    /**
     * Tests updating an existing slideshow.
     * This method verifies that the controller correctly handles PUT requests
     * to update a slideshow and returns the expected response.
     */
    @Test
    void testUpdateSlideshow() {
        // Create a slideshow to be returned by the service
        Slideshow updatedSlideshow = new Slideshow();
        updatedSlideshow.setId(1L);
        updatedSlideshow.setName("Updated Slideshow");
        updatedSlideshow.setImageIds(Arrays.asList(201L, 202L));

        // Mock the service response
        when(slideshowService.updateSlideshow(eq(1L), any(Slideshow.class))).thenReturn(Mono.just(updatedSlideshow));

        // Perform the test
        webTestClient.put()
                .uri("/api/slideshow/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateSlideshowJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Slideshow.class)
                .isEqualTo(updatedSlideshow);

        // Verify the service method was called with the correct parameters
        verify(slideshowService, times(1)).updateSlideshow(eq(1L), any(Slideshow.class));
    }

    /**
     * Tests the scenario where a requested slideshow is not found.
     * This method verifies that the controller correctly handles GET requests
     * for non-existent slideshows and returns the expected error response.
     */
    @Test
    void testGetSlideshowNotFound() {
        // Mock the service response for a not found scenario
        when(slideshowService.getSlideshow(99L))
                .thenReturn(Mono.error(new ResourceNotFoundException("Slideshow not found with id: 99")));

        // Perform the test
        webTestClient.get()
                .uri("/api/slideShow/{id}/slideshowOrder", 99L)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response.getStatus()).isEqualTo(404);
                    assertThat(response.getError()).isEqualTo("Not Found");
                    assertThat(response.getMessage()).isEqualTo("Slideshow not found with id: 99");
                });

        // Verify the service method was called with the correct parameter
        verify(slideshowService, times(1)).getSlideshow(99L);
    }

    /**
     * Tests the scenario where an update is attempted on a non-existent slideshow.
     * This method verifies that the controller correctly handles PUT requests
     * for non-existent slideshows and returns the expected error response.
     */
    @Test
    void testUpdateSlideshowNotFound() {
        // Mock the service response for a not found scenario
        when(slideshowService.updateSlideshow(eq(99L), any(Slideshow.class)))
                .thenReturn(Mono.error(new ResourceNotFoundException("Slideshow", 99L)));

        // Perform the test
        webTestClient.put()
                .uri("/api/slideshow/99")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateSlideshowJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response.getStatus()).isEqualTo(404);
                    assertThat(response.getError()).isEqualTo("Not Found");
                    assertThat(response.getMessage()).isEqualTo("Slideshow not found with id: 99");
                });

        // Verify the service method was called with the correct parameters
        verify(slideshowService, times(1)).updateSlideshow(eq(99L), any(Slideshow.class));
    }

    /**
     * Tests the scenario where a proof of play is attempted for a non-existent slideshow.
     * This method verifies that the controller correctly handles POST requests
     * for recording proof of play on non-existent slideshows and returns the expected error response.
     */
    @Test
    void testRecordProofOfPlayNotFound() {
        // Mock the service response for a not found scenario
        when(slideshowService.recordProofOfPlay(99L, 101L))
                .thenReturn(Mono.error(new ResourceNotFoundException("Slideshow", 99L)));

        // Perform the test
        webTestClient.post()
                .uri("/api/slideShow/99/proof-of-play/101")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response.getStatus()).isEqualTo(404);
                    assertThat(response.getError()).isEqualTo("Not Found");
                    assertThat(response.getMessage()).isEqualTo("Slideshow not found with id: 99");
                });

        // Verify the service method was called with the correct parameters
        verify(slideshowService, times(1)).recordProofOfPlay(99L, 101L);
    }
}