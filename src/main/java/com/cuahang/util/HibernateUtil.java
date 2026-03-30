package com.cuahang.util;

import java.util.Properties;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Tự động đọc file hibernate.cfg.xml cấu hình ở trên
            Configuration cfg = new Configuration().configure();
            Properties props = cfg.getProperties();
            DatabaseBootstrap.ensureDatabaseExists(
                props.getProperty("hibernate.connection.url"),
                props.getProperty("hibernate.connection.username"),
                props.getProperty("hibernate.connection.password")
            );
            return cfg.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Khởi tạo SessionFactory thất bại: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}
