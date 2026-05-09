package com.trainstation;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.gui.FrmDangNhap;
import com.trainstation.network.AppServer;
import com.trainstation.network.NetworkConfig;
import com.trainstation.util.DataInitializer;

import javax.swing.*;
import java.io.IOException;
import java.util.logging.Logger;

// Main application
public class MainApplication {
    private static final Logger LOG = Logger.getLogger(MainApplication.class.getName());

    public static void main(String[] args) {
        // Parse command-line arguments
        String host = NetworkConfig.DEFAULT_HOST;
        int port = NetworkConfig.DEFAULT_PORT;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--server":
                    if (i + 1 < args.length) {
                        try { port = Integer.parseInt(args[++i]); } catch (NumberFormatException ignored) {}
                    }
                    startServer(port);
                    return;
                case "--client":
                    if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                        String[] hostPort = args[++i].split(":");
                        host = hostPort[0];
                        if (hostPort.length > 1) {
                            try { port = Integer.parseInt(hostPort[1]); } catch (NumberFormatException ignored) {}
                        }
                    }
                    launchClientMode(host, port);
                    return;
                default:
                    break;
            }
        }

        launchUi();
    }

    /** Configure and start GUI in client mode with remote server host/port. */
    public static void launchClientMode(String host, int port) {
        NetworkConfig.setClientMode(true);
        NetworkConfig.setServerHost(host);
        NetworkConfig.setServerPort(port);
        launchUi();
    }

    private static void launchUi() {
        // Khởi tạo Material Professional Light theme trước khi tạo UI
        MaterialInitializer.initUI();

        // Tải dữ liệu mẫu (only in local/server mode)
        if (!NetworkConfig.isClientMode()) {
            DataInitializer.initializeSampleData();
        }

        // Hiển thị form đăng nhập
        SwingUtilities.invokeLater(() -> {
            FrmDangNhap frmDangNhap = new FrmDangNhap();
            frmDangNhap.setVisible(true);
        });
    }

    private static void startServer(int port) {
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
