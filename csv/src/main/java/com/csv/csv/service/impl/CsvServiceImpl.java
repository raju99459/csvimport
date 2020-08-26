package com.csv.csv.service.impl;

import com.csv.csv.controller.CsvController;
import com.csv.csv.domain.VendorVo;
import com.csv.csv.entity.Vendor;
import com.csv.csv.exceptions.DuplicateRecordsExceeded;
import com.csv.csv.service.CsvService;
import com.csv.csv.service.ValidateService;
import com.csv.csv.service.VendorService;
import com.csv.csv.utility.ObjectMapperUtils;
import com.csv.csv.utility.Utils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import javax.validation.ConstraintViolationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;


@Service
@Transactional
public class CsvServiceImpl implements CsvService {

    Logger logger = LoggerFactory.getLogger(CsvService.class);

    @Autowired
    ObjectMapper mapper;

    @Autowired
    ValidateService validateService;

    @Autowired
    VendorService vendorService;

    static String[] fields = new String[]{"id", "firstName", "lastName", "age", "email",
            "mobile", "address", "city", "state", "country",  "zip", "salary"};

    int chunkSize = 30;
    int duplicateRecLimit = 1000;


    public List<Vendor> checkForDuplicates (List<VendorVo> li, HashMap<String, Set<String>> checkList) throws DuplicateRecordsExceeded {
        Set<String> emailList;
        Set<String> mobileList;
        if (checkList.containsKey("emails")) {
            emailList = checkList.get("emails");
        } else {
            emailList = new HashSet<>();
        }
        if (checkList.containsKey("mobiles")) {
            mobileList = checkList.get("mobiles");
        } else {
            mobileList = new HashSet<>();
        }
        List<Vendor> list = new ArrayList<>();

        li.stream().forEach((VendorVo vendorVo) -> {
            if (!emailList.contains(vendorVo.getEmail()) &&
                    !mobileList.contains(vendorVo.getMobile())) {
                list.add(ObjectMapperUtils.map(vendorVo, Vendor.class));
            }
        });

        return list;
    }

    @Transactional(rollbackFor = { DuplicateRecordsExceeded.class, RuntimeException.class })
    public HashMap<String, Integer> processCsv(Reader reader) throws DuplicateRecordsExceeded {
        int invalidRecCount = 0, duplicateRecCount = 0, importedRecords = 0;

        Set<String> dCheckMobile = new HashSet<>();
        Set<String> dCheckEmail = new HashSet<>();

        List<VendorVo> recordChunk = new ArrayList<>();

        try {
            CSVReader csvReader = new CSVReader(reader);
            String[] line;
            VendorVo vendorVo = new VendorVo();

            // first row can be used to generic import
            // for skipping the column header,
            csvReader.readNext();

            if ((line = csvReader.readNext()) != null) {

                do {

                    if (duplicateRecCount >= duplicateRecLimit) {
                        throw new DuplicateRecordsExceeded("The no of duplicate records exceeded!");
                    }
                    /* // converting string array to vo bean
                    if ((vendorVo = this.convertToBean(line)) == null) {
                        invalidRecCount++;
                        continue;
                    } */

                    // converting string array to vo bean
                    if ((vendorVo = Utils.convertArrayToBean(line, fields, VendorVo.class)) == null) {
                        invalidRecCount++;
                        continue;
                    }

                    try {
                        // validation of the bean and counting the invalid records
                        if (validateService.validateVendor(vendorVo)) {

                            if (!dCheckEmail.contains(vendorVo.getEmail()) && !dCheckMobile.contains(vendorVo.getMobile())) {
                                recordChunk.add(vendorVo);
                                dCheckEmail.add(vendorVo.getEmail());
                                dCheckMobile.add(vendorVo.getMobile());
                            } else {
                                duplicateRecCount++;
                                if (duplicateRecCount >= duplicateRecLimit) {
                                    throw new DuplicateRecordsExceeded("The no of duplicate records exceeded!");
                                }
                            }
                        } else {
                            invalidRecCount++;
                        }

                    }
                    catch (ConstraintViolationException cve) {
                        invalidRecCount++;
                    }

                    line = csvReader.readNext();

                    if (chunkSize == recordChunk.size() || line == null) {
                        HashMap<String, Set<String>> checkList = vendorService.getEmailAndMobileForDuplicateCheck(dCheckEmail, dCheckMobile);
                        List<Vendor> li = this.checkForDuplicates(recordChunk, checkList);

                        duplicateRecCount = duplicateRecCount + (recordChunk.size() - li.size());

                        if (duplicateRecCount >= duplicateRecLimit) {
                            throw new DuplicateRecordsExceeded("The no of duplicate records exceeded!");
                        }

                        li = vendorService.createVendors(li);
                        if (li != null) {
                            importedRecords = importedRecords + li.size();
                        }
                        checkList.clear();
                        recordChunk.clear();
                        dCheckEmail.clear();
                        dCheckMobile.clear();
                    }

                }
                while (line != null);


            }

            HashMap<String, Integer> ret = new HashMap<>();
            ret.put("importedRec", importedRecords);
            ret.put("invalidRec", invalidRecCount);
            ret.put("duplicateRecords", duplicateRecCount);

            logger.info("no of records imported : " + importedRecords);
            logger.info("no of records invalid : " + invalidRecCount);
            logger.info("no of duplicate records : " + duplicateRecCount);


            reader.close();
            csvReader.close();

            return ret;
        }
        catch (DuplicateRecordsExceeded duplicateRecordsExceeded) {
            logger.error("duplicate record exception in processCsv " + duplicateRecordsExceeded.getMessage());
            throw duplicateRecordsExceeded;
        }
        catch (IOException io) {
            logger.error("io exception in processCsv" + io.getMessage());
        }
        return null;
    }

    @Override
    public HashMap<String, Integer> importCsv(MultipartFile file) throws DuplicateRecordsExceeded{

        try {
            logger.info("import csv has called");
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            HashMap<String, Integer>  ret = this.processCsv(reader);
            logger.info("file successfully processed");
            return ret;
        }
        catch (DuplicateRecordsExceeded duplicateRecordsExceeded) {
            throw duplicateRecordsExceeded;
        }
        catch (IOException io) {
            logger.error("importCsv : " + io.getMessage());
            System.out.println(io.getMessage());
        }
        catch (Exception ex) {
            logger.error("importCsv : " + ex.getMessage());
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
