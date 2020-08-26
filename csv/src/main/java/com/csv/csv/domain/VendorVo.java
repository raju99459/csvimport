package com.csv.csv.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VendorVo {

    private Long id;

    @NonNull
    @NotEmpty
    private String firstName;

    @NonNull
    @NotEmpty
    private String lastName;

    @NonNull
    @NotEmpty
    @Email
    private String email;

    private Integer age;

    @NonNull
    @NotEmpty
    @Length(min = 10, max = 10)
    private String mobile;

    private String address;

    private String city;

    private String state;

    private String zip;

    private String country;

    private Double salary;
}
