package com.redshirt.example.sqslambda;

import com.redshirt.example.sqslambda.core.MessageHandler;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class FunctionTests {
    @Test
    void testDependencyInjection() {
        Function function = new Function();
        assertNull(function.injector);

        function.confirmDependencyInjection();

        assertNotNull(function.injector);
        Injector original = function.injector;

        assertNotNull(function.resolveHandler(), "Could not find handler");
        assertNotNull(function.resolveMessageHandler(), "Could not find message handler");
        assertNotNull(function.injector.getInstance(MessageHandler.class));

        function.confirmDependencyInjection();
        assertSame(original, function.injector);
    }
}
