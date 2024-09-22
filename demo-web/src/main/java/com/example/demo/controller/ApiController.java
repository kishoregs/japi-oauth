package com.example.demo.controller;

import com.example.demo.service.OAuthTokenService;
import com.example.demo.service.SpotifyService;
import com.example.demo.service.GitHubService;
import com.example.demo.config.SpotifyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Arrays;

@RestController
public class ApiController {

    @Autowired
    private OAuthTokenService oAuthTokenService;

    public ApiController(OAuthTokenService oAuthTokenService) {
        this.oAuthTokenService = oAuthTokenService;
    }

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private SpotifyService spotifyService;

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private SpotifyConfig spotifyConfig;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/api/call-github-api")
    public ResponseEntity<String> callGithubApi() {
        logger.info("Entering callGithubApi method");
        try {
            String accessToken = oAuthTokenService.getAccessToken();
            logger.info("Access token obtained");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            logger.info("Calling GitHub API");
            ResponseEntity<String> response = restTemplate.exchange(
                "https://api.github.com/user/repos",
                HttpMethod.GET,
                entity,
                String.class
            );
            logger.info("GitHub API call completed");
            return response;
        } catch (Exception e) {
            logger.error("Error occurred while calling GitHub API", e);
            return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/api/call-github-api-oauth2")
    public ResponseEntity<String> callGithubApiOAuth2() {
        logger.info("Entering callGithubApiOAuth2 method");
        try {
            return gitHubService.getUserReposOAuth2();
        } catch (Exception e) {
            logger.error("Error occurred while calling GitHub API with OAuth2", e);
            return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/api/search-spotify")
    public ResponseEntity<?> searchSpotify(
            @RequestParam String query,
            @RequestParam(defaultValue = "track") String type,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        logger.info("Entering searchSpotify method with query: {}, type: {}, limit: {}, offset: {}", query, type, limit, offset);
        logger.debug("SpotifyConfig: {}", spotifyConfig);

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Query parameter cannot be empty");
        }

        if (limit < 1 || limit > 50) {
            return ResponseEntity.badRequest().body("Limit must be between 1 and 50");
        }

        if (offset < 0) {
            return ResponseEntity.badRequest().body("Offset must be non-negative");
        }

        List<String> validTypes = Arrays.asList("album", "artist", "playlist", "track", "show", "episode");
        if (!validTypes.contains(type)) {
            return ResponseEntity.badRequest().body("Invalid type. Must be one of: " + String.join(", ", validTypes));
        }

        try {
            ResponseEntity<String> response = spotifyService.searchItems(query, type, limit, offset);
            logger.info("Spotify API call completed successfully");
            return response;
        } catch (HttpClientErrorException e) {
            logger.error("Client error occurred while calling Spotify API", e);
            return ResponseEntity.status(e.getStatusCode()).body("Spotify API error: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            logger.error("Server error occurred while calling Spotify API", e);
            return ResponseEntity.status(500).body("Spotify server error. Please try again later.");
        } catch (Exception e) {
            logger.error("Unexpected error occurred while calling Spotify API", e);
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/api/search-spotify-oauth2")
    public ResponseEntity<String> searchSpotifyOAuth2(@RequestParam String query) {
        logger.info("Entering searchSpotifyOAuth2 method with query: {}", query);
        logger.debug("SpotifyConfig: {}", spotifyConfig);
        try {
            return spotifyService.searchTracksOAuth2(query);
        } catch (Exception e) {
            logger.error("Error occurred while calling Spotify API with OAuth2", e);
            return ResponseEntity.status(500).body("Error occurred: " + e.getMessage() + ". Cause: " + e.getCause());
        }
    }
}