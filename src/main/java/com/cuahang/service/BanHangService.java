package com.cuahang.service;

import com.cuahang.entity.ChiTietHoaDon;
import com.cuahang.entity.HoaDon;
import com.cuahang.entity.KhachHang;
import com.cuahang.entity.NhanVien;
import com.cuahang.entity.SanPham;
import com.cuahang.service.dto.PurchaseItem;
import com.cuahang.util.HibernateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class BanHangService {
    public HoaDon taoHoaDon(String maHD, String maKH, String maNV, List<PurchaseItem> items) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            KhachHang khachHang = maKH == null || maKH.isBlank() ? null : session.get(KhachHang.class, maKH);
            NhanVien nhanVien = maNV == null || maNV.isBlank() ? null : session.get(NhanVien.class, maNV);

            HoaDon hoaDon = new HoaDon();
            hoaDon.setMaHD(maHD);
            hoaDon.setNgayLap(new Date());
            hoaDon.setTrangThai("Complete");
            hoaDon.setKhachHang(khachHang);
            hoaDon.setNhanVien(nhanVien);

            List<ChiTietHoaDon> chiTiets = new ArrayList<>();
            double tongTien = 0.0;

            for (PurchaseItem item : items) {
                if (item == null) continue;
                if (item.soLuong() <= 0) continue;

                SanPham sp = session.get(SanPham.class, item.maSP());
                if (sp == null) {
                    throw new IllegalArgumentException("Không tìm thấy sản phẩm: " + item.maSP());
                }
                if (sp.getSoLuongTon() < item.soLuong()) {
                    throw new IllegalArgumentException(
                        "Tồn kho không đủ cho " + sp.getTenSP() + ". Tồn: " + sp.getSoLuongTon()
                    );
                }

                ChiTietHoaDon ct = new ChiTietHoaDon();
                ct.setHoaDon(hoaDon);
                ct.setSanPham(sp);
                ct.setSoLuong(item.soLuong());
                ct.setDonGia(sp.getGiaBan());

                sp.setSoLuongTon(sp.getSoLuongTon() - item.soLuong());
                session.merge(sp);

                tongTien += (ct.getDonGia() * ct.getSoLuong());
                chiTiets.add(ct);
            }

            hoaDon.setTongTien(tongTien);
            hoaDon.setDanhSachChiTiet(chiTiets);

            session.persist(hoaDon);
            for (ChiTietHoaDon ct : chiTiets) {
                session.persist(ct);
            }

            tx.commit();
            return hoaDon;
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}

