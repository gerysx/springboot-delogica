package com.delogica.springboot.mapper;

import org.mapstruct.Mapper;

import com.delogica.springboot.dto.CustomerInputDTO;
import com.delogica.springboot.dto.CustomerOutputDTO;
import com.delogica.springboot.model.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
	
	Customer toEntity(CustomerInputDTO customer);
	
	CustomerOutputDTO toDto(Customer customer);

}
