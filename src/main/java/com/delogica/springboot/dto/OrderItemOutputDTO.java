package com.delogica.springboot.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemOutputDTO {
    private Long orderId;
    private Long productId;
    private int quantity;
    private BigDecimal unitPrice;
}
