package com.redshirt.example.sqslambda;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.inject.Inject;
import com.redshirt.example.sqslambda.core.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSafeRecordHandler implements SafeRecordHandler {
    private static final Logger log = LoggerFactory.getLogger(DefaultSafeRecordHandler.class);

    private final MessageHandler messageHandler;

    @Inject
    public DefaultSafeRecordHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public boolean handle(SQSEvent.SQSMessage message) {
        try {
            messageHandler.handleMessage(message.getBody());
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }
}
