package com.redshirt.example.sqslambda.core;

public interface MessageHandler {
    void handleMessage(String message) throws Exception;
}
