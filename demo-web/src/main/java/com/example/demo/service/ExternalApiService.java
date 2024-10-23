package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExternalApiService {

    @Autowired
    private RestTemplate restTemplate;

    // Custom exception class for API errors
    public class ApiException extends RuntimeException {
        private final HttpStatus status;

        public ApiException(String message, HttpStatus status) {
            super(message);
            this.status = status;
        }

        public HttpStatus getStatus() {
            return status;
        }
    }

    public ResponseEntity<String> callExternalApi(String requestUrl) {
        try {
            // Make the API call
            ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);

            // If successful, return the response
            return ResponseEntity.ok(response.getBody());

        } catch (ResourceAccessException e) {
            // Handle connection timeout, read timeout, and other connectivity issues
            log.error("Connection error while calling external API: {}", e.getMessage());
            String errorMessage = "Unable to connect to external service";
            throw new ApiException(errorMessage, HttpStatus.SERVICE_UNAVAILABLE);

        } catch (RestClientResponseException e) {
            // Handle specific HTTP error responses (4xx, 5xx)
            log.error("External API error - Status: {}, Body: {}",
                    e.getRawStatusCode(), e.getResponseBodyAsString());

            if (e.getRawStatusCode() >= 500) {
                throw new ApiException("External service error", HttpStatus.BAD_GATEWAY);
            } else if (e.getRawStatusCode() == 404) {
                throw new ApiException("Resource not found", HttpStatus.NOT_FOUND);
            } else {
                throw new ApiException("External API error", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            // Handle any other unexpected errors
            log.error("Unexpected error while calling external API: {}", e.getMessage());
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
