package com.delogica.springboot.service.interfaces;

import java.util.List;
import java.util.Optional;

import com.delogica.springboot.dto.AddressInputDTO;
import com.delogica.springboot.dto.AddressOutputDTO;

public interface AddressService {
	
	 // Obtener todas las direcciones de un cliente
    List<AddressOutputDTO> findByCustomerId(Long customerId);

    // Obtener la dirección por defecto de un cliente
    Optional<AddressOutputDTO> findByCustomerIdAndIsDefaultTrue(Long customerId);

    // Crear dirección para un cliente
    AddressOutputDTO create(Long customerId, AddressInputDTO addressDto);

    // Actualizar una dirección específica de un cliente
    AddressOutputDTO update(Long customerId, Long addressId, AddressInputDTO addressDto);

    // Eliminar una dirección de un cliente
    void delete(Long customerId, Long addressId);

}
