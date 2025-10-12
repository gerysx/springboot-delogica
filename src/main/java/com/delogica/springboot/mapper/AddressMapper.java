package com.delogica.springboot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.delogica.springboot.dto.AddressInputDTO;
import com.delogica.springboot.dto.AddressOutputDTO;
import com.delogica.springboot.model.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(source = "customer.id", target = "customerId")
    AddressOutputDTO toDto(Address address);

    @Mapping(source = "customerId", target = "customer.id")
    Address toEntity(AddressInputDTO dto);
}

