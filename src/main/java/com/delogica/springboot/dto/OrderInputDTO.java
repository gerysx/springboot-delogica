package com.delogica.springboot.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderInputDTO {

    @NotNull(message = "El id del cliente es obligatorio")
    private Long customerId;

    @NotNull(message = "La dirección de envío es obligatoria")
    private Long addressId;

    @NotEmpty(message = "Debe incluir al menos un ítem en el pedido")
    private List<OrderItemInputDTO> items;

}
