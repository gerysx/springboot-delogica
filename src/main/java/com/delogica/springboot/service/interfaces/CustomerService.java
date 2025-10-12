package com.delogica.springboot.service.interfaces;

import com.delogica.springboot.dto.CustomerInputDTO;
import com.delogica.springboot.dto.CustomerOutputDTO;

public interface CustomerService {

	CustomerOutputDTO findByEmail(String email);
	
	CustomerOutputDTO findById(Long customerId);
	
	CustomerOutputDTO save(CustomerInputDTO customer);
	
	CustomerOutputDTO updateById(CustomerInputDTO customer, Long id);
	
	void delete(Long id);
	
	
}
