package com.delogica.springboot.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ProductOutputDTO {

    private Long id;
    private String sku;
    private String name;
    private Double price;
    private Integer stock;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
