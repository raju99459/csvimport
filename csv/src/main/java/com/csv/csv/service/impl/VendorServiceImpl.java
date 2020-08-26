package com.csv.csv.service.impl;

import com.csv.csv.entity.Vendor;
import com.csv.csv.repository.VendorRepository;
import com.csv.csv.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VendorServiceImpl implements VendorService {
    @Autowired
    VendorRepository vendorRepository;


    @Override
    public HashMap<String, Set<String>> getEmailAndMobileForDuplicateCheck(Set<String> emails, Set<String> mobiles){

        HashMap<String, Set<String>> ret = new HashMap<>();
        ret.put("emails", new HashSet<>());
        ret.put("mobiles", new HashSet<>());
        List<Object[]> list = vendorRepository.getVendorEmailsAndMobiles(emails, mobiles);
        list.stream().forEach((Object[] obj) -> {

            ret.get("emails").add(obj[0].toString());
            ret.get("emails").add(obj[1].toString());

        });

        return ret;

    }


    @Override
    public List<Vendor> createVendors(List<Vendor> list) {
        if(list.size() > 0) {
            return vendorRepository.saveAll(list);
        } else {
            return null;
        }

    }
}
