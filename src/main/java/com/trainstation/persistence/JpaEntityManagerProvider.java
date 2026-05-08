package com.trainstation.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public final class JpaEntityManagerProvider {
    private static final String PERSISTENCE_UNIT_NAME = "qlvetau-pu";
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = createFactory();

    private JpaEntityManagerProvider() {
    }

    private static EntityManagerFactory createFactory() {
        Map<String, Object> props = new HashMap<>();
        putIfPresent(props, "jakarta.persistence.jdbc.url", "DB_URL");
        putIfPresent(props, "jakarta.persistence.jdbc.user", "DB_USERNAME");
        putIfPresent(props, "jakarta.persistence.jdbc.password", "DB_PASSWORD");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, props);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (emf.isOpen()) {
                emf.close();
            }
        }));
        return emf;
    }

    private static void putIfPresent(Map<String, Object> properties, String key, String envKey) {
        String value = System.getenv(envKey);
        if (value != null && !value.isBlank()) {
            properties.put(key, value);
        }
    }

    public static EntityManager createEntityManager() {
        return ENTITY_MANAGER_FACTORY.createEntityManager();
    }
}
