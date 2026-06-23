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

public class ExelImport {

    // genralisation de l'importation des données à partir d'un fichier Excel

    private final EntityManager entityManager;

    public ExelImport(EntityManager entityManager) {
        this.entityManager = entityManager;
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

                // 1. Loop through each entity class provided
                for (Class<?> clazz : entityClasses) {
                    // Get a clean prefix name (e.g., "Machine" becomes "machine")
                    String entityPrefix = clazz.getSimpleName().substring(0, 1).toLowerCase()
                            + clazz.getSimpleName().substring(1);

                    // 2. Inspect the fields of the current class
                    for (Field field : clazz.getDeclaredFields()) {

                        // Skip system-managed fields
                        if (field.isAnnotationPresent(GeneratedValue.class) ||
                                field.isAnnotationPresent(CreationTimestamp.class)) {
                            continue;
                        }

                        // 3. Create the header following the "entityPrefix_fieldName" rule
                        Cell cell = headerRow.createCell(columnIndex++);
                        String headerName = entityPrefix + "_" + field.getName();
                        cell.setCellValue(headerName);
                    }
                }

                // Write the generated template out
                workbook.write(outputStream);
            }
        }

        // Pass in the list of available machine types from your database
        public List<Object> importGeneralise(InputStream inputStream) {
            List<Object> entities = new ArrayList<>();

            try (Workbook workbook = WorkbookFactory.create(inputStream)) {
                // Read the first sheet
                Sheet sheet = workbook.getSheetAt(0);
                Row headerRow = sheet.getRow(0);

                // 1. Map header names to column indices
                Map<String, Integer> headerMap = new HashMap<>();
                for (Cell cell : headerRow) {
                    headerMap.put(cell.getStringCellValue(), cell.getColumnIndex());
                }

                // 2. Identify all distinct entities present in the headers
                // Example headers: "machine_nom", "machine_prixJour",
                // "typeMachine_idTypeMachine"
                Set<String> entityPrefixes = new HashSet<>();
                for (String header : headerMap.keySet()) {
                    if (header.contains("_")) {
                        entityPrefixes.add(header.split("_")[0]); // yields "machine", "typeMachine"
                    }
                }

                // 3. Process each data row
                for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    if (row == null)
                        continue;

                    // Keep track of all instantiated objects for this specific row
                    Map<String, Object> rowEntities = new HashMap<>();

                    // Instantiate an instance for each entity prefix found
                    for (String prefix : entityPrefixes) {
                        Class<?> clazz = getEntityClassByName(prefix);
                        if (clazz != null) {
                            Object entityInstance = clazz.getDeclaredConstructor().newInstance();
                            rowEntities.put(prefix, entityInstance);
                        }
                    }

                    // 4. Populate fields dynamically across all instantiated row objects
                    populateEntityFields(row, headerMap, rowEntities);

                    // Add all fully populated row entities to the final return list
                    entities.addAll(rowEntities.values());
                }

                // 5. Persist everything to the database dynamically
                persistEntity(entities);

            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
            }
            return entities;
        }

        // Helper to dynamically find a class type by its simple name matching your
        // populate
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

        // persist
        private void persistEntity(List<Object> entities) {
            entityManager.getTransaction().begin();
            for (Object entity : entities) {
                entityManager.persist(entity);
            }
            entityManager.getTransaction().commit();
        }

        // --- Dynamic Value Binding ---
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
            // --- Handle Relational Entity Fields (Object Relations) ---
            else if (!fieldType.getName().startsWith("java.")) {
                // If it's a relation, Excel provides an ID (Integer/Long)
                Long foreignId = (long) cell.getNumericCellValue();

                // Dynamically find and fetch the matching related record from the database
                Object managedRelation = entityManager.find(fieldType, foreignId);
                if (managedRelation != null) {
                    field.set(currentEntity, managedRelation);
                }
            }
            
        }

        // package layout
        private Class<?> getEntityClassByName(String entityName) {
            try {
                // Update this string to your exact entity package location
                String packagePrefix = "com.example.project.entity.";
                String className = packagePrefix + entityName.substring(0, 1).toUpperCase() + entityName.substring(1);
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }

        // Helper to find a field even if it lives up inside a parent superclass
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
