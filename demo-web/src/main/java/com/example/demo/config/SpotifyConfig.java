package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "spotify")
public class SpotifyConfig {
    //46ceb2e8d1d449ab9cfa64138c115946
    //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
    private static final Logger logger = LoggerFactory.getLogger(SpotifyConfig.class);

    private String clientId;
    private String clientSecret;

    // Getters and setters
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    public String toString() {
        return "SpotifyConfig{" +
                "clientId='" + (clientId != null ? clientId : "null") + '\'' +
                ", clientSecret='" + (clientSecret != null ? (clientSecret.length() > 8 ? 
                    clientSecret.substring(0, 4) + "..." + clientSecret.substring(clientSecret.length() - 4) : "***") 
                    : "null") + '\'' +
                '}';
    }

    @PostConstruct
    public void init() {
        logger.info("Spotify Client ID: {}", clientId);
        logger.info("Spotify Client Secret: {}", clientSecret != null ? clientSecret.substring(0, 4) + "..." : "null");
    }
}