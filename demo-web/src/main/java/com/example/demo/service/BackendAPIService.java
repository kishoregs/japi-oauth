package com.example.demo.service;

import java.net.SocketTimeoutException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.ConnectTimeoutException;

public class BackendAPIService {
    private final RestTemplate restTemplate;
        private final String backendApiUrl = "https://example.com/backend/createUser";

        public BackendAPIService(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }

        public String createUser(JsonNode userRequest) {
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(backendApiUrl, userRequest, String.class);
                return response.getBody();
            } catch (RestClientException e) {
                // Handle rest client exceptions (including ResourceAccessException)
                System.err.println("Error calling the backend: " + e.getMessage());
                return "Error";
                // Handle rest client exceptions (including ResourceAccessException)
               
            }
        }
    
}
