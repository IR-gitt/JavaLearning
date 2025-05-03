package com.onlyOneMethod;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class OperationsWithExel {
    // Метод для вывода массива чисел из столбца таблицы
    public int[] readNumbersFromExcel(String filePath) {
        int columnIndex = 0; // Индекс столбца с числами
        // Создаем массив фиксированного размера
        int[] numbers = new int[0];

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            // выводит также используемые строки
            int rowCount = sheet.getLastRowNum()+1;

            numbers = new int[rowCount];

            for (int i = 0; i < rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell cell = row.getCell(columnIndex);
                    if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                        numbers[i] = (int) cell.getNumericCellValue();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return numbers;
    }
}
