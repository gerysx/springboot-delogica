package com.delogica.springboot.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.delogica.springboot.dto.CustomerInputDTO;
import com.delogica.springboot.dto.CustomerOutputDTO;
import com.delogica.springboot.exceptions.NotFoundException;
import com.delogica.springboot.exceptions.ResourceAlreadyExistsException;
import com.delogica.springboot.mapper.CustomerMapper;
import com.delogica.springboot.model.Customer;
import com.delogica.springboot.repository.CustomerRepository;
import com.delogica.springboot.service.interfaces.CustomerService;
import com.delogica.springboot.utils.Pageables;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de clientes que gestiona operaciones de consulta y mantenimiento
 * Orquesta el acceso a datos mediante CustomerRepository y el mapeo con
 * CustomerMapper
 */

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final Pageables pageables;

    // ===== Lectura =====

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerOutputDTO> findAll(Pageable pageable) {
        Pageable effective = pageables.withDefaultSort(pageable,
                pageables.customerDefaultSort()); // p.ej. fullName ASC, id DESC
        return customerRepository.findAll(effective).map(customerMapper::toDto);
    }

    /**
     * Busca un cliente por su email
     *
     * @param email email a consultar
     * @return DTO del cliente encontrado
     * @throws NotFoundException si no existe un cliente con ese email
     */
    @Override
    @Transactional(readOnly = true)
    public CustomerOutputDTO findByEmail(@NotBlank @Email String email) {

        Optional<Customer> opt = customerRepository.findByEmail(email);

        Customer customer = opt.orElseThrow(
                () -> new NotFoundException("Cliente no encontrado con email: " + email));
        return customerMapper.toDto(customer);
    }

    /**
     * Obtiene un cliente por su identificador
     *
     * @param customerId id del cliente
     * @return DTO del cliente encontrado
     * @throws NotFoundException si no existe un cliente con ese id
     */
    @Override
    @Transactional(readOnly = true)
    public CustomerOutputDTO findById(@NotNull @Positive Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado con id: " + customerId));

        return customerMapper.toDto(customer);
    }

    // ===== Creación, actualización y eliminación =====

    /**
     * Actualiza los datos básicos de un cliente por id
     *
     * @param customer DTO de entrada con los nuevos datos
     * @param id       id del cliente a actualizar
     * @return DTO del cliente actualizado
     * @throws NotFoundException si no existe un cliente con ese id
     */
    @Override
    @Transactional
    public CustomerOutputDTO updateById(@Valid @NotNull CustomerInputDTO input,
            @NotNull @Positive Long id) {

        Customer entity = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado con id: " + id));

        if (!entity.getEmail().equalsIgnoreCase(input.getEmail())) {
            customerRepository.findByEmail(input.getEmail())
                    .ifPresent(c -> {
                        throw new ResourceAlreadyExistsException(
                                "Ya existe un cliente con email: " + input.getEmail());
                    });
        }

        entity.setEmail(input.getEmail());
        entity.setFullName(input.getFullName());
        entity.setPhone(input.getPhone());

        return customerMapper.toDto(customerRepository.save(entity));
    }

    /**
     * Elimina un cliente por id
     *
     * @param id id del cliente a eliminar
     * @throws NotFoundException si no existe un cliente con ese id
     */
    @Override
    public void delete(@NotNull @Positive Long id) {

        Customer findCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado con id: " + id));

        customerRepository.delete(findCustomer);

    }

    /**
     * Crea un nuevo cliente si el email no está registrado
     *
     * @param customer DTO de entrada con los datos del cliente
     * @return DTO del cliente creado
     * @throws ResourceAlreadyExistsException si ya existe un cliente con ese email
     */
    @Override
    public CustomerOutputDTO create(CustomerInputDTO customer) {
        customerRepository.findByEmail(customer.getEmail())
                .ifPresent(c -> {
                    throw new ResourceAlreadyExistsException("Ya existe un cliente con email: " + customer.getEmail());
                });

        Customer saved = customerRepository.save(customerMapper.toEntity(customer));
        return customerMapper.toDto(saved);
    }

}
