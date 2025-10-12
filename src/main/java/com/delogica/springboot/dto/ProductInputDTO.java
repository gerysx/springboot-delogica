package com.delogica.springboot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductInputDTO {

    @NotBlank(message = "SKU es obligatorio")
    private String sku;

    @NotBlank(message = "Nombre es obligatorio")
    private String name;

    @NotNull(message = "Precio es obligatorio")
    @Min(value = 0, message = "Precio debe ser mayor o igual a 0")
    private Double price;

    @NotNull(message = "Stock es obligatorio")
    @Min(value = 0, message = "Stock no puede ser negativo")
    private Integer stock;

    private Boolean active;
}
