package com.delogica.springboot.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.delogica.springboot.dto.CustomerInputDTO;
import com.delogica.springboot.dto.CustomerOutputDTO;
import com.delogica.springboot.exceptions.NotFoundException;
import com.delogica.springboot.exceptions.ResourceAlreadyExistsException;
import com.delogica.springboot.mapper.CustomerMapper;
import com.delogica.springboot.model.Customer;
import com.delogica.springboot.repository.CustomerRepository;
import com.delogica.springboot.service.interfaces.CustomerService;

/**
 * Servicio de clientes que gestiona operaciones de consulta y mantenimiento
 * Orquesta el acceso a datos mediante CustomerRepository y el mapeo con
 * CustomerMapper
 */

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
        this.customerRepository = customerRepository;
    }

    /**
     * Busca un cliente por su email
     *
     * @param email email a consultar
     * @return DTO del cliente encontrado
     * @throws NotFoundException si no existe un cliente con ese email
     */
    @Override
    public CustomerOutputDTO findByEmail(String email) {

        Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);

        if (!optionalCustomer.isPresent()) {
            throw new NotFoundException("Cliente no encontrado con email: " + email);
        }

        return customerMapper.toDto(optionalCustomer.get());
    }

    /**
     * Obtiene un cliente por su identificador
     *
     * @param customerId id del cliente
     * @return DTO del cliente encontrado
     * @throws NotFoundException si no existe un cliente con ese id
     */
    @Override
    public CustomerOutputDTO findById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado con id: " + customerId));

        return customerMapper.toDto(customer);
    }

    /**
     * Actualiza los datos básicos de un cliente por id
     *
     * @param customer DTO de entrada con los nuevos datos
     * @param id       id del cliente a actualizar
     * @return DTO del cliente actualizado
     * @throws NotFoundException si no existe un cliente con ese id
     */
    @Override
    public CustomerOutputDTO updateById(CustomerInputDTO customer, Long id) {
        Customer findCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado con id: " + id));

        findCustomer.setEmail(customer.getEmail());
        findCustomer.setFullName(customer.getFullName());
        findCustomer.setPhone(customer.getPhone());

        Customer updatedCustomer = customerRepository.save(findCustomer);

        return customerMapper.toDto(updatedCustomer);
    }

    /**
     * Elimina un cliente por id
     *
     * @param id id del cliente a eliminar
     * @throws NotFoundException si no existe un cliente con ese id
     */
    @Override
    public void delete(Long id) {

        Customer findCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado con id: " + id));

        customerRepository.delete(findCustomer);

    }

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
