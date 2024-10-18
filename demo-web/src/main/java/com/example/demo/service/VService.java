package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@Service
public class VService {

    @Autowired
    private RestTemplate restTemplate;

    @Async
    public CompletableFuture<String> simCallBackendService() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(4000); // Simulate a 15-second delay
                return "Delayed response";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CompletionException(e);
            }
        });
    }


    @Async
    public CompletableFuture<String> callBackendService() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                // Prepare the request
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                String requestBody = "{\"key\": \"value\"}"; // Your JSON request body

                HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

                // Make the POST request
                String url = "https://api.example.com/endpoint"; // Replace with your API endpoint
                return restTemplate.postForObject(url, request, String.class);
            } catch (Exception e) {
                throw new CompletionException("Error calling backend service", e);
            }
        });

        return CompletableFuture.supplyAsync(() -> {
            try {
                return future.get(3, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                throw new CompletionException("Request timed out after 3 seconds", e);
            } catch (Exception e) {
                throw new CompletionException("Error calling backend service", e);
            }
        });
    }
}
