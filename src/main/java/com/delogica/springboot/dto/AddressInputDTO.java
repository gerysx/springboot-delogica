package com.delogica.springboot.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddressInputDTO {

	@NotNull
	private Long customerId;
	
	@NotEmpty
	private String line1;
	
	private String line2;
	
	@NotEmpty
	private String city;
	
	@NotEmpty
	private String postalCode;
	
	@NotEmpty
	private String country;
	
	private Boolean isDefault;
	
}
