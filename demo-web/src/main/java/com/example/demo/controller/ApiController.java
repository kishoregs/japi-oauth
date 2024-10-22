package com.example.demo.controller;

import com.example.demo.service.OAuthTokenService;
import com.example.demo.service.SpotifyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;

import com.example.demo.service.GitHubService;
import com.example.demo.config.SpotifyConfig;
import com.example.demo.dto.TrackDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class ApiController {

    @Autowired
    private OAuthTokenService oAuthTokenService;

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private SpotifyService spotifyService;

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private SpotifyConfig spotifyConfig;

    @Autowired
    private ObjectMapper objectMapper;

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
                    String.class);
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

    private final CacheManager cacheManager;

    @GetMapping("/api/search-spotifyf")
     
    public CompletableFuture<ResponseEntity<?>> searchSpotifyf(
            @RequestParam String query,
            @RequestParam(defaultValue = "track") String type,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(required = false) String market) throws JsonProcessingException, UnsupportedEncodingException {

        logger.info("Received search request for query: '{}', type: '{}', limit: {}, offset: {}, market: '{}'",
                query, type, limit, offset, market);

        String cacheKey = String.format("spotify_search_%s_%s_%d_%d_%s", query, type, limit, offset, market);
        Cache cache = cacheManager.getCache("spotifySearchCache");

        if (cache != null) {
            Cache.ValueWrapper cacheWrapper = cache.get(cacheKey);
            if (cacheWrapper != null) {
                JsonNode cachedValue = (JsonNode) cacheWrapper.get();
                if (cachedValue != null) {
                    JsonNode cachedResult = objectMapper.readValue(cachedValue.toString(), JsonNode.class);
                    if (cachedResult != null) {
                        logger.debug("Cache hit for query: '{}'", query);
                        return CompletableFuture.completedFuture(ResponseEntity.ok(cachedResult));
                    }
                }
            }
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Cache miss for query: '{}', fetching from Spotify API", query);
                ResponseEntity<String> response = spotifyService.searchItems(query, type, limit, offset);

                if (cache != null && response != null) {

                    // Convert the response to JsonNode

                    JsonNode jsonNode = objectMapper.readTree(response.getBody());

                    // Cache the result
                    cache.put(cacheKey, jsonNode);
                    logger.debug("Cached result for query: '{}'", query);
                }

                return ResponseEntity.ok(response);
            } catch (Exception e) {
                logger.error("Error occurred while searching Spotify for query: '{}'", query, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"error\": \"An error occurred while processing your request\"}");
            }
        });
    }

    @GetMapping("/api/search-spotify")
    public ResponseEntity<?> searchSpotify(
            @RequestParam String query,
            @RequestParam(defaultValue = "track") String type,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        logger.info("Entering searchSpotify method with query: {}, type: {}, limit: {}, offset: {}", query, type, limit,
                offset);
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

    @GetMapping("api/search/spotify-oauth2")
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

    @GetMapping("/api/search-spotify-lite")
    public ResponseEntity<JsonNode> searchSpotifyLite(@RequestParam String query) {
        logger.info("Entering searchSpotifyLite method with query: {}", query);
        logger.debug("SpotifyConfig: {}", spotifyConfig);
        try {

            ResponseEntity<String> fullResponse = spotifyService.searchTracksOAuth2(query);

            if (fullResponse.getStatusCode().is2xxSuccessful()) {
                JsonNode fullResponseBody = objectMapper.readTree(fullResponse.getBody());
                ObjectNode lightResponse = objectMapper.createObjectNode();
                ArrayNode lightItems = objectMapper.createArrayNode();

                JsonNode items = fullResponseBody.path("tracks").path("items");
                for (JsonNode item : items) {
                    ObjectNode lightItem = objectMapper.createObjectNode();
                    JsonNode album = item.path("album");

                    lightItem.set("album_name", album.path("name"));
                    lightItem.set("album_uri", album.path("uri"));
                    lightItem.set("external_urls", album.path("external_urls"));
                    lightItem.set("images", album.path("images"));
                    lightItem.set("release_date", album.path("release_date"));

                    lightItems.add(lightItem);
                }

                lightResponse.set("items", lightItems);
                return ResponseEntity.ok(lightResponse);
            } else {
                return ResponseEntity.status(fullResponse.getStatusCode())
                        .body(objectMapper.readTree(fullResponse.getBody()));
            }

        } catch (Exception e) {
            logger.error("Error occurred while calling searchSpotifyLite  with OAuth2", e);
            ObjectNode errorResponse = objectMapper.createObjectNode();
            errorResponse.put("error", "Error occurred: " + e.getMessage());
            errorResponse.put("cause", e.getCause() != null ? e.getCause().toString() : "Unknown");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/api/search-spotify-lightweight")
    public JsonNode getLightweightSpotifyResponse() throws IOException {
        JsonNode fullResponse = objectMapper
                .readTree(new ClassPathResource("spotify-sample-resp.json").getInputStream());
        ObjectNode lightResponse = objectMapper.createObjectNode();
        ArrayNode lightItems = objectMapper.createArrayNode();

        JsonNode items = fullResponse.path("tracks").path("items");
        for (JsonNode item : items) {
            ObjectNode lightItem = objectMapper.createObjectNode();
            JsonNode album = item.path("album");

            lightItem.set("album_name", album.path("name"));
            lightItem.set("album_uri", album.path("uri"));
            lightItem.set("external_urls", album.path("external_urls"));
            lightItem.set("images", album.path("images"));
            lightItem.set("release_date", album.path("release_date"));

            lightItems.add(lightItem);
        }

        lightResponse.set("items", lightItems);
        return lightResponse;
    }
}
