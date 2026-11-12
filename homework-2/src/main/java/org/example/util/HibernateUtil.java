package org.example.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HibernateUtil {
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {}

    private static SessionFactory buildSessionFactory() {
        try {
            SessionFactory factory = new Configuration().configure().buildSessionFactory();
            Runtime.getRuntime().addShutdownHook(new Thread(HibernateUtil::shutdown, "hibernate-shutdown"));
            logger.info("SessionFactory initialized");
            return factory;
        } catch (Throwable ex) {
            logger.error("Initial SessionFactory creation failed", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    /**
     * Закрывает SessionFactory и освобождает пул соединений с БД.
     * Идемпотентен: повторный вызов безопасен.
     */
    public static void shutdown() {
        if (SESSION_FACTORY != null && !SESSION_FACTORY.isClosed()) {
            logger.info("Closing SessionFactory...");
            try {
                SESSION_FACTORY.close();
                logger.info("SessionFactory closed");
            } catch (Exception e) {
                logger.error("Error closing SessionFactory", e);
            }
        }
    }
}