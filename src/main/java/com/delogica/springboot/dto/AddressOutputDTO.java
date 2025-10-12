package com.delogica.springboot.dto;

import lombok.Data;

@Data
public class AddressOutputDTO {

    private Long id;

    private String line1;
    private String line2;
    private String city;
    private String postalCode;
    private String country;

    private Boolean isDefault;

    private Long customerId;
    
}