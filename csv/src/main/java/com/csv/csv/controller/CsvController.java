package com.csv.csv.controller;

import com.csv.csv.exceptions.DuplicateRecordsExceeded;
import com.csv.csv.service.CsvService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

@RestController("/v1/csv/")
public class CsvController {

    Logger logger = LoggerFactory.getLogger(CsvController.class);

    @Autowired
    CsvService csvService;

    @PostMapping
    public ResponseEntity importCsv(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            logger.info("Start of service import csv ");

            HashMap<String, Integer> ret = csvService.importCsv(file);

            logger.info("service import csv successful");
            if (ret != null){
                return ResponseEntity.ok()
                        .body(ret);
            } else {
                return ResponseEntity.ok()
                        .body("unable to import.");
            }

        }
        catch (DuplicateRecordsExceeded duplicateRecordsExceeded) {
            logger.error("No of duplicate records exceeded limit 1000.");
            return ResponseEntity.badRequest().body("No of duplicate records exceeded limit 1000.");
        }
        catch (Exception ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.badRequest().body("Unhandled exception");
        }

    }
}
