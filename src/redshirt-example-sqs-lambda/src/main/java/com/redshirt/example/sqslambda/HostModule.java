package com.redshirt.example.sqslambda;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

class HostModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Handler.class).in(Scopes.SINGLETON);
        bind(SafeRecordHandler.class).to(DefaultSafeRecordHandler.class).in(Scopes.SINGLETON);
    }
}
