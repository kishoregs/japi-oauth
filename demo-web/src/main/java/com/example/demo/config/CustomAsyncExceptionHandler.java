package com.example.demo.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;
import java.util.concurrent.TimeoutException;

public class CustomAsyncExceptionHandler implements
        AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        if (ex instanceof TimeoutException) {
            // Handle timeout exception
            System.err.println("Request timed out: " + method.getName());
        } else {
            // Handle other exceptions
            System.err.println("Unexpected error occurred: " + ex.getMessage());
        }
    }
}
