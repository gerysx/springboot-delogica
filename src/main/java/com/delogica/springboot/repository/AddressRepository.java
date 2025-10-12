package com.delogica.springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.delogica.springboot.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

	 // Listar direcciones de un cliente
	
    List<Address> findByCustomerId(Long customerId);

    // Buscar dirección por si es default
    
    Optional<Address> findByCustomerIdAndIsDefaultTrue(Long customerId);
    
    Page<Address> findAllByCustomerId(Long customerId, Pageable pageable);
}
