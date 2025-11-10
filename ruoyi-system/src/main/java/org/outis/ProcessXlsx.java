package org.outis;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.IOUtils;

import java.io.*;
import java.util.List;
import java.util.Map;


public class ProcessXlsx {
    public static void main(String[] args) {
        String filePath = "my/332818675_5_5.xlsx"; // Excel 文件路径

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // 获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);

            // 遍历每一行
            for (Row row : sheet) {
                // 遍历每个单元格
                for (Cell cell : row) {
                    // 统一将不同类型的单元格转为字符串输出
                    cell.setCellType(CellType.STRING);
                    System.out.print(cell.getStringCellValue() + "\t");
                }
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
