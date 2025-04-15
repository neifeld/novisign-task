package com.nvs.task.slideshow.service;

import com.nvs.task.slideshow.config.TestConfig;
import com.nvs.task.slideshow.exception.ResourceNotFoundException;
import com.nvs.task.slideshow.model.Image;
import com.nvs.task.slideshow.repository.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for ImageService.
 * This class contains unit tests for the ImageService methods using WebFlux and reactive programming.
 */
@WebFluxTest(ImageService.class)
@Import(TestConfig.class)
class ImageServiceTest {

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageRepository imageRepository;

    /**
     * Set up method to initialize mocks before each test.
     * This method opens mocks and resets the imageRepository.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reset(imageRepository);
    }

    /**
     * Test method to verify the addition of an image with a valid URL.
     * It checks if the image is correctly saved and returned.
     */
    @Test
    void testAddImageWithValidUrl() {
        // Arrange
        Image testImage = new Image();
        testImage.setUrl("https://example.com/image.jpg");

        when(imageRepository.save(any(Image.class))).thenReturn(Mono.just(testImage));

        // Act
        Mono<Image> result = imageService.addImage(testImage);

        // Assert
        StepVerifier.create(result)
                .expectNext(testImage)
                .verifyComplete();

        verify(imageRepository, times(1)).save(testImage);
    }

    /**
     * Test method to verify the behavior when adding an image with an invalid URL.
     * It checks if the appropriate exception is thrown and no save operation is performed.
     */
    @Test
    void testAddImageWithInvalidUrl() {
        // Arrange
        Image testImage = new Image();
        testImage.setUrl("invalid-url");

        // Act
        Mono<Image> result = imageService.addImage(testImage);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Invalid image URL: invalid-url")
                )
                .verify();

        verify(imageRepository, never()).save(any(Image.class));
    }

    /**
     * Test method to verify the deletion of an image.
     * It checks if the delete operation is called on the repository and completes successfully.
     */
    @Test
    void testDeleteImage() {
        // Arrange
        Long imageId = 1L;
        when(imageRepository.deleteById(imageId)).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = imageService.deleteImage(imageId);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(imageRepository, times(1)).deleteById(imageId);
    }

    /**
     * Test method to verify the search functionality when no images match the search criteria.
     * It checks if an empty result is returned when searching for non-existent images.
     */
    @Test
    void testSearchImagesReturnsEmptyForNonExistentImages() {
        // Arrange
        String nonExistentKeyword = "nonexistent";
        when(imageRepository.findByUrlContainingIgnoreCase(nonExistentKeyword))
                .thenReturn(Flux.empty());

        // Act
        Flux<Image> result = imageService.searchImages(nonExistentKeyword);

        // Assert
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(imageRepository, times(1)).findByUrlContainingIgnoreCase(nonExistentKeyword);
    }

    /**
     * Test method to verify the validation of image URLs with allowed file extensions.
     * It checks if images with valid extensions (jpg, png, gif, svg) are accepted.
     */
    @Test
    void testValidateImageUrlWithAllowedExtensions() {
        // Arrange
        String[] validUrls = {
                "https://example.com/image.jpg",
                "http://test.org/picture.png",
                "https://images.site.com/photo.gif",
                "http://mysite.net/icon.svg"
        };

        for (String url : validUrls) {
            Image image = new Image();
            image.setUrl(url);
            when(imageRepository.save(any(Image.class))).thenReturn(Mono.just(image));

            // Act & Assert
            StepVerifier.create(imageService.addImage(image))
                    .expectNextMatches(savedImage -> savedImage.getUrl().equals(url))
                    .verifyComplete();
        }

        verify(imageRepository, times(validUrls.length)).save(any(Image.class));
    }

    /**
     * Test method to verify the validation of image URLs with invalid file extensions.
     * It checks if an exception is thrown when an image with an invalid extension is added.
     */
    @Test
    void testValidateImageUrlWithInvalidExtension() {
        // Arrange
        String invalidUrl = "https://example.com/image.txt";
        Image image = new Image();
        image.setUrl(invalidUrl);

        // Act & Assert
        StepVerifier.create(imageService.addImage(image))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("Invalid image URL: " + invalidUrl)
                )
                .verify();

        verify(imageRepository, never()).save(any(Image.class));
    }

    /**
     * Test method to verify the retrieval of all images.
     * It checks if all images are correctly returned from the repository.
     */
    @Test
    void testGetAllImages() {
        // Arrange
        Image image1 = new Image(1L, "https://example.com/image1.jpg");
        Image image2 = new Image(2L, "https://example.com/image2.png");
        when(imageRepository.findAll()).thenReturn(Flux.just(image1, image2));

        // Act
        Flux<Image> result = imageService.getAllImages();

        // Assert
        StepVerifier.create(result)
                .expectNext(image1)
                .expectNext(image2)
                .verifyComplete();

        verify(imageRepository, times(1)).findAll();
    }

    /**
     * Test method to verify the retrieval of a single image by its ID.
     * It checks if the correct image is returned when a valid ID is provided.
     */
    @Test
    void testGetImageWithValidId() {
        // Arrange
        Long imageId = 1L;
        Image expectedImage = new Image(imageId, "https://example.com/image.jpg");
        when(imageRepository.findById(imageId)).thenReturn(Mono.just(expectedImage));

        // Act
        Mono<Image> result = imageService.getImage(imageId);

        // Assert
        StepVerifier.create(result)
                .expectNext(expectedImage)
                .verifyComplete();

        verify(imageRepository, times(1)).findById(imageId);
    }

    /**
     * Test method to verify the behavior when updating a non-existent image.
     * It checks if the appropriate exception is thrown when trying to update an image that doesn't exist.
     */
    @Test
    void testUpdateImageNonExistent() {
        // Arrange
        Long nonExistentId = 999L;
        Image updatedImage = new Image(nonExistentId, "https://example.com/updated.jpg");
        when(imageRepository.findById(nonExistentId)).thenReturn(Mono.empty());

        // Act
        Mono<Image> result = imageService.updateImage(nonExistentId, updatedImage);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResourceNotFoundException &&
                                throwable.getMessage().equals("Image not found with id: " + nonExistentId)
                )
                .verify();

        verify(imageRepository, times(1)).findById(nonExistentId);
        verify(imageRepository, never()).save(any(Image.class));
    }

    /**
     * Test method to verify the successful update of an existing image.
     * It checks if the image is correctly updated and saved in the repository.
     */
    @Test
    void testUpdateImageSuccessfully() {
        // Arrange
        Long imageId = 1L;
        Image existingImage = new Image(imageId, "https://example.com/old-image.jpg");
        Image updatedImage = new Image(imageId, "https://example.com/new-image.png");

        when(imageRepository.findById(imageId)).thenReturn(Mono.just(existingImage));
        when(imageRepository.save(any(Image.class))).thenReturn(Mono.just(updatedImage));

        // Act
        Mono<Image> result = imageService.updateImage(imageId, updatedImage);

        // Assert
        StepVerifier.create(result)
                .expectNext(updatedImage)
                .verifyComplete();

        verify(imageRepository, times(1)).findById(imageId);
        verify(imageRepository, times(1)).save(updatedImage);
    }
}