package com.redshirt.example.sqslambda.core;

public class DefaultMessageHandler implements MessageHandler {
    @Override
    public void handleMessage(String message) {
        System.out.println("HandleMessage: " + message);
    }
}
