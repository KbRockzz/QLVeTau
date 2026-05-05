package com.trainstation.network;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class AppClient {
    private static final Logger LOG = Logger.getLogger(AppClient.class.getName());
    private static volatile AppClient instance;
    private final String host;
    private final int port;

    private AppClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static AppClient getInstance() {
        if (instance == null) {
            synchronized (AppClient.class) {
                if (instance == null) {
                    instance = new AppClient(NetworkConfig.getServerHost(), NetworkConfig.getServerPort());
                }
            }
        }
        return instance;
    }

    public AppResponse sendRequest(AppRequest request) {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(request);
            out.flush();
            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                in.setObjectInputFilter(SerializationFilter.INSTANCE);
                return (AppResponse) in.readObject();
            }
        } catch (IOException e) {
            LOG.severe("Connection error: " + e.getMessage());
            return AppResponse.error("Cannot connect to server: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            LOG.severe("Deserialize error: " + e.getMessage());
            return AppResponse.error("Error reading server response.");
        }
    }

    public boolean isServerReachable() {
        try (Socket socket = new Socket(host, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
