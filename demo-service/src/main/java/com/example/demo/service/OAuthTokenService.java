package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class



    OAuthTokenService {

    private static final Logger logger = LoggerFactory.getLogger(OAuthTokenService.class);

    @Value("${github.personal-access-token}")
    private String personalAccessToken;

    public String getAccessToken() {
        logger.info("Returning GitHub personal access token");
        return personalAccessToken;
    }
}