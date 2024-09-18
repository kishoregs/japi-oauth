package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GitHubService {

    private static final Logger logger = LoggerFactory.getLogger(GitHubService.class);

    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public GitHubService(RestTemplate restTemplate, OAuth2AuthorizedClientManager authorizedClientManager) {
        this.restTemplate = restTemplate;
        this.authorizedClientManager = authorizedClientManager;
    }

    public ResponseEntity<String> getUserReposOAuth2() {
        logger.info("Fetching GitHub user repos with OAuth2");

        String accessToken = getAccessTokenOAuth2();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                "https://api.github.com/user/repos",
                HttpMethod.GET,
                entity,
                String.class
        );
    }

    private String getAccessTokenOAuth2() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("github")
                .principal(authentication)
                .build();

        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);
        
        if (authorizedClient != null) {
            return authorizedClient.getAccessToken().getTokenValue();
        }
        
        return null;
    }
}