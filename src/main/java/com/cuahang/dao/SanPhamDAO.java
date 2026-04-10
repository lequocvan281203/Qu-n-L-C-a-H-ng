package com.cuahang.dao;

import com.cuahang.entity.SanPham;
import com.cuahang.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;

public class SanPhamDAO extends GenericDAO<SanPham, String> {
    public SanPhamDAO() {
        super(SanPham.class);
    }

    public List<SanPham> search(String keyword) {
        String kw = keyword != null ? keyword.trim() : "";
        if (kw.isBlank()) return findAll();
        String like = "%" + kw + "%";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                .createQuery(
                    "from SanPham sp where sp.maSP like :kw or sp.tenSP like :kw order by sp.maSP",
                    SanPham.class
                )
                .setParameter("kw", like)
                .getResultList();
        }
    }
}
