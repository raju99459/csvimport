package com.csv.csv.service.impl;

import com.csv.csv.domain.VendorVo;
import com.csv.csv.service.ValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import java.util.Set;

@Service
public class ValidateServiceImpl implements ValidateService {

    @Autowired
    Validator validator;

    public boolean validateVendor(VendorVo vendorVo) throws ConstraintViolationException {
        Set<ConstraintViolation<VendorVo>> constraintViolation = validator.validate(vendorVo);
        if (!constraintViolation.isEmpty()) {
            throw new ConstraintViolationException(constraintViolation);
        }
        return true;
    }
}
