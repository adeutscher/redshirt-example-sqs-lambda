package com.redshirt.example.sqslambda;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;

public interface SafeRecordHandler {
    boolean handle(SQSEvent.SQSMessage message);
}
