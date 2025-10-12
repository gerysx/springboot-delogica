package com.delogica.springboot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import com.delogica.springboot.dto.OrderInputDTO;
import com.delogica.springboot.dto.OrderOutputDTO;
import com.delogica.springboot.model.Order;


@Mapper(componentModel = "spring")
public interface OrderMapper {

	@Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "addressId", target = "shippingAddress.id")
    Order toEntity(OrderInputDTO dto);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "shippingAddress.id", target = "shippingAddressId")
    OrderOutputDTO toDto(Order entity);
}
