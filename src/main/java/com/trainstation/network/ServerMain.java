package com.trainstation.network;

import java.io.IOException;
import java.util.logging.Logger;

public class ServerMain {
    private static final Logger LOG = Logger.getLogger(ServerMain.class.getName());

    public static void main(String[] args) {
        int port = NetworkConfig.DEFAULT_PORT;
        if (args.length > 0) {
            try { port = Integer.parseInt(args[0]); }
            catch (NumberFormatException e) { LOG.warning("Invalid port: " + args[0]); }
        }
        AppServer server = new AppServer(port);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        try {
            server.start();
        } catch (IOException e) {
            LOG.severe("Cannot start server: " + e.getMessage());
            System.exit(1);
        }
    }
}
