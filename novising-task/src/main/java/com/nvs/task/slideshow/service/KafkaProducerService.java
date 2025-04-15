package com.nvs.task.slideshow.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class responsible for producing and sending Kafka messages.
 * <p>
 * This service provides methods to send various types of events to Kafka topics,
 * particularly focused on proof-of-play events for slideshows.
 */
@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${kafka.topic.proof-of-play:proof-of-play}")
    private String proofOfPlayTopic;

    /**
     * Sends a proof of play event to Kafka.
     * <p>
     * This method creates an event with slideshow and image information,
     * serializes it to JSON, and sends it to a Kafka topic.
     *
     * @param slideshowId The unique identifier of the slideshow being played.
     * @param imageId     The unique identifier of the image that was displayed in the slideshow.
     * @return A Mono<Void> that completes when the message is successfully sent to Kafka.
     * If an error occurs during JSON processing, the Mono will contain the error.
     */
    public Mono<Void> sendProofOfPlayEvent(Long slideshowId, Long imageId) {
        Map<String, Object> event = new HashMap<>();
        event.put("slideshowId", slideshowId);
        event.put("imageId", imageId);
        event.put("timestamp", LocalDateTime.now().toString());
        event.put("eventType", "PROOF_OF_PLAY");

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(proofOfPlayTopic, slideshowId.toString(), message);
            return Mono.empty();
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }
}