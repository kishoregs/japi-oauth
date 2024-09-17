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
import org.springframework.web.client.RestTemplate;

@RestController
public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private OAuthTokenService oAuthTokenService;

    @Autowired
    private SpotifyService spotifyService;

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private SpotifyConfig spotifyConfig;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/call-github-api")
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

    @GetMapping("/call-github-api-oauth2")
    public ResponseEntity<String> callGithubApiOAuth2() {
        logger.info("Entering callGithubApiOAuth2 method");
        try {
            return gitHubService.getUserReposOAuth2();
        } catch (Exception e) {
            logger.error("Error occurred while calling GitHub API with OAuth2", e);
            return ResponseEntity.status(500).body("Error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/search-spotify")
    public ResponseEntity<String> searchSpotify(@RequestParam String query) {
        logger.info("Entering searchSpotify method with query: {}", query);
        logger.debug("SpotifyConfig: {}", spotifyConfig);
        try {
            ResponseEntity<String> response = spotifyService.searchTracks(query);
            logger.info("Spotify API call completed");
            return response;
        } catch (Exception e) {
            logger.error("Error occurred while calling Spotify API", e);
            return ResponseEntity.status(500).body("Error occurred: " + e.getMessage() + ". Cause: " + e.getCause());
        }
    }

    @GetMapping("/search-spotify-oauth2")
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