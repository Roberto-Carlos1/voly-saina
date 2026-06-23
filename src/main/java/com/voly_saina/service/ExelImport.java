package com.voly_saina.service;

import com.voly_saina.entity.Machine;
import com.voly_saina.entity.TypeMachine;

import jakarta.persistence.GeneratedValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.annotations.CreationTimestamp;

public class ExelImport {

    // genralisation de l'importation des données à partir d'un fichier Excel

    public List<Machine> importDataFromExcel(String filePath) {
        try {

        } catch (Exception e) {
            // no thing
        }

        return null; // Implémentation de l'importation des données à partir d'un fichier Excel
    }

    public void generateMachineTemplate(OutputStream outputStream) throws IOException {
        // 1. Create a new empty .xlsx workbook and sheet
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Machines");

            // 2. Create the first row for headers
            Row headerRow = sheet.createRow(0);
            int columnIndex = 0;

            // 3. Inspect fields of the Machine entity dynamically
            for (Field field : Machine.class.getDeclaredFields()) {

                // Skip system-generated fields
                if (field.isAnnotationPresent(GeneratedValue.class) ||
                        field.isAnnotationPresent(CreationTimestamp.class)) {
                    continue;
                }

                // Skip relational fields for now, or fields like Collection/List
                if (field.getType().isInterface()) {
                    continue;
                }

                // 4. Create a cell and write the exact Java field name
                Cell cell = headerRow.createCell(columnIndex++);
                cell.setCellValue(field.getName());
            }

            // 5. Write the compiled Excel structure to the output stream
            workbook.write(outputStream);
        }
    }

    // Pass in the list of available machine types from your database
    public void generateTemplateWithDropdowns(OutputStream outputStream, List<String> availableTypes)
            throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {

            // --- SHEET 2: THE RELATION DATA SOURCE ---
            Sheet typeSheet = workbook.createSheet("TypeMachine_Data");
            for (int i = 0; i < availableTypes.size(); i++) {
                Row row = typeSheet.createRow(i);
                row.createCell(0).setCellValue(availableTypes.get(i));
            }
            // Hide this sheet if you want a clean UI for the user
            // workbook.setSheetHidden(workbook.getSheetIndex(typeSheet), true);

            // --- SHEET 1: THE MAIN ENTITY SHEET ---
            Sheet mainSheet = workbook.createSheet("Machines");
            Row headerRow = mainSheet.createRow(0);
            int columnIndex = 0;
            Integer typeMachineColumnIndex = null;

            for (Field field : Machine.class.getDeclaredFields()) {
                if (field.isAnnotationPresent(GeneratedValue.class) ||
                        field.isAnnotationPresent(CreationTimestamp.class)) {
                    continue;
                }

                Cell cell = headerRow.createCell(columnIndex);
                cell.setCellValue(field.getName());

                // Detect if the field is a complex relationship (TypeMachine entity)
                if (field.getType() == TypeMachine.class) {
                    typeMachineColumnIndex = columnIndex;
                }
                columnIndex++;
            }

            // --- ADD THE DROPDOWN VALIDATION TO SHEET 1 ---
            if (typeMachineColumnIndex != null && !availableTypes.isEmpty()) {
                DataValidationHelper validationHelper = mainSheet.getDataValidationHelper();

                // Reference the list from the second sheet dynamically:
                // TypeMachine_Data!$A$1:$A$4
                String formula = "TypeMachine_Data!$A$1:$A$" + availableTypes.size();
                DataValidationConstraint constraint = validationHelper.createFormulaListConstraint(formula);

                // Apply this validation to column index where 'typeMachine' is, from row 1 to
                // 1000
                CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, typeMachineColumnIndex,
                        typeMachineColumnIndex);
                DataValidation validation = validationHelper.createValidation(constraint, addressList);

                // Ensure user must select from list
                validation.setSuppressDropDownArrow(true);
                validation.setShowErrorBox(true);

                mainSheet.addValidationData(validation);
            }

            workbook.write(outputStream);
        }
    }
    public List<Machine> importMachines(InputStream inputStream, Map<String, TypeMachine> typeCache) throws Exception {
        List<Machine> resultList = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheet("Machines");
            Row headerRow = sheet.getRow(0);
            
            // 1. Map header names to column indices
            Map<String, Integer> headerMap = new HashMap<>();
            for (Cell cell : headerRow) {
                headerMap.put(cell.getStringCellValue(), cell.getColumnIndex());
            }

            // 2. Loop through each data row
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                Machine targetEntity = new Machine();

                // 3. Populate fields via Reflection
                for (Field field : Machine.class.getDeclaredFields()) {
                    String fieldName = field.getName();

                    if (headerMap.containsKey(fieldName)) {
                        int colIndex = headerMap.get(fieldName);
                        Cell cell = row.getCell(colIndex);
                        if (cell == null || cell.getCellType() == CellType.BLANK) continue;

                        field.setAccessible(true);
                        Class<?> fieldType = field.getType();

                        // Dynamic Type Binding
                        if (fieldType == String.class) {
                            field.set(targetEntity, cell.getStringCellValue());
                        } 
                        else if (fieldType == BigDecimal.class) {
                            field.set(targetEntity, BigDecimal.valueOf(cell.getNumericCellValue()));
                        } 
                        else if (fieldType == Boolean.class || fieldType == boolean.class) {
                            field.set(targetEntity, cell.getBooleanCellValue());
                        } 
                        else if (fieldType == Double.class || fieldType == double.class) {
                            field.set(targetEntity, cell.getNumericCellValue());
                        } 
                        // DYNAMIC RELATIONSHIP RESOLUTION
                        else if (fieldType == TypeMachine.class) {
                            String dropdownValue = cell.getStringCellValue();
                            // Retrieve the actual entity from the cache using the text key
                            TypeMachine relationalEntity = typeCache.get(dropdownValue.toLowerCase().trim());
                            field.set(targetEntity, relationalEntity);
                        }
                    }
                }
                resultList.add(targetEntity);
            }
        }
        return resultList;
    }
}
