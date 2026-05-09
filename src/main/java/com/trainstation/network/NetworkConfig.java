package com.trainstation.network;

public class NetworkConfig {
    public static final int DEFAULT_PORT = 9999;
    public static final String DEFAULT_HOST = "localhost";

    private static volatile boolean clientMode = false;
    private static String serverHost = DEFAULT_HOST;
    private static int serverPort = DEFAULT_PORT;

    public static boolean isClientMode() { return clientMode; }
    public static void setClientMode(boolean mode) { clientMode = mode; }
    public static String getServerHost() { return serverHost; }
    public static void setServerHost(String host) { serverHost = host; }
    public static int getServerPort() { return serverPort; }
    public static void setServerPort(int port) { serverPort = port; }
}
