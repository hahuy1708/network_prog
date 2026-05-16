package com.example.util;

import com.example.model.CanBo;
import com.example.model.PhanCong;
import com.example.model.PhongThi;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExcelHandler {

    private static final String SHEET_CAN_BO = "Danh sach can bo";
    private static final String SHEET_PHONG = "DS phong thi";

    public static List<CanBo> docCanBo(String filePath) throws IOException {
        List<CanBo> result = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = new XSSFWorkbook(fis)) {

            Sheet sheet = findSheetByName(wb, SHEET_CAN_BO);
            if (sheet == null && wb.getNumberOfSheets() > 0) {
                sheet = wb.getSheetAt(0);
            }

            if (sheet == null) {
                return result;
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null) {
                    continue;
                }

                int stt = (int) getNumeric(row.getCell(0));
                String maGV = getCellString(row.getCell(1));
                String hoTen = getCellString(row.getCell(2));
                String ngaySinh = getDateString(row.getCell(3));
                String donVi = getCellString(row.getCell(4));

                if (maGV.isEmpty() && hoTen.isEmpty()) {
                    continue;
                }
                result.add(new CanBo(stt, maGV, hoTen, ngaySinh, donVi));
            }
        }
        return result;
    }

    public static List<PhongThi> docPhongThi(String filePath) throws IOException {
        List<PhongThi> result = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = new XSSFWorkbook(fis)) {

            Sheet sheet = findSheetByName(wb, SHEET_PHONG);
            if (sheet == null) {
                if (wb.getNumberOfSheets() > 1) {
                    sheet = wb.getSheetAt(1);
                } else if (wb.getNumberOfSheets() > 0) {
                    sheet = wb.getSheetAt(0);
                }
            }

            if (sheet == null) {
                return result;
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null) {
                    continue;
                }

                int stt = (int) getNumeric(row.getCell(0));
                String maPhong = getCellString(row.getCell(1));
                String diaDiem = getCellString(row.getCell(2));

                if (maPhong.isEmpty()) {
                    continue;
                }
                result.add(new PhongThi(stt, maPhong, diaDiem));
            }
        }
        return result;
    }

    public static void ghiDanhSachPhanCong(List<PhanCong> danhSach, String outputPath)
            throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Phan cong");

        CellStyle headerStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        Row header = sheet.createRow(0);
        String[] cols = {"STT", "Ma GV", "Ho va ten", "Vai tro", "Phong thi"};
        for (int i = 0; i < cols.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(cols[i]);
            cell.setCellStyle(headerStyle);
        }

        for (int i = 0; i < danhSach.size(); i++) {
            PhanCong pc = danhSach.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(pc.getStt());
            row.createCell(1).setCellValue(pc.getCanBo().getMaGV());
            row.createCell(2).setCellValue(pc.getCanBo().getHoTen());
            row.createCell(3).setCellValue("Giam thi " + pc.getViTri());
            row.createCell(4).setCellValue(pc.getPhong().getMaPhong());
        }

        for (int i = 0; i < cols.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            wb.write(fos);
        }
        wb.close();
    }

    public static void ghiDanhSachGiamSat(
            List<CanBo> giamSatList,
            List<PhongThi> phongList,
            String outputPath) throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Giam sat");

        CellStyle headerStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        Row header = sheet.createRow(0);
        String[] cols = {"STT", "Ma GV", "Ho va ten", "Phong thi duoc giam sat"};
        for (int i = 0; i < cols.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(cols[i]);
            cell.setCellStyle(headerStyle);
        }

        int soPhong = phongList.size();
        int soGiamSat = giamSatList.size();

        for (int i = 0; i < soGiamSat; i++) {
            CanBo cb = giamSatList.get(i);

            int phongPerGS = (soGiamSat > 0) ? soPhong / soGiamSat : 0;
            int tuIdx = i * phongPerGS;
            int denIdx = (i == soGiamSat - 1) ? soPhong - 1 : (i + 1) * phongPerGS - 1;

            String phamVi = (phongPerGS > 0 && tuIdx < soPhong)
                    ? String.format("Tu %s den %s",
                    phongList.get(tuIdx).getMaPhong(),
                    phongList.get(Math.min(denIdx, soPhong - 1)).getMaPhong())
                    : "-";

            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(cb.getMaGV());
            row.createCell(2).setCellValue(cb.getHoTen());
            row.createCell(3).setCellValue(phamVi);
        }

        for (int i = 0; i < cols.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            wb.write(fos);
        }
        wb.close();
    }

    private static Sheet findSheetByName(Workbook wb, String expectedName) {
        String expected = normalize(expectedName);
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);
            if (normalize(sheet.getSheetName()).equals(expected)) {
                return sheet;
            }
        }
        return null;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");
        return normalized.toLowerCase(Locale.ROOT).trim();
    }

    private static String getCellString(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> cell.toString().trim();
        };
    }

    private static double getNumeric(Cell cell) {
        if (cell == null) {
            return 0;
        }
        try {
            return cell.getNumericCellValue();
        } catch (Exception e) {
            return 0;
        }
    }

    private static String getDateString(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return new SimpleDateFormat("dd/MM/yyyy").format(cell.getDateCellValue());
        }
        return cell.toString().trim();
    }
}
