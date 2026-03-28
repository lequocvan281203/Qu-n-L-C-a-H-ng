package com.cuahang.service;

import com.cuahang.entity.TaiKhoan;
import com.cuahang.util.HibernateUtil;
import java.util.Optional;
import org.hibernate.Session;

public class AuthService {
    public Optional<TaiKhoan> login(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                .createQuery(
                    "from TaiKhoan tk where tk.username = :u and tk.password = :p",
                    TaiKhoan.class
                )
                .setParameter("u", username)
                .setParameter("p", password)
                .uniqueResultOptional();
        }
    }
}

