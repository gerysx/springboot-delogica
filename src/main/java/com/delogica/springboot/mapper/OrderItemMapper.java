package com.delogica.springboot.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.delogica.springboot.dto.OrderItemInputDTO;
import com.delogica.springboot.dto.OrderItemOutputDTO;
import com.delogica.springboot.model.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "productId", target = "product.id")
    OrderItemMapper toEntity(OrderItemInputDTO dto);

    @Mapping(source = "product.id", target = "productId")
    OrderItemOutputDTO toDto(OrderItem entity);

    List<OrderItem> toEntityList(List<OrderItemInputDTO> dtos);

    List<OrderItemOutputDTO> toDtoList(List<OrderItem> entities);
}