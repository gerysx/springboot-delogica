package com.delogica.springboot.service.impl;

import java.util.List;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.delogica.springboot.dto.AddressInputDTO;
import com.delogica.springboot.dto.AddressOutputDTO;
import com.delogica.springboot.exceptions.NotFoundException;
import com.delogica.springboot.mapper.AddressMapper;
import com.delogica.springboot.model.Address;
import com.delogica.springboot.model.Customer;
import com.delogica.springboot.repository.AddressRepository;
import com.delogica.springboot.repository.CustomerRepository;
import com.delogica.springboot.service.interfaces.AddressService;
import com.delogica.springboot.utils.Pageables;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de direcciones que gestiona consultas y mantenimiento para direcciones de clientes,
 * realiza la persistencia con AddressRepository y mapeo con AddressMapper
 */
@Service
@RequiredArgsConstructor
@Validated
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final Pageables pageables;
    private final CustomerRepository customerRepository;

     /**
     * Obtiene las direcciones por defecto de un cliente
     *
     * @param customerId id del cliente (> 0)
     * @return lista de direcciones por defecto mapeadas a DTO
     * @throws NotFoundException si el cliente no tiene direcciones por defecto
     */
    @Override
    @Transactional(readOnly = true)
    public List<AddressOutputDTO> findByCustomerIdAndIsDefaultTrue(@NotNull @Positive Long customerId) {

        List<Address> addresses = addressRepository.findByCustomerIdAndIsDefaultTrue(customerId);

        if (addresses.isEmpty()) {
            throw new NotFoundException("No se encontraron direcciones para el cliente con ID " + customerId);
        }

        return addresses.stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }

     /**
     * Crea una nueva dirección para un cliente
     *
     * @param customerId id del cliente (> 0)
     * @param dto DTO de entrada validado con los datos de la dirección
     * @return dirección creada mapeada a DTO
     * @throws jakarta.persistence.EntityNotFoundException al hacer flush si el customerId no existe físicamente
     */
    @Override
    @Transactional
    public AddressOutputDTO create(
            @NotNull @Positive Long customerId,
            @Valid @NotNull AddressInputDTO dto) {

        Customer ref = customerRepository.getReferenceById(customerId);

        Address address = addressMapper.toEntity(dto);
        address.setCustomer(ref);

        Address saved = addressRepository.save(address);
        return addressMapper.toDto(saved);
    }

    /**
     * Actualiza una dirección existente de un cliente
     *
     * @param customerId id del cliente propietario de la dirección (> 0)
     * @param addressId id de la dirección a actualizar (> 0)
     * @param dto DTO de entrada validado con los nuevos datos
     * @return dirección actualizada mapeada a DTO
     * @throws NotFoundException si no existe la dirección para el cliente indicado
     */
    @Override
    @Transactional
    public AddressOutputDTO update(
            @NotNull @Positive Long customerId,
            @NotNull @Positive Long addressId,
            @Valid @NotNull AddressInputDTO dto) {

        Address existing = addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new NotFoundException(
                        "Dirección no encontrada para id: " + addressId + " y customer: " + customerId));

        existing.setLine1(dto.getLine1());
        existing.setLine2(dto.getLine2());
        existing.setCity(dto.getCity());
        existing.setPostalCode(dto.getPostalCode());
        existing.setCountry(dto.getCountry());

        Address saved = addressRepository.save(existing);
        return addressMapper.toDto(saved);
    }

    /**
     * Elimina una dirección de un cliente
     *
     * @param customerId id del cliente propietario (> 0)
     * @param addressId id de la dirección a eliminar (> 0)
     * @throws NotFoundException si no existe la dirección para el cliente indicado
     * @throws IllegalStateException si la dirección es la predeterminada del cliente
     */
    @Override
    public void delete(Long customerId, Long addressId) {
        Address address = addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new NotFoundException(
                        "No se ha encontrado la dirección con id: " + addressId + " para el cliente " + customerId));

        if (Boolean.TRUE.equals(address.getIsDefault())) {
            throw new IllegalStateException("No se puede eliminar la dirección por defecto del cliente");
        }

        addressRepository.deleteByIdAndCustomerId(addressId, customerId);
    }

    /**
     * Lista paginada de direcciones de un cliente
     * Si no se especifica orden en el pageable, se aplica el orden por defecto de direcciones
     *
     * @param customerId id del cliente (> 0)
     * @param pageable criterios de paginación y orden
     * @return página de direcciones mapeadas a DTO
     */ 
    @Override
    public Page<AddressOutputDTO> findAllByCustomerId(
            @NotNull @Positive Long customerId,
            Pageable pageable) {
        Pageable effective = pageables.withDefaultSort(pageable, pageables.addressDefaultSort());
        return addressRepository
                .findAllByCustomerId(customerId, effective)
                .map(addressMapper::toDto);
    }

}
