package com.example.demo.controller;

import com.example.demo.service.BackendAPIService;
import com.example.demo.service.DummyService;
import com.example.demo.service.VService;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
public class DummyController {

    @Autowired
    private DummyService dummyService;

    @GetMapping("/api/dummy")
    public String getDummyMessage() {

        return dummyService.getDummyMessage();
    }

    @Autowired
    private VService backendService;

    /*
     * This implementation provides a comprehensive approach to handling timeouts:
     * The RestTemplate is configured with connect and read timeouts1.
     * The Spring Boot application has a global async request timeout6.
     * A custom AsyncConfigurer manages the thread pool for async operations6.
     * The @Async annotation is used to make the service method asynchronous6.
     * In the controller, CompletableFuture.get()class.getName());ith a timeout is
     * used to enforce
     * a timeout on the entire operation5.
     * By following these steps, you'll have a robust timeout handling mechanism
     * both for the backend service call and within your Spring Boot application.
     */

    @Async
    @GetMapping("/data")
    public CompletableFuture<ResponseEntity<String>> getData() {
        CompletableFuture<ResponseEntity<String>> responseFuture = backendService.simCallBackendService()
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    if (ex instanceof TimeoutException) {
                        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body("Request timed out");
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("An error occurred");
                    }
                });

        return CompletableFuture.supplyAsync(() -> {
            try {
                return responseFuture.get(3, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Request timed out");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An error occurred");
            }
        });
    }

   @GetMapping("/dataasync")
    public CompletableFuture<ResponseEntity<String>> getDataAsync() {
        return backendService.callBackendService()
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    Throwable cause = ex instanceof CompletionException ? ex.getCause() : ex;
                    if (cause instanceof TimeoutException) {
                        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body("Request timed out after 3 seconds");
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("An error occurred: " + cause.getMessage());
                    }
                });
    }
    // private final BackendAPIService backendAPIService;

    public DummyController() {
        // this.backendAPIService = backendAPIService;
    }

    // @PostMapping("/myendpoint")
    // public ResponseEntity<String> myEndpoint(@RequestBody JsonNode userRequest) {
    // String response = backendAPIService.createUser(userRequest);
    // return ResponseEntity.ok(response);
    // }
}