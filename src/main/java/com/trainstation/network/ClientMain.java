package com.trainstation.network;

import java.util.logging.Logger;

public class ClientMain {
    private static final Logger LOG = Logger.getLogger(ClientMain.class.getName());

    public static void main(String[] args) {
        String host = NetworkConfig.DEFAULT_HOST;
        int port = NetworkConfig.DEFAULT_PORT;

        if (args.length > 0 && !args[0].isBlank()) {
            host = args[0];
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                LOG.warning("Invalid port: " + args[1] + ", fallback to " + port);
            }
        }

        NetworkConfig.setClientMode(true);
        NetworkConfig.setServerHost(host);
        NetworkConfig.setServerPort(port);

        AppClient client = AppClient.getInstance();
        if (client.isServerReachable()) {
            LOG.info("Connected to server " + host + ":" + port);
        } else {
            LOG.severe("Cannot connect to server " + host + ":" + port);
            System.exit(1);
        }
    }
}
