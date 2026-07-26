package com.redshirt.example.sqslambda.core;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class CoreModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(MessageHandler.class).to(DefaultMessageHandler.class).in(Scopes.SINGLETON);
    }
}
