package com.delogica.springboot.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class OrderOutputDTO {

	private Long orderId;
    private Long customerId;
    private Long shippingAddressId;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal total;
    private List<OrderItemOutputDTO> items;

}