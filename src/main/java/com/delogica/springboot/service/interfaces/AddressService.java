package com.delogica.springboot.service.interfaces;

import java.util.List;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.delogica.springboot.dto.AddressInputDTO;
import com.delogica.springboot.dto.AddressOutputDTO;

public interface AddressService {
	
	 // Obtener todas las direcciones de un cliente
    Page<AddressOutputDTO> findAllByCustomerId(Long customerId, Pageable pageable);

    // Obtener la dirección por defecto de un cliente
    List<AddressOutputDTO> findByCustomerIdAndIsDefaultTrue(Long customerId);

    // Crear dirección para un cliente
    AddressOutputDTO create(Long customerId, AddressInputDTO addressDto);

    // Actualizar una dirección específica de un cliente
    AddressOutputDTO update(Long customerId, Long addressId, AddressInputDTO addressDto);

    // Eliminar una dirección de un cliente
    void delete(Long customerId, Long addressId);

}
