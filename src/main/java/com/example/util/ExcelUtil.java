package com.example.util;


import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.poi.ss.usermodel.CellType.BLANK;

public class ExcelUtil {

    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";


    /**
     * 读取后缀为.xlsx的Excel文件（只读单个sheet表）
     * @param inputStream 文件流 ---NOT NULL
     * @param startRowNum 开始读取行数 ---NOT NULL
     * @return
     */
    public static List<List<String>> readXlsxData(InputStream inputStream, int startRowNum){
        XSSFWorkbook workbook= null;
        XSSFSheet sheet = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        List<List<String>> dataList = null;
        try {
            workbook = new XSSFWorkbook(inputStream);
            //读取第一个sheet表
            sheet = workbook.getSheetAt(0);
            if(sheet != null){
                dataList = new ArrayList<>();
                //读取每一行数据
                for (int i = startRowNum; i <= sheet.getLastRowNum() ; i++) {
                    row = sheet.getRow(i);
                    if (row != null) {
                        List<String> rowCellList = new ArrayList<>();
                        //读取每个单元格数据
                        for (int j = 0; j < row.getLastCellNum(); j++) {
                            cell = row.getCell(j);
                            if (cell != null) {
                                switch(cell.getCellType()){
//                                    case XSSFCell.CELL_TYPE_STRING:
                                    case STRING:
                                        rowCellList.add(cell.getStringCellValue().trim());
                                        break;
//                                    case XSSFCell.CELL_TYPE_NUMERIC:
                                    case NUMERIC:
                                        if (DateUtil.isCellDateFormatted(cell)) {
                                            Date date = cell.getDateCellValue();
                                            if (date != null) {
                                                rowCellList.add(DateFormatUtils.format(cell.getDateCellValue(),"yyyy-MM-dd"));
                                            } else {
                                                rowCellList.add("");
                                            }
                                        } else {
                                            rowCellList.add(String.valueOf(cell.getNumericCellValue()));
                                        }
                                        break;
                                    default:
                                        rowCellList.add("");
                                        break;
                                }
                            } else {
                                rowCellList.add("");
                            }
                        }
                        dataList.add(rowCellList);
                    }else{
                        dataList.add(new ArrayList<>());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    /**
     * 读取后缀为.xls的Excel文件（只读单个sheet表）
     * @param inputStream 文件流 ---NOT NULL
     * @param startRowNum 开始读取行数 ---NOT NULL
     * @return
     */
    public static List<List<String>> readXlsData(InputStream inputStream, int startRowNum){
        HSSFWorkbook workbook= null;
        HSSFSheet sheet = null;
        HSSFRow row = null;
        HSSFCell cell = null;
        List<List<String>> dataList = null;
        try {
            workbook = new HSSFWorkbook(inputStream);
            //读取第一个sheet表
            sheet = workbook.getSheetAt(0);
            if(sheet != null){
                dataList = new ArrayList<>();
                //读取每一行数据
                for (int i = startRowNum; i <= sheet.getLastRowNum() ; i++) {
                    row = sheet.getRow(i);
                    if (row != null) {
                        List<String> rowCellList = new ArrayList<>();
                        //读取每个单元格数据
                        for (int j = 0; j < row.getLastCellNum(); j++) {
                            cell = row.getCell(j);
                            if (cell != null) {
                                switch(cell.getCellType()){
//                                    case HSSFCell.CELL_TYPE_STRING:
                                    case STRING:
                                        rowCellList.add(cell.getStringCellValue().trim());
                                        break;
//                                    case HSSFCell.CELL_TYPE_NUMERIC:
                                    case NUMERIC:
                                        if (DateUtil.isCellDateFormatted(cell)) {
                                            Date date = cell.getDateCellValue();
                                            if (date != null) {
                                                rowCellList.add(DateFormatUtils.format(cell.getDateCellValue(),"yyyy-MM-dd"));
                                            } else {
                                                rowCellList.add("");
                                            }
                                        } else {
                                            rowCellList.add(String.valueOf(cell.getNumericCellValue()));
                                        }
                                        break;
                                    default:
                                        rowCellList.add("");
                                        break;
                                }
                            } else {
                                rowCellList.add("");
                            }
                        }
                        dataList.add(rowCellList);
                    }else{
                        dataList.add(new ArrayList<>());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }


    /**
     * 处理excel 空白行
     * @param dataList
     * @return
     */
    public static List<List<String>> dealBlankLine(List<List<String>> dataList){
        //excel空行 index
        List<Integer> blankIndexList = new ArrayList<>();
        for(int i=dataList.size()-1; i>0; i--){
            boolean isBlank = true;
            for(int j= 0; j<dataList.get(i).size(); j++){
                if(StringUtils.hasText(dataList.get(i).get(j))){
                    isBlank = false;
                    break;
                }
            }
            if(isBlank){
                blankIndexList.add(i);
            }else{
                break;
            }
        }

        //组装新数据集
        List<List<String>> newDataList = new ArrayList<>();
        for(int i=0; i<dataList.size(); i++){
            if(!blankIndexList.contains(i)){
                newDataList.add(dataList.get(i));
            }
        }
        return newDataList;
    }


    /**
     * 判断Excel的版本,获取Workbook
     * @param file
     * @return
     * @throws IOException
     */
    public static Workbook getWorkbook(File file) throws IOException{
        Workbook wb = null;
        FileInputStream in = new FileInputStream(file);
        if(file.getName().endsWith(EXCEL_XLS)){     //Excel&nbsp;2003
            wb = new HSSFWorkbook(in);
        }else if(file.getName().endsWith(EXCEL_XLSX)){    // Excel 2007/2010
            wb = new XSSFWorkbook(in);
        }
        return wb;
    }

    /**
     * 将数据写入excel
     * @param dataList 数据集 ---NOT NULL
     * @param startRowNum 开始写入行数 ---NOT NULL
     * @param columnCount 数据列数 ---NOT NULL
     * @param columnCount 列宽 ---NOT NULL
     * @return
     */
    public static Workbook writeExcel(List<List<String>> dataList, int startRowNum, int columnCount, int columnWidth) {
        try {
            Workbook workBook =  new XSSFWorkbook();
            // 创建sheet
            Sheet sheet = workBook.createSheet("sheet1");
            //设置列宽
            for(int i=0; i<columnCount; i++){
                sheet.setColumnWidth(i, columnWidth);
            }
            // 往Excel中写新数据
            for (int j = 0; j < dataList.size(); j++) {
                // 创建一行：从第startRowNum行开始
                Row row = sheet.createRow(startRowNum + j);
                // 得到要插入的每一条记录
                for (int k = 0; k < dataList.get(j).size(); k++) {
                    // 在一行内循环
                    Cell first = row.createCell(k);
                    first.setCellValue(dataList.get(j).get(k));

                }
            }
            return workBook;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断是否为空白行
     * @param row
     * @return
     */
    public static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != BLANK)
                return false;
        }
        return true;
    }

    /**
     * 创建字体
     * @param workbook excel对象
     * @param fontName 字体风格 例如:"宋体"
     * @param fontSize 字体大小
     * @param boldWeight 字体粗细程度
     * @param color 颜色
     * @return
     */
//    public static Font createFont(XSSFWorkbook workbook, String fontName, Short fontSize,  Short boldWeight, Short color){
//        Font font = workbook.createFont();
//        font.setFontName(fontName);
//        font.setFontHeightInPoints(fontSize);
//        font.setBoldweight(boldWeight);
//        font.setColor(color);
//        return font;
//    }

    /**
     * 创建单元格样式
     * @param workbook excel对象
     * @param font 字体
     * @param alignment 水平对齐模式
     * @param verticalAlignment 垂直对齐模式
     * @param isBorder 是否有边框
     * @param fillPattern 填充模式
     * @param fillForeGroundColor  填充背景色
     * @return
     */
//    public static CellStyle createCellStyle(XSSFWorkbook workbook, Font font, Short alignment, Short verticalAlignment,
//                                      Boolean isBorder, Short fillPattern, Short fillForeGroundColor){
//
//
//
//        CellStyle cellStyle = workbook.createCellStyle();
//        cellStyle.setFont(font);
//        cellStyle.setAlignment(alignment);
//        cellStyle.setVerticalAlignment(verticalAlignment);
//        if(isBorder){
//            cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
//            cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
//            cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
//            cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
//        }
//        if(fillPattern != null){
//            cellStyle.setFillPattern(fillPattern);
//        }
//        if(fillForeGroundColor != null){
//            cellStyle.setFillForegroundColor(fillForeGroundColor);
//        }
//        cellStyle.setLocked(true);
//        cellStyle.setWrapText(false);// 自动换行
//        return cellStyle;
//    }
}
