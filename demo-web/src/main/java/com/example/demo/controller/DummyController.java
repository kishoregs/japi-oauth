package com.example.demo.controller;

import com.example.demo.service.DummyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyController {

    @Autowired
    private DummyService dummyService;

    @GetMapping("/api/dummy")
    public String getDummyMessage() {
        return dummyService.getDummyMessage();
    }
}