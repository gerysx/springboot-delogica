package com.delogica.springboot.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CustomerOutputDTO {
	
	private Long id;
	private String fullName;
	private String email;
	private String phone;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
