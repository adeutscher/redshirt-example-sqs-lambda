package com.redshirt.example.sqslambda;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.redshirt.example.sqslambda.core.MessageHandler;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SafeRecordHandlerTests {
    @Test
    void handle_shouldReturnFalse() throws Exception {
        String messageString = UUID.randomUUID().toString();

        MessageHandler messageHandler = mock(MessageHandler.class);
        doThrow(new RuntimeException("BOOM")).when(messageHandler).handleMessage(messageString);

        SafeRecordHandler safeHandler = new DefaultSafeRecordHandler(messageHandler);

        SQSEvent.SQSMessage message = new SQSEvent.SQSMessage();
        message.setBody(messageString);

        assertFalse(safeHandler.handle(message));

        verify(messageHandler, times(1)).handleMessage(anyString());
        verify(messageHandler, times(1)).handleMessage(messageString);
    }

    @Test
    void handle_shouldReturnTrue() throws Exception {
        String messageString = UUID.randomUUID().toString();

        MessageHandler messageHandler = mock(MessageHandler.class);

        SafeRecordHandler safeHandler = new DefaultSafeRecordHandler(messageHandler);

        SQSEvent.SQSMessage message = new SQSEvent.SQSMessage();
        message.setBody(messageString);

        assertTrue(safeHandler.handle(message));

        verify(messageHandler, times(1)).handleMessage(anyString());
        verify(messageHandler, times(1)).handleMessage(messageString);
    }
}
