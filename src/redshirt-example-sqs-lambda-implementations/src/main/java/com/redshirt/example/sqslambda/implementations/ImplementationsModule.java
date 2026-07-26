package com.redshirt.example.sqslambda.implementations;

import com.google.inject.AbstractModule;
import com.redshirt.example.sqslambda.core.CoreModule;

/**
 * Extension point for AWS clients and other adapters. Installs {@link CoreModule}.
 */
public class ImplementationsModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new CoreModule());
    }
}
