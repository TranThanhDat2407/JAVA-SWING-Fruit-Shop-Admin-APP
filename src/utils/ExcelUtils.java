/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.collections4.ListValuedMap;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import java.io.FileOutputStream;

/**
 *
 * @author THANHDAT
 */
public class ExcelUtils {
    
      public static void exportToExcel(JTable table, String filePath) throws Exception {
        TableModel model = table.getModel();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row;
        Cell cell;

        row = sheet.createRow(0);
        for (int c = 0; c < model.getColumnCount(); c++) {
            cell = row.createCell(c);
            cell.setCellValue(model.getColumnName(c));
        }

        for (int r = 0; r < model.getRowCount(); r++) {
            row = sheet.createRow(r + 1);
            for (int c = 0; c < model.getColumnCount(); c++) {
                cell = row.createCell(c);
                Object value = model.getValueAt(r, c);
                if (value instanceof String) {
                    cell.setCellValue((String) value);
                } else if (value instanceof Double) {
                    cell.setCellValue((Double) value);
                }else if (value instanceof Integer) {
                    cell.setCellValue((Integer) value);
                }
            }
        }

        try (FileOutputStream out = new FileOutputStream(filePath)) {
            workbook.write(out);
        }
    }
}
