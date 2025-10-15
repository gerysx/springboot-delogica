package com.delogica.springboot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import com.delogica.springboot.dto.OrderInputDTO;
import com.delogica.springboot.dto.OrderOutputDTO;
import com.delogica.springboot.model.Order;


@Mapper(componentModel = "spring", uses = { OrderItemMapper.class })
public interface OrderMapper {

   // ENTRADA: no tocar el id, lo genera la BBDD
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "addressId", target = "shippingAddress.id")
    Order toEntity(OrderInputDTO dto);

    // SALIDA: exponer el id generado en orderId
    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "shippingAddress.id", target = "shippingAddressId")
    OrderOutputDTO toDto(Order entity);
}
