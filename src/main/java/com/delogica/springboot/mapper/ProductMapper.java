package com.delogica.springboot.mapper;

import org.mapstruct.Mapper;

import com.delogica.springboot.dto.ProductInputDTO;
import com.delogica.springboot.dto.ProductOutputDTO;
import com.delogica.springboot.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductInputDTO dto);
    ProductOutputDTO toDto(Product entity);
}
