package com.csv.csv.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data

@Entity
@Table(name = "vendor")
public class Vendor {

    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator="vendor_sequence")
    @SequenceGenerator(name = "vendor_sequence", allocationSize = 1, sequenceName ="VENDOR_SEQ")
    private Long id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="email")
    private String email;

    @Column(name="age")
    private Integer age;

    @Column(name="mobile")
    private String mobile;

    @Column(name="address")
    private String address;

    @Column(name="city")
    private String city;

    @Column(name="state")
    private String state;

    @Column(name="zip")
    private String zip;

    @Column(name="country")
    private String country;

    @Column(name="salary")
    private Double salary;
}
