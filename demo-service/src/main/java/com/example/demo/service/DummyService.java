package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class DummyService {
    public String getDummyMessage() {
        return "Hello from DummyService!";
    }
}