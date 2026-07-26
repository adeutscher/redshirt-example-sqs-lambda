package com.redshirt.example.sqslambda;

import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HandlerTests {
    @ParameterizedTest
    @CsvSource({"2, 1"})
    void testOneFailure(int numberOfJobs, int badEggIndex) {
        List<SQSEvent.SQSMessage> records = new ArrayList<>();
        SafeRecordHandler safeHandler = mock(SafeRecordHandler.class);

        for (int i = 0; i < numberOfJobs; i++) {
            SQSEvent.SQSMessage message = new SQSEvent.SQSMessage();
            message.setMessageId(UUID.randomUUID().toString());
            message.setBody(UUID.randomUUID().toString());
            records.add(message);
            when(safeHandler.handle(message)).thenReturn(i != badEggIndex);
        }

        SQSEvent sqsEvent = new SQSEvent();
        sqsEvent.setRecords(records);

        Handler handler = new Handler(safeHandler);
        SQSBatchResponse result = handler.handle(sqsEvent);

        verify(safeHandler, times(numberOfJobs)).handle(any(SQSEvent.SQSMessage.class));
        for (SQSEvent.SQSMessage currentRecord : records) {
            verify(safeHandler, times(1)).handle(currentRecord);
        }

        assertEquals(1, result.getBatchItemFailures().size());
        assertEquals(records.get(badEggIndex).getMessageId(),
                result.getBatchItemFailures().getFirst().getItemIdentifier());
    }
}
