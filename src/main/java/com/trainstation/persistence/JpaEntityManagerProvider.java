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
        props.put(
                "jakarta.persistence.jdbc.url",
                System.getenv().getOrDefault(
                        "DB_URL",
                        "jdbc:mariadb://localhost:3306/QLTauHoa?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh"
                )
        );
        putIfPresent(props, "jakarta.persistence.jdbc.user", "root");
        putIfPresent(props, "jakarta.persistence.jdbc.password", "sapassword");

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

    public static void close() {
        if (ENTITY_MANAGER_FACTORY.isOpen()) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }
}
