package com.redshirt.example.sqslambda;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.redshirt.example.sqslambda.core.MessageHandler;
import com.redshirt.example.sqslambda.implementations.ImplementationsModule;
import org.slf4j.LoggerFactory;

/**
 * Lambda entry point. Lazily builds a Guice injector on first invocation and reuses it.
 * Do not put {@link Context} in the injector — it is not refreshed per invocation.
 */
public class Function implements RequestHandler<SQSEvent, SQSBatchResponse> {
    public volatile Injector injector;

    public Function() {
    }

    public synchronized void confirmDependencyInjection() {
        if (injector != null) {
            return;
        }

        EnvironmentConfig configuration = EnvironmentConfig.fromEnvironment();
        configureLogging(configuration);

        injector = Guice.createInjector(new ImplementationsModule(), new HostModule());
    }

    private static void configureLogging(EnvironmentConfig configuration) {
        String configured = configuration.get("LogLevel");
        if (configured == null) {
            configured = configuration.get("LOGLEVEL");
        }

        Level level = Level.toLevel(configured, Level.WARN);
        // Map Microsoft-style Information → INFO
        if (configured != null && configured.equalsIgnoreCase("Information")) {
            level = Level.INFO;
        }

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).setLevel(level);
    }

    @Override
    public SQSBatchResponse handleRequest(SQSEvent event, Context context) {
        confirmDependencyInjection();
        return injector.getInstance(Handler.class).handle(event);
    }

    public MessageHandler resolveMessageHandler() {
        return injector.getInstance(MessageHandler.class);
    }

    public Handler resolveHandler() {
        return injector.getInstance(Handler.class);
    }
}
