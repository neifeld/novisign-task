package com.nvs.task.slideshow.controller;

import com.nvs.task.slideshow.config.TestConfig;
import com.nvs.task.slideshow.exception.ResourceNotFoundException;
import com.nvs.task.slideshow.model.ErrorResponse;
import com.nvs.task.slideshow.model.Image;
import com.nvs.task.slideshow.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for ImageController using WebFluxTest.
 * This class contains unit tests for the ImageController, focusing on testing
 * the web layer of the application without starting a full HTTP server.
 * <p>
 * The @WebFluxTest annotation is used to auto-configure the application context
 * for testing WebFlux components. It disables full auto-configuration and instead
 * applies only configuration relevant to WebFlux tests.
 * <p>
 * The @Import annotation is used to import additional configuration classes,
 * in this case, TestConfig, which may contain beans or configurations specific
 * to the testing environment.
 */
@WebFluxTest(ImageController.class)
@Import(TestConfig.class)
public class ImageControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ImageService imageService;

    private Image image;

    /**
     * Sets up the test environment before each test method.
     * Creates a sample image for testing purposes.
     */
    @BeforeEach
    void setUp() {
        // Create a sample image for testing
        image = new Image();
        image.setId(1L);
        image.setUrl("http://example.com/image1.jpg");
        image.setDuration(5);
    }

    /**
     * Tests the addition of a new image.
     * Verifies that the image is successfully added and returns the correct response.
     *
     * @throws IOException if there's an error reading the test payload file
     */
    @Test
    public void testAddImage() throws IOException {
        // Load test payload from file
        ClassPathResource resource = new ClassPathResource("payloads/image.json");
        String imageJson = new String(Files.readAllBytes(resource.getFile().toPath()));

        // Create a test image for the expected response
        Image responseImage = new Image();
        responseImage.setId(1L);
        responseImage.setUrl("http://example.com/image.jpg");
        responseImage.setDuration(10);
        responseImage.setName("Sample Image");
        responseImage.setDescription("A sample image for testing");

        // Mock the service response
        when(imageService.addImage(any(Image.class))).thenReturn(Mono.just(responseImage));

        // Perform the test
        webTestClient.post()
                .uri("/api/addImage")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(imageJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Image.class)
                .isEqualTo(responseImage);
    }

    /**
     * Tests retrieving a single image by its ID.
     * Verifies that the correct image is returned for a given ID.
     */
    @Test
    public void testGetImage() {
        when(imageService.getImage(anyLong())).thenReturn(Mono.just(image));

        webTestClient.get()
                .uri("/api/images/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Image.class)
                .isEqualTo(image);
    }

    /**
     * Tests retrieving all images.
     * Verifies that all images are returned correctly.
     */
    @Test
    public void testGetAllImages() {
        Image image2 = new Image();
        image2.setId(2L);
        image2.setUrl("http://example.com/image2.jpg");
        image2.setDuration(10);

        when(imageService.getAllImages()).thenReturn(Flux.just(image, image2));

        webTestClient.get()
                .uri("/api/images")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Image.class)
                .hasSize(2);
    }

    /**
     * Tests the scenario of requesting a non-existent image.
     * Verifies that a proper error response is returned when an image is not found.
     */
    @Test
    public void testGetNonExistentImage() {
        Long nonExistentId = 999L;
        when(imageService.getImage(nonExistentId)).thenReturn(Mono.error(new ResourceNotFoundException("Image not found with id: " + nonExistentId)));

        webTestClient.get()
                .uri("/api/images/" + nonExistentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response.getStatus()).isEqualTo(404);
                    assertThat(response.getError()).isEqualTo("Not Found");
                    assertThat(response.getMessage()).isEqualTo("Image not found with id: " + nonExistentId);
                });

        verify(imageService, times(1)).getImage(nonExistentId);
    }

    /**
     * Tests the deletion of an image.
     * Verifies that an image is successfully deleted and returns the correct response.
     */
    @Test
    public void testDeleteImage() {
        Long imageId = 1L;
        when(imageService.deleteImage(imageId)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/images/" + imageId)
                .exchange()
                .expectStatus().isNoContent();

        verify(imageService, times(1)).deleteImage(imageId);
    }

    /**
     * Tests adding an image with invalid data.
     * Verifies that proper error handling occurs when invalid image data is provided.
     *
     * @throws IOException if there's an error reading the test payload file
     */
    @Test
    public void testAddImageWithInvalidData() throws IOException {
        // Load test payload from file with invalid data
        ClassPathResource resource = new ClassPathResource("payloads/invalid-image.json");
        String invalidImageJson = new String(Files.readAllBytes(resource.getFile().toPath()));

        // Mock the service response for invalid data
        when(imageService.addImage(any(Image.class))).thenReturn(Mono.error(new IllegalArgumentException("Invalid image data")));

        // Perform the test
        webTestClient.post()
                .uri("/api/addImage")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidImageJson)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.status").isNumber()
                .jsonPath("$.error").exists()
                .jsonPath("$.message").isEqualTo("Invalid image data");

        // Verify the service method was called
        verify(imageService, times(1)).addImage(any(Image.class));
    }

    /**
     * Tests updating an existing image.
     * Verifies that an image is successfully updated and returns the correct response.
     */
    @Test
    public void testUpdateImage() {
        Long imageId = 1L;
        Image updatedImage = new Image();
        updatedImage.setId(imageId);
        updatedImage.setUrl("http://example.com/updated-image.jpg");
        updatedImage.setDuration(15);
        updatedImage.setName("Updated Image");
        updatedImage.setDescription("An updated image for testing");

        when(imageService.updateImage(eq(imageId), any(Image.class))).thenReturn(Mono.just(updatedImage));

        webTestClient.put()
                .uri("/api/images/" + imageId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedImage)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Image.class)
                .isEqualTo(updatedImage);

        verify(imageService, times(1)).updateImage(eq(imageId), any(Image.class));
    }

    /**
     * Tests updating a non-existent image.
     * Verifies that proper error handling occurs when attempting to update a non-existent image.
     */
    @Test
    public void testUpdateNonExistentImage() {
        Long nonExistentId = 999L;
        Image updatedImage = new Image();
        updatedImage.setId(nonExistentId);
        updatedImage.setUrl("http://example.com/non-existent-image.jpg");
        updatedImage.setDuration(20);
        updatedImage.setName("Non-existent Image");
        updatedImage.setDescription("An image that doesn't exist");

        when(imageService.updateImage(eq(nonExistentId), any(Image.class)))
                .thenReturn(Mono.error(new ResourceNotFoundException("Image not found with id: " + nonExistentId)));

        webTestClient.put()
                .uri("/api/images/" + nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedImage)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response.getStatus()).isEqualTo(404);
                    assertThat(response.getError()).isEqualTo("Not Found");
                    assertThat(response.getMessage()).isEqualTo("Image not found with id: " + nonExistentId);
                });

        verify(imageService, times(1)).updateImage(eq(nonExistentId), any(Image.class));
    }

    /**
     * Tests using an unsupported HTTP method on an endpoint.
     * Verifies that the correct error response is returned for unsupported methods.
     */
    @Test
    public void testUnsupportedMethodOnEndpoint() {
        webTestClient.patch()
                .uri("/api/images/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
                .expectBody()
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.status").isEqualTo(405)
                .jsonPath("$.error").isEqualTo("Method Not Allowed")
                .jsonPath("$.message").isEqualTo("Request method 'PATCH' is not supported.")
                .jsonPath("$.path").isEqualTo("/api/images/1");
    }

    /**
     * Tests searching for images with a non-existent keyword.
     * Verifies that an empty result is returned when searching with a non-existent keyword.
     */
    @Test
    public void testSearchImagesWithNonExistentKeyword() {
        String nonExistentKeyword = "nonexistent";
        when(imageService.searchImages(nonExistentKeyword)).thenReturn(Flux.empty());

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/images/search")
                        .queryParam("keyword", nonExistentKeyword)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Image.class)
                .hasSize(0);

        verify(imageService, times(1)).searchImages(nonExistentKeyword);
    }

    /**
     * Tests searching for images with a case-insensitive keyword.
     * Verifies that the search is performed case-insensitively.
     */
    @Test
    public void testSearchImagesWithCaseInsensitiveKeyword() {
        String keyword = "Sample";
        String lowercaseKeyword = keyword.toLowerCase();
        when(imageService.searchImages(lowercaseKeyword)).thenReturn(Flux.empty());

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/images/search")
                        .queryParam("keyword", keyword)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Image.class)
                .hasSize(0);

        verify(imageService, times(1)).searchImages(lowercaseKeyword);
    }

    /**
     * Tests retrieving an image with a non-existent ID.
     * Verifies that a proper error response is returned when an image is not found.
     */
    @Test
    public void testGetImageWithNonExistentId() {
        Long nonExistentId = 999L;
        when(imageService.getImage(nonExistentId)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/images/" + nonExistentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();

        verify(imageService, times(1)).getImage(nonExistentId);
    }

    /**
     * Tests handling of unexpected exceptions.
     * Verifies that unexpected errors are properly caught and result in an appropriate error response.
     */
    @Test
    public void testUnexpectedExceptionHandling() {
        Long imageId = 1L;
        RuntimeException unexpectedException = new RuntimeException("Unexpected error");

        when(imageService.getImage(imageId)).thenReturn(Mono.error(unexpectedException));

        webTestClient.get()
                .uri("/api/images/" + imageId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response.getStatus()).isEqualTo(500);
                    assertThat(response.getError()).isEqualTo("Internal Server Error");
                    assertThat(response.getMessage()).isEqualTo("An unexpected error occurred: Unexpected error");
                });

        verify(imageService, times(1)).getImage(imageId);
    }
}