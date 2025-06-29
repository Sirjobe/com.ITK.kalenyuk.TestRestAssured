package com.ITK.kalenyuk.utils;

import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelDataProvider {
//    public static Iterator<Object[]> provideLoginData(String excelPath) {
//        List<Object[]> testCases = new ArrayList<>();
//        try (Workbook workbook = WorkbookFactory.create(new File(excelPath))) {
//            Sheet sheet = workbook.getSheetAt(0);
//            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//                Row row = sheet.getRow(i);
//                String username = row.getCell(0).getStringCellValue();
//                String password = row.getCell(1).getStringCellValue();
//                boolean isSuccess = row.getCell(2).getBooleanCellValue();
//                testCases.add(new Object[]{username, password, isSuccess});
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Error reading Excel file", e);
//        }
//        return testCases.iterator();
//    }
public static Iterator<Object[]> provideLoginData(String excelPath) {
    List<Object[]> testCases = new ArrayList<>();
    File file = new File(excelPath);

    if (!file.exists()) {
        throw new RuntimeException("Excel file not found: " + excelPath);
    }

    try (FileInputStream fis = new FileInputStream(file);
         Workbook workbook = WorkbookFactory.create(fis)) {

        Sheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();


        for (int i = 1; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String username = getCellValue(row.getCell(0));
            String password = getCellValue(row.getCell(1));
            boolean expectedResult = Boolean.parseBoolean(getCellValue(row.getCell(2)));

            testCases.add(new Object[]{username, password, expectedResult});
        }
    } catch (Exception e) {
        throw new RuntimeException("Error reading Excel file: " + excelPath, e);
    }
    return testCases.iterator();
}

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula();
            default: return "";
        }
    }
}