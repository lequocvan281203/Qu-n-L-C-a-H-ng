package com.cuahang.service;

import com.cuahang.entity.ChiTietHoaDon;
import com.cuahang.entity.HoaDon;
import com.cuahang.entity.KhachHang;
import com.cuahang.entity.NhanVien;
import com.cuahang.util.HibernateUtil;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HoaDonService {
    public List<HoaDon> search(String keyword) {
        String kw = keyword == null ? "" : keyword.trim();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                .createQuery(
                    "from HoaDon hd where hd.maHD like :kw or hd.trangThai like :kw order by hd.ngayLap desc",
                    HoaDon.class
                )
                .setParameter("kw", "%" + kw + "%")
                .getResultList();
        }
    }

    public Optional<HoaDon> findById(String maHD) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(HoaDon.class, maHD));
        }
    }

    public List<KhachHang> listKhachHang() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from KhachHang kh order by kh.maKH", KhachHang.class).getResultList();
        }
    }

    public List<NhanVien> listNhanVien() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from NhanVien nv order by nv.maNV", NhanVien.class).getResultList();
        }
    }

    public void saveOrUpdate(HoaDon hd) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(hd);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void deleteById(String maHD) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            HoaDon found = session.get(HoaDon.class, maHD);
            if (found != null) session.remove(found);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void recalcTongTien(String maHD) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            HoaDon hd = session.get(HoaDon.class, maHD);
            if (hd == null) {
                tx.commit();
                return;
            }

            Double total = session
                .createQuery(
                    "select sum(ct.soLuong * ct.donGia) from ChiTietHoaDon ct where ct.hoaDon.maHD = :maHD",
                    Double.class
                )
                .setParameter("maHD", maHD)
                .getSingleResult();

            hd.setTongTien(total != null ? total : 0.0);
            session.merge(hd);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}

