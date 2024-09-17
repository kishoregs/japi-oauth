package com.example.demo.service;

import com.example.demo.config.SpotifyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Base64;

@Service
public class SpotifyService {

    private static final Logger logger = LoggerFactory.getLogger(SpotifyService.class);

    private final SpotifyConfig spotifyConfig;
    private final RestTemplate restTemplate;

    public SpotifyService(SpotifyConfig spotifyConfig, RestTemplate restTemplate) {
        this.spotifyConfig = spotifyConfig;
        this.restTemplate = restTemplate;
        logger.debug("SpotifyService constructed with config: {}", spotifyConfig);
    }

    @PostConstruct
    public void init() {
        logger.info("Initializing SpotifyService");
        logger.info("Spotify Client ID: {}", spotifyConfig.getClientId());
        logger.info("Spotify Client Secret: {}", spotifyConfig.getClientSecret() != null ? "******" : "null");
    }

    public String getAccessToken() {
        logger.info("Requesting Spotify access token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        // Manually create the Basic Auth header
        String auth = spotifyConfig.getClientId() + ":" + spotifyConfig.getClientSecret();
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://accounts.spotify.com/api/token",
                    request,
                    String.class  // Changed to String.class to get the raw response
            );

            logger.info("Spotify API response: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Parse the JSON response manually
                String responseBody = response.getBody();
                if (responseBody.contains("access_token")) {
                    String accessToken = responseBody.split("\"access_token\":\"")[1].split("\"")[0];
                    if (!accessToken.isEmpty()) {
                        logger.info("Successfully obtained Spotify access token");
                        return accessToken;
                    }
                }
                logger.error("Received invalid response from Spotify: {}", responseBody);
                throw new RuntimeException("Received invalid response from Spotify");
            } else {
                logger.error("Failed to obtain Spotify access token. Status: {}, Body: {}", 
                             response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to obtain Spotify access token. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Exception occurred while obtaining Spotify access token", e);
            throw new RuntimeException("Exception occurred while obtaining Spotify access token", e);
        }
    }

    public ResponseEntity<String> searchTracks(String query) {
        logger.info("Searching Spotify tracks with query: {}", query);

        String accessToken = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                "https://api.spotify.com/v1/search?q=" + query + "&type=track",
                HttpMethod.GET,
                entity,
                String.class
        );
    }

    private static class SpotifyTokenResponse {
        private String access_token;

        public String getAccessToken() {
            return access_token;
        }

        public void setAccessToken(String access_token) {
            this.access_token = access_token;
        }

        @Override
        public String toString() {
            return "SpotifyTokenResponse{" +
                    "access_token='" + (access_token != null ? access_token.substring(0, 5) + "..." : "null") + '\'' +
                    '}';
        }
    }
}