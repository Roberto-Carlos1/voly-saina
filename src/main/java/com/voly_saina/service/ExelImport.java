package com.voly_saina.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExelImport {

    private final EntityManager entityManager;

    public ExelImport(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public List<Object> importExcel(InputStream inputStream) {
        return new UniversalTemplateGenerator().importGeneralise(inputStream);
    }

    public class UniversalTemplateGenerator {

        /**
         * Generates a single-sheet template containing columns for multiple entities.
         * * @param outputStream The target output stream for the xlsx file.
         * 
         * @param entityClasses List of classes to include (e.g., List.of(Machine.class,
         *                      TypeMachine.class))
         */
        public void generateMultiEntityTemplate(OutputStream outputStream, List<Class<?>> entityClasses)
                throws IOException {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Universal_Import");
                Row headerRow = sheet.createRow(0);
                int columnIndex = 0;

                for (Class<?> clazz : entityClasses) {
                    String entityPrefix = clazz.getSimpleName().substring(0, 1).toLowerCase()
                            + clazz.getSimpleName().substring(1);

                    for (Field field : clazz.getDeclaredFields()) {

                        if (field.isAnnotationPresent(GeneratedValue.class) ||
                                field.isAnnotationPresent(CreationTimestamp.class)) {
                            continue;
                        }

                        Cell cell = headerRow.createCell(columnIndex++);
                        String headerName = entityPrefix + "_" + field.getName();
                        cell.setCellValue(headerName);
                    }
                }

                workbook.write(outputStream);
            }
        }

        private List<Object> importGeneralise(InputStream inputStream) {
            List<Object> entities = new ArrayList<>();

            try (Workbook workbook = WorkbookFactory.create(inputStream)) {
                Sheet sheet = workbook.getSheetAt(0);
                Row headerRow = sheet.getRow(0);

                Map<String, Integer> headerMap = new HashMap<>();
                for (Cell cell : headerRow) {
                    headerMap.put(cell.getStringCellValue(), cell.getColumnIndex());
                }

                Set<String> entityPrefixes = new HashSet<>();
                for (String header : headerMap.keySet()) {
                    if (header.contains("_")) {
                        entityPrefixes.add(header.split("_")[0]);
                    }
                }

                for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    if (row == null)
                        continue;

                    Map<String, Object> rowEntities = new HashMap<>();

                    for (String prefix : entityPrefixes) {
                        Class<?> clazz = getEntityClassByName(prefix);
                        if (clazz != null) {
                            Object entityInstance = clazz.getDeclaredConstructor().newInstance();
                            rowEntities.put(prefix, entityInstance);
                        }
                    }

                    populateEntityFields(row, headerMap, rowEntities);

                    entities.addAll(rowEntities.values());
                }

                for (Object entity : entities) {
                    entityManager.persist(entity);
                }

            } catch (Exception e) {
                throw new RuntimeException("Import echoue: " + e.getMessage(), e);
            }
            return entities;
        }

        private void populateEntityFields(Row row, Map<String, Integer> headerMap, Map<String, Object> rowEntities)
                throws IllegalAccessException {
            for (String header : headerMap.keySet()) {
                int colIndex = headerMap.get(header);
                Cell cell = row.getCell(colIndex);
                if (cell == null || cell.getCellType() == CellType.BLANK)
                    continue;

                String[] parts = header.split("_");
                String prefix = parts[0];
                String fieldName = parts[1];

                Object currentEntity = rowEntities.get(prefix);
                if (currentEntity == null)
                    continue;

                Field field = getFieldProperties(currentEntity.getClass(), fieldName);
                if (field == null)
                    continue;

                dynamicValueBinding(currentEntity, currentEntity, field, cell);

            }
        }

        private void dynamicValueBinding(Object entityInstance, Object currentEntity, Field field, Cell cell)
        throws IllegalAccessException {
            Class<?> fieldType = field.getType();
            field.setAccessible(true);
            
            if (fieldType == String.class) {
                field.set(currentEntity, cell.getStringCellValue());
            } else if (fieldType == BigDecimal.class) {
                field.set(currentEntity, BigDecimal.valueOf(cell.getNumericCellValue()));
            } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                field.set(currentEntity, cell.getBooleanCellValue());
            } else if (fieldType == Integer.class || fieldType == int.class) {
                field.set(currentEntity, (int) cell.getNumericCellValue());
            } else if (fieldType == Long.class || fieldType == long.class) {
                field.set(currentEntity, (long) cell.getNumericCellValue());
            }
            else if (!fieldType.getName().startsWith("java.")) {
                Long foreignId = (long) cell.getNumericCellValue();

                Object managedRelation = entityManager.find(fieldType, foreignId);
                if (managedRelation != null) {
                    field.set(currentEntity, managedRelation);
                }
            }
            
        }

        private Class<?> getEntityClassByName(String entityName) {
            try {
                String packagePrefix = "com.voly_saina.entity.";
                String className = packagePrefix + entityName.substring(0, 1).toUpperCase() + entityName.substring(1);
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }

        private Field getFieldProperties(Class<?> clazz, String fieldName) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                if (clazz.getSuperclass() != null) {
                    return getFieldProperties(clazz.getSuperclass(), fieldName);
                }
                return null;
            }
        }
    }
}
