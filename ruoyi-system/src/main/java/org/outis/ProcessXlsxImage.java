package org.outis;

import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.List;

/**
 * 使用 Apache POI 读取 .xlsx 文件中的所有图片并保存到本地
 */
public class ProcessXlsxImage {
    public static void main(String[] args) {
        String excelPath = "my/332818675_5_5.xlsx"; // Excel 文件路径
        String outputDir = "my/image-out/";             // 图片输出目录

        // 推荐：使用 WorkbookFactory.create() 自动管理底层流
        try (FileInputStream fis = new FileInputStream(excelPath);
             XSSFWorkbook workbook = (XSSFWorkbook) WorkbookFactory.create(fis)) {

            // 遍历每个 sheet
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                System.out.println("正在读取Sheet: " + sheet.getSheetName());

                // 遍历 sheet 中所有图片
                for (POIXMLDocumentPart part : sheet.getRelations()) {
                    if (part instanceof XSSFDrawing) {
                        XSSFDrawing drawing = (XSSFDrawing) part;
                        List<XSSFShape> shapes = drawing.getShapes();

                        for (XSSFShape shape : shapes) {
                            if (shape instanceof XSSFPicture) {
                                XSSFPicture picture = (XSSFPicture) shape;
                                XSSFPictureData data = picture.getPictureData();

                                // 图片位置信息
                                XSSFClientAnchor anchor = picture.getPreferredSize();
                                int row1 = anchor.getRow1();
                                int col1 = anchor.getCol1();

                                // 构造输出文件名
                                String ext = data.suggestFileExtension();
                                String fileName = String.format("sheet%d_r%d_c%d.%s", i + 1, row1, col1, ext);
                                File imgFile = new File(outputDir, fileName);
                                imgFile.getParentFile().mkdirs();

                                // 保存图片
                                try (FileOutputStream out = new FileOutputStream(imgFile)) {
                                    IOUtils.copy(new ByteArrayInputStream(data.getData()), out);
                                }

                                System.out.println("保存图片: " + imgFile.getAbsolutePath());
                            }
                        }
                    }
                }
            }

            System.out.println("✅ 所有图片已成功导出到: " + new File(outputDir).getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
