package com.songdesy.excel;

import com.songdesy.common.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
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
public class ExcelImportUtil {


    private static final Logger logger = LoggerFactory.getLogger(ExcelImportUtil.class);

    public static <T> JsonResult readExcelData(InputStream is, Class<T> t) throws Exception {
        logger.info("==begin===");
        Workbook wb = HSSFWorkbookFactory.create(is);
        Sheet sheet = wb.getSheetAt(0);
        Row titleRow = sheet.getRow(0);
        List<String> rowList = readRowList(titleRow);
        JsonResult jsonResult = checkColumn(rowList, t);
        if (!jsonResult.getCode().equals("0000")) {
            logger.error("列较验失败！");
            return jsonResult;
        }
        List<T> list = readDataColumn(sheet, rowList, t);
        return JsonResult.instance().success("操作成功！",list);
    }

    private static List<String> readRowList(Row row) {
        List<String> listCellValue = new ArrayList<>();
        for (Cell cell : row) {
            listCellValue.add(cell.getStringCellValue());
        }
        return listCellValue;
    }

    private static <T> List<T> readDataColumn(Sheet sheet, List<String> rowList, Class<T> cls) throws Exception {
        ArrayList<T> list = new ArrayList<>();
        Iterator<Row> iterator = sheet.rowIterator();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            if (row.getRowNum() == 0) {
                continue;
            }
            T t = cls.newInstance();
            Class c = t.getClass();
            Field[] fields = c.getDeclaredFields();
            for (Cell cell : row) {
                String rowName = rowList.get(cell.getColumnIndex());
                Object val = readCellContent(cell);
                for (Field field : fields) {
                    field.setAccessible(true);
                    Annotation annotation = field.getAnnotation(ExcelField.class);
                    if (annotation != null) {
                        String fieldName = ((ExcelField) annotation).filedName();
                        if (fieldName.equals(rowName)) {
                            fieldValue(field, val, t);
                            break;
                        }
                    }
                }
            }
            list.add(t);
        }
        return list;
    }

    private static void fieldValue(Field field, Object o, Object clazz) throws Exception {
        if (null == o || StringUtils.isEmpty(o.toString())) {
            return;
        }
        if (field.getType().equals(String.class)) {
            field.set(clazz, o.toString());
        } else if (field.getType().equals(Integer.class)) {
            field.set(clazz, Integer.valueOf(o.toString()));
        } else if (field.getType().equals(Float.class)) {
            field.set(clazz, Float.valueOf(o.toString()));
        } else if (field.getType().equals(Double.class)) {
            field.set(clazz, Double.valueOf(o.toString()));
        } else if (field.getType().equals(Long.class)) {
            field.set(clazz, Long.valueOf(o.toString()));
        }
    }

    /**
     * 较验列
     *
     * @param listCellValue
     * @param cls
     * @return
     */
    private static <T> JsonResult checkColumn(List<String> listCellValue, Class<T> cls) {
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Annotation annotation = field.getAnnotation(ExcelField.class);
            if (annotation != null) {
                String fieldName = ((ExcelField) annotation).filedName();
                boolean hasField = listCellValue.stream().filter(s -> s.equals(fieldName)).findFirst().isPresent();
                if (!hasField)
                    return JsonResult.instance().error("缺少列[" + fieldName + "]!");
            }
        }
        return JsonResult.instance().success();
    }

    /**
     * 获取单元格的值
     *
     * @param cell
     * @return
     */
    private static Object readCellContent(Cell cell) {
        DataFormatter formatter = new DataFormatter();

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

}
