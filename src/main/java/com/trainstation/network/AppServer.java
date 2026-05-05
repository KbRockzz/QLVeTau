package com.trainstation.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class AppServer {
    private static final Logger LOG = Logger.getLogger(AppServer.class.getName());
    private final int port;
    private final ExecutorService threadPool;
    private volatile boolean running = false;
    private ServerSocket serverSocket;

    public AppServer(int port) {
        this.port = port;
        this.threadPool = Executors.newCachedThreadPool();
    }

    public void start() throws IOException {
        running = true;
        serverSocket = new ServerSocket(port);
        LOG.info("=== Server started on port " + port + " ===");
        try {
            while (running) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            if (running) { throw e; }
        } finally {
            threadPool.shutdown();
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (IOException e) {
            LOG.warning("Error stopping server: " + e.getMessage());
        }
        threadPool.shutdown();
    }
}
