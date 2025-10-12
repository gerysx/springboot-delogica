package com.delogica.springboot.service.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.delogica.springboot.dto.ProductInputDTO;
import com.delogica.springboot.dto.ProductOutputDTO;


public interface ProductService {
	Page<ProductOutputDTO> findAll(Pageable pageable);

	List<ProductOutputDTO> findByName(String name);

	List<ProductOutputDTO> findByIsActiveTrue();

	ProductOutputDTO create(ProductInputDTO dto);

	ProductOutputDTO update(Long id, ProductInputDTO dto);

	void delete(Long id);
}
