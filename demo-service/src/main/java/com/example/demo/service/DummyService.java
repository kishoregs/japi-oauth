package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DummyService {
     private static final Logger logger = LoggerFactory.getLogger(DummyService.class);
    public String getDummyMessage() {
        logger.debug("DummyService; getDummyMessage");

        return "Hello from DummyService!";
    }
}