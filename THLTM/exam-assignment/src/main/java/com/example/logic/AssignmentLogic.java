package com.example.logic;

import com.example.model.AssignmentResult;
import com.example.model.CanBo;
import com.example.model.PhanCong;
import com.example.model.PhongThi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AssignmentLogic {

    public static AssignmentResult phanCong(
            List<CanBo> canBoList,
            List<PhongThi> phongList,
            Map<String, List<Set<String>>> lichSu,
            int soThuTuCa) {

        if (soThuTuCa == 1) {
            return phanCongCaDau(canBoList, phongList);
        }
        return phanCongCaTiepTheo(canBoList, phongList, lichSu);
    }

    private static AssignmentResult phanCongCaDau(
            List<CanBo> canBoList, List<PhongThi> phongList) {

        List<PhanCong> danhSachPC = new ArrayList<>();
        int n = phongList.size();
        int m = canBoList.size();

        for (int i = 0; i < n; i++) {
            int idx1 = i * 2;
            int idx2 = i * 2 + 1;
            if (idx1 >= m || idx2 >= m) {
                break;
            }

            PhongThi phong = phongList.get(i);
            CanBo gt1 = canBoList.get(idx1);
            CanBo gt2 = canBoList.get(idx2);

            danhSachPC.add(new PhanCong(idx1 + 1, gt1, phong, 1, 1));
            danhSachPC.add(new PhanCong(idx2 + 1, gt2, phong, 2, 1));
        }

        List<CanBo> giamSat = new ArrayList<>();
        for (int i = n * 2; i < m; i++) {
            giamSat.add(canBoList.get(i));
        }

        return new AssignmentResult(danhSachPC, giamSat, phongList);
    }

    private static AssignmentResult phanCongCaTiepTheo(
            List<CanBo> canBoList,
            List<PhongThi> phongList,
            Map<String, List<Set<String>>> lichSu) {

        List<PhanCong> danhSachPC = new ArrayList<>();
        Set<String> daSuDungTrongCa = new HashSet<>();

        for (PhongThi phong : phongList) {
            String maPhong = phong.getMaPhong();
            List<Set<String>> lichSuPhong = lichSu.getOrDefault(maPhong, new ArrayList<>());

            Set<String> daCoiPhong = new HashSet<>();
            for (Set<String> cap : lichSuPhong) {
                daCoiPhong.addAll(cap);
            }

            CanBo gt1 = null;
            CanBo gt2 = null;

            for (CanBo cb : canBoList) {
                if (daSuDungTrongCa.contains(cb.getMaGV())) {
                    continue;
                }
                if (daCoiPhong.contains(cb.getMaGV())) {
                    continue;
                }

                if (gt1 == null) {
                    gt1 = cb;
                    continue;
                }

                Set<String> capThu = new HashSet<>(Arrays.asList(gt1.getMaGV(), cb.getMaGV()));
                boolean laCapCu = lichSuPhong.stream().anyMatch(cap -> cap.equals(capThu));
                if (!laCapCu) {
                    gt2 = cb;
                    break;
                }
            }

            if (gt1 != null && gt2 != null) {
                int stt = danhSachPC.size();
                int soThuTuCa = lichSuPhong.size() + 1;
                danhSachPC.add(new PhanCong(stt + 1, gt1, phong, 1, soThuTuCa));
                danhSachPC.add(new PhanCong(stt + 2, gt2, phong, 2, soThuTuCa));
                daSuDungTrongCa.add(gt1.getMaGV());
                daSuDungTrongCa.add(gt2.getMaGV());
            } else {
                System.err.println("Canh bao: khong du GV hop le cho phong " + maPhong);
            }
        }

        List<CanBo> giamSat = canBoList.stream()
                .filter(cb -> !daSuDungTrongCa.contains(cb.getMaGV()))
                .collect(Collectors.toList());

        return new AssignmentResult(danhSachPC, giamSat, phongList);
    }
}
