package com.redshirt.example.sqslambda;

import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class Handler {
    private final SafeRecordHandler safeRecordHandler;

    @Inject
    public Handler(SafeRecordHandler safeRecordHandler) {
        this.safeRecordHandler = safeRecordHandler;
    }

    public SQSBatchResponse handle(SQSEvent sqsEvent) {
        List<SQSEvent.SQSMessage> records = sqsEvent.getRecords();
        if(records == null)
        {
            records = List.of();
        }

        List<CompletableFuture<Boolean>> futures = new ArrayList<>(records.size());
        for (SQSEvent.SQSMessage currentRecord : records) {
            futures.add(CompletableFuture.supplyAsync(() -> safeRecordHandler.handle(currentRecord)));
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        List<SQSBatchResponse.BatchItemFailure> failures = new ArrayList<>();
        for (int i = 0; i < futures.size(); i++) {
            if (!getResult(futures.get(i))) {
                failures.add(SQSBatchResponse.BatchItemFailure.builder()
                        .withItemIdentifier(records.get(i).getMessageId())
                        .build());
            }
        }

        return SQSBatchResponse.builder()
                .withBatchItemFailures(failures.isEmpty() ? Collections.emptyList() : failures)
                .build();
    }

    private static boolean getResult(CompletableFuture<Boolean> future) {
        try {
            return Boolean.TRUE.equals(future.join());
        } catch (CompletionException e) {
            return false;
        }
    }
}
