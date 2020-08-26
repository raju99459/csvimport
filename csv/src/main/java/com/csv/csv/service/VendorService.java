package com.csv.csv.service;

import com.csv.csv.entity.Vendor;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface VendorService {

    HashMap<String, Set<String>> getEmailAndMobileForDuplicateCheck(Set<String> emails, Set<String> mobiles);
    List<Vendor> createVendors(List<Vendor> list);

}
