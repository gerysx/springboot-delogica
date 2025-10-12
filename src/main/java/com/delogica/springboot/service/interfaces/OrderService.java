package com.delogica.springboot.service.interfaces;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.delogica.springboot.dto.OrderInputDTO;
import com.delogica.springboot.dto.OrderOutputDTO;
import com.delogica.springboot.model.OrderStatus;

public interface OrderService {
	
	Page<OrderOutputDTO> findByCustomerId(Long customerId, Pageable pageable);
	
	Page<OrderOutputDTO> findByCustomerIdAndOrderDateBetween(Long customerId, LocalDateTime start, LocalDateTime end, Pageable pageable);
	
	Page<OrderOutputDTO> findByOrderDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
	
	OrderOutputDTO create(OrderInputDTO order);
	
	OrderOutputDTO update(Long customerId, Long orderId, OrderInputDTO order);
	
	OrderOutputDTO updateStatus(Long customerId, Long orderId, OrderStatus newStatus);

	
	
	
	
	 
	}
	
	

