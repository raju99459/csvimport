package com.csv.csv.repository;

import com.csv.csv.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface VendorRepository extends JpaRepository<Vendor, Long> {

    @Query("SELECT email, mobile FROM Vendor v WHERE v.email IN (:emails) OR v.mobile IN (:mobile) ")
    List<Object[]> getVendorEmailsAndMobiles(@Param("emails") Set<String> emails, @Param("mobile") Set<String> mobile);

}
