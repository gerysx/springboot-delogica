package com.delogica.springboot.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.delogica.springboot.dto.CustomerInputDTO;
import com.delogica.springboot.dto.CustomerOutputDTO;

public interface CustomerService {

	CustomerOutputDTO findByEmail(String email);
	
	CustomerOutputDTO findById(Long customerId);

	CustomerOutputDTO create(CustomerInputDTO customer);
	
	CustomerOutputDTO updateById(CustomerInputDTO customer, Long id);
	
	void delete(Long id);

	Page<CustomerOutputDTO> findAll(Pageable pageable);
	
	
}
