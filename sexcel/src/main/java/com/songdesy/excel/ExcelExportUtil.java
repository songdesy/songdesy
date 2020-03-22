package com.songdesy.excel;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 少   年   辛   苦   终   身   事
 * 莫   向   光   阴   惰   寸   功
 * Today the best performance  as tomorrow newest starter!
 * Created by IntelliJ IDEA.
 *
 * @author : songsong.wu
 * github: https://github.com/songdesy
 * email: callwss@qq.com
 * <p>
 * Date: 2018/8/2 下午6:44
 * Description:
 * Copyright(©) 2018/8/2 by songsong.wu.
 **/
public class ExcelExportUtil {


    private static final Logger logger = LoggerFactory.getLogger(ExcelExportUtil.class);

    public static void exportExcel(List list, Class clz, String sheetName, OutputStream os) throws Exception {
        Workbook wb = createWorkbook();

        CellStyle cellStyle = createCellStyle(wb);
        Font font = wb.createFont();
        Sheet sheet = createSheet(wb, sheetName);

        if (CollectionUtils.isNotEmpty(list)) {
            int rowIndex = 1;
            for (Object o : list) {
                Row row = sheet.createRow(rowIndex);
                int cellIndex = 0;
//                Class cls = o.getClass();
                Field[] fields = clz.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    Annotation annotation = field.getAnnotation(ExcelField.class);
                    if (annotation != null) {
                        Object value = fieldValue(field, o);
                        if (value != null) {
                            createCell(value, cellStyle, row, cellIndex, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, font);
                        }
                        cellIndex++;
                    }

                }
                rowIndex++;
            }
        }

        createTitle(clz, wb, sheet);
        wb.write(os);

    }

    private static void createTitle(Class cls, Workbook wb, Sheet sheet) {
        Row row = sheet.createRow(0);
        int cellIndex = 0;
        Field[] fields = cls.getDeclaredFields();
        Font font = wb.createFont();
        CellStyle cellStyle = createCellStyle(wb);
        font.setFontHeightInPoints((short) 12);
        font.setFontName("楷体");
        font.setBold(true);

        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(ExcelField.class);
            if (annotation != null) {
                createCell(((ExcelField) annotation).filedName(), cellStyle, row, cellIndex, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, font);
                cellIndex++;
            }
        }

    }

    private static Object fieldValue(Field field, Object o) {
        try {
            return field.get(o);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    private static void saveFile(Workbook wb) {
        try (OutputStream fileOut = new FileOutputStream("/Users/wss/Downloads/excelImport.xls")) {
            wb.write(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createCell(Object value, CellStyle cellStyle, Row row, int column, HorizontalAlignment halign, VerticalAlignment valign, Font font) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value.toString());
        cellStyle.setAlignment(halign);
        cellStyle.setVerticalAlignment(valign);
        cellStyle.setFont(font);
        cell.setCellStyle(cellStyle);
    }

    private static Workbook createWorkbook() {
        Workbook wb = new HSSFWorkbook();
        return wb;
    }

    private static CellStyle createCellStyle(Workbook wb) {
        return wb.createCellStyle();
    }

    private static Sheet createSheet(Workbook wb, String sheetName) {
        Sheet sheet1 = wb.createSheet(sheetName);
        return sheet1;
    }

//    public static void main(String[] args) {
//        SysTechnology sysTechnology = new SysTechnology();
//        sysTechnology.setTechnologyCode("hello");
//        sysTechnology.setAxDtSt((float) 10);
//        sysTechnology.setRemarks("谢谢");
//        List list = new ArrayList();
//        list.add(sysTechnology);
//        ExcelExportUtil.exportExcel(list, "第一个测试", "第一个工作");

//    }
}
