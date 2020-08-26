package com.csv.csv.service;

import com.csv.csv.domain.VendorVo;
import com.csv.csv.entity.Vendor;
import com.csv.csv.exceptions.DuplicateRecordsExceeded;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public interface CsvService {

    @Transactional
    public HashMap<String, Integer> processCsv(Reader reader) throws DuplicateRecordsExceeded;
    HashMap<String, Integer> importCsv(MultipartFile file) throws DuplicateRecordsExceeded;

}
