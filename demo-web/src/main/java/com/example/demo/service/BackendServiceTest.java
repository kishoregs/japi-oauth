package com.example.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BackendServiceTest {

    @Autowired
    private VService backendService;

    @MockBean
    private RestTemplate restTemplate;

    public BackendServiceTest() {
    }

    @Test
    public void testTimeoutHandling() throws Exception {
        // Simulate a timeout by delaying the response
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenAnswer(invocation -> {
                    Thread.sleep(6000); // Simulate a 6-second delay
                    return "Delayed response";
                });

        CompletableFuture<String> future = backendService.callBackendService();

        // Assert that a TimeoutException is thrown when we try to get the result
        assertThrows(TimeoutException.class, () -> {
            future.get(5, TimeUnit.SECONDS);
        });
    }
}
