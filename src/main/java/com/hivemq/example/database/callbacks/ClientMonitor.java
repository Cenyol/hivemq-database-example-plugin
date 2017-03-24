/*
 * Copyright 2015 dc-square GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hivemq.example.database.callbacks;

import com.google.inject.Provider;
import com.hivemq.spi.callback.CallbackPriority;
import com.hivemq.spi.callback.events.OnConnectCallback;
import com.hivemq.spi.callback.events.OnDisconnectCallback;
import com.hivemq.spi.callback.exception.RefusedConnectionException;
import com.hivemq.spi.callback.lowlevel.OnPingCallback;
import com.hivemq.spi.message.CONNECT;
import com.hivemq.spi.security.ClientData;
import com.hivemq.spi.services.PluginExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class is an acme of a callback, which is invoked every time a new client is
 * successfully authenticated. The callback can be used to execute some custom behavior,
 * which is necessary when a new client connects or to implement a custom logic based
 * on the {@link CONNECT} to refuse the connection throwing a
 * {@link RefusedConnectionException}.
 *
 * @author Christian Götz
 */
public class ClientMonitor implements OnConnectCallback,OnDisconnectCallback,OnPingCallback {

    private static final int CLIENT_STATUS_ONLINE= 10;
    private static final int CLIENT_STATUS_OFFLINE = 0;

    Logger log = LoggerFactory.getLogger(ClientMonitor.class);

    private final Provider<Connection> connectionProvider;
    private final PluginExecutorService pluginExecutorService;

    private static final String SQLStatement = "UPDATE `Users` SET status = ? WHERE username = ?";

    @Inject
    public ClientMonitor(final Provider<Connection> connectionProvider,
                         final PluginExecutorService pluginExecutorService) {
        this.connectionProvider = connectionProvider;
        this.pluginExecutorService = pluginExecutorService;
    }

    /**
     * This is the callback method, which is called by the HiveMQ core, if a client has sent,
     * a {@link CONNECT} Message and was successfully authenticated. In this acme there is only
     * a logging statement, normally the behavior would be implemented in here.
     *
     * @param connect    The {@link CONNECT} message from the client.
     * @param clientData Useful information about the clients authentication state and credentials.
     * @throws RefusedConnectionException This exception should be thrown, if the client is
     *                                    not allowed to connect.
     */
    @Override
    public void onConnect(CONNECT connect, ClientData clientData) throws RefusedConnectionException {
        this.changeUserStatus(clientData, CLIENT_STATUS_ONLINE);
    }

    /**
     * This method is called from the HiveMQ on a client disconnect.
     *
     * @param clientData       Useful information about the clients authentication state and credentials.
     * @param abruptDisconnect When true the connection of the client broke down without a
     *                         {@link com.hivemq.spi.message.DISCONNECT} message and if false then the client
     *                         disconnected properly with a {@link com.hivemq.spi.message.DISCONNECT} message.
     */
    @Override
    public void onDisconnect(ClientData clientData, boolean abruptDisconnect) {
        this.changeUserStatus(clientData, CLIENT_STATUS_OFFLINE);
    }

    private void changeUserStatus(ClientData clientData, final int status){
        final String username = clientData.getUsername().get();
        final Connection connection = connectionProvider.get();

        pluginExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final PreparedStatement preparedStatement = connection.prepareStatement(SQLStatement);
                    preparedStatement.setInt(1, status);
                    preparedStatement.setString(2, username);

                    preparedStatement.execute();
                    preparedStatement.close();
                } catch (SQLException e) {
                    log.error("An error occured while preparing the SQL statement", e);
                } finally {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        log.error("An error occured while giving back a connection to the connection pool");
                    }
                }
            }
        });

    }


    /**
     * The priority is used when more than one OnConnectCallback is implemented to determine the order.
     * If there is only one callback, which implements a certain interface, the priority has no effect.
     *
     * @return callback priority
     */
    @Override
    public int priority() {
        return CallbackPriority.MEDIUM;
    }

    @Override
    public void onPingReceived(ClientData clientData) {
        log.info("received Client {} ping package", clientData.getClientId());
    }
}
