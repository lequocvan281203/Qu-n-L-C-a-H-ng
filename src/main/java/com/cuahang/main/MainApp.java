package com.cuahang.main;

import org.hibernate.Session;
import com.cuahang.util.HibernateUtil;

public class MainApp {
    public static void main(String[] args) {
        System.out.println("Đang thử kết nối đến MySQL...");
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("🎉 KẾT NỐI THÀNH CÔNG RỒI NHÉ!");
        } catch (Exception e) {
            System.out.println("❌ Kết nối thất bại. Lỗi chi tiết:");
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}