package com.cuahang.main;

import com.cuahang.ai.AiQueryService;
import com.cuahang.service.BanHangService;
import com.cuahang.service.BootstrapService;
import com.cuahang.service.DemoDataService;
import com.cuahang.service.dto.PurchaseItem;
import com.cuahang.util.HibernateUtil;
import java.util.List;
import java.util.UUID;
import org.hibernate.Session;

public class SmokeDemo {
    public static void main(String[] args) {
        try (Session ignored = HibernateUtil.getSessionFactory().openSession()) {
            new BootstrapService().ensureDefaultAdmin();
            new DemoDataService().ensureDemoData();

            String maHD = "HD_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            new BanHangService().taoHoaDon(maHD, null, null, List.of(new PurchaseItem("SP01", 1)));

            String sql = "SELECT MaSP, TenSP, GiaBan, SoLuongTon FROM SanPham ORDER BY MaSP";
            var result = new AiQueryService().executeSelectQuery(sql);
            System.out.println("SmokeDemo OK. Rows=" + result.rows().size());
        } finally {
            HibernateUtil.shutdown();
        }
    }
}

