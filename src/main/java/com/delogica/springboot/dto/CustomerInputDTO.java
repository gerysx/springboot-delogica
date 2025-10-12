package com.delogica.springboot.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CustomerInputDTO {
	
	@NotEmpty(message = "El nombre completo es obligatorio")
	private String fullName;
	
	@NotEmpty (message = "El email del cliente es obligatorio")
	private String email;
	
	@NotEmpty (message = "El teléfono del cliente es obligatorio")
	private String phone;
	

}
