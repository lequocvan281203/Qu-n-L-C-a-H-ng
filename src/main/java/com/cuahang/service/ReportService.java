package com.cuahang.service;

import com.cuahang.entity.HoaDon;
import com.cuahang.entity.SanPham;
import com.cuahang.util.HibernateUtil;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.hibernate.Session;

public class ReportService {
    public List<SanPham> searchSanPham(String keyword) {
        String kw = keyword == null ? "" : keyword.trim();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                .createQuery(
                    "from SanPham sp where sp.maSP like :kw or sp.tenSP like :kw order by sp.maSP",
                    SanPham.class
                )
                .setParameter("kw", "%" + kw + "%")
                .getResultList();
        }
    }

    public List<Object[]> tonKhoThap(int threshold) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                .createQuery(
                    "select sp.maSP, sp.tenSP, sp.soLuongTon from SanPham sp where sp.soLuongTon <= :t order by sp.soLuongTon asc",
                    Object[].class
                )
                .setParameter("t", threshold)
                .getResultList();
        }
    }

    public List<Object[]> doanhThuTheoNgay(LocalDate fromInclusive, LocalDate toInclusive) {
        Date from = Date.from(fromInclusive.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date to = Date.from(toInclusive.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                .createQuery(
                    "select function('date', hd.ngayLap), sum(hd.tongTien) " +
                        "from HoaDon hd " +
                        "where hd.trangThai = 'Complete' and hd.ngayLap >= :from and hd.ngayLap < :to " +
                        "group by function('date', hd.ngayLap) " +
                        "order by function('date', hd.ngayLap)",
                    Object[].class
                )
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
        }
    }

    public List<Object[]> topSanPhamBanChay(int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                .createQuery(
                    "select sp.maSP, sp.tenSP, sum(ct.soLuong) " +
                        "from ChiTietHoaDon ct join ct.sanPham sp join ct.hoaDon hd " +
                        "where hd.trangThai = 'Complete' " +
                        "group by sp.maSP, sp.tenSP " +
                        "order by sum(ct.soLuong) desc",
                    Object[].class
                )
                .setMaxResults(limit)
                .getResultList();
        }
    }

    public List<HoaDon> timHoaDonTheoMa(String maHDLike) {
        String kw = maHDLike == null ? "" : maHDLike.trim();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                .createQuery(
                    "from HoaDon hd where hd.maHD like :kw order by hd.ngayLap desc",
                    HoaDon.class
                )
                .setParameter("kw", "%" + kw + "%")
                .getResultList();
        }
    }
}

