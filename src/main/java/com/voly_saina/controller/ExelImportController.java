package com.voly_saina.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.voly_saina.entity.Machine;
import com.voly_saina.service.ExelImport;

@Controller
@RequestMapping("/api/exel")
public class ExelImportController {

    private final ExelImport exelImport;

    public ExelImportController(ExelImport exelImport) {
        this.exelImport = exelImport;
    }

    @GetMapping("/template/{tableName}")
    public ResponseEntity<byte[]> downloadTemplate(@PathVariable String tableName) throws IOException {
        try {
            Class<?> entityClass = Class.forName(tableName);
            ExelImport.UniversalTemplateGenerator generator = exelImport.new UniversalTemplateGenerator();
            // machine.class chage selon le controller
            List<Class<?>> entities = List.of(entityClass); // You can add more entities if needed

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            generator.generateMultiEntityTemplate(baos, entities);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=template_machine.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(baos.toByteArray());
        } catch (ClassNotFoundException e) {
            return ResponseEntity.badRequest().body(("Classe non trouvée: " + tableName).getBytes());
        }
    }

    @PostMapping("/import")
    @ResponseBody
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            List<Object> entities = exelImport.importExcel(file.getInputStream());
            return ResponseEntity.ok(entities.size() + " entite(s) importee(s) avec succes");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Echec de l'import : " + e.getMessage());
        }
    }
}
