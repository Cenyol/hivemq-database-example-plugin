/*
 * Copyright (C) dc-square GmbH - All Rights Reserved
 */

package com.hivemq.example.database;

import com.hivemq.example.database.callbacks.ClientConnect;
import com.hivemq.example.database.callbacks.DBAuthenticationCallback;
import com.hivemq.example.database.callbacks.PersistMessagesCallback;
import com.hivemq.example.database.callbacks.ShutdownCallback;
import com.hivemq.spi.PluginEntryPoint;
import com.hivemq.spi.callback.registry.CallbackRegistry;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class DatabaseExamplePlugin extends PluginEntryPoint {


    private final ClientConnect clientConnect;
    private final PersistMessagesCallback persistMessagesCallback;
    private final ShutdownCallback shutdownCallback;
    private final DBAuthenticationCallback dbAuthenticationCallback;

    @Inject
    public DatabaseExamplePlugin(final ClientConnect clientConnect,
                                 final PersistMessagesCallback persistMessagesCallback,
                                 final ShutdownCallback shutdownCallback,
                                 final DBAuthenticationCallback dbAuthenticationCallback) {
        this.persistMessagesCallback = persistMessagesCallback;
        this.clientConnect = clientConnect;
        this.shutdownCallback = shutdownCallback;
        this.dbAuthenticationCallback = dbAuthenticationCallback;
    }


    @PostConstruct
    public void postConstruct() {

        final CallbackRegistry callbackRegistry = getCallbackRegistry();

        callbackRegistry.addCallback(persistMessagesCallback);
        callbackRegistry.addCallback(clientConnect);
        callbackRegistry.addCallback(shutdownCallback);
        callbackRegistry.addCallback(dbAuthenticationCallback);

    }
}
