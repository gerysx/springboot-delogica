package com.delogica.springboot.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.delogica.springboot.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{

    // Listar pedidos de un cliente
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    // Listar pedidos por cliente y rango de fechas
    Page<Order> findByCustomerIdAndOrderDateBetween(Long customerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    // Listar pedidos por rango de fechas (sin cliente)
    Page<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
