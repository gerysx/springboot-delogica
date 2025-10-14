package com.delogica.springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.delogica.springboot.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	Page<Product> findAll(Pageable pageable);
	
	  // Buscar productos activos
	
    List<Product> findByActiveTrue();

    // Buscar productos por nombre (contenga texto)
    
    List<Product> findByNameContainingIgnoreCase(String name);

    Optional<Product> findBySku(String sku);

}
