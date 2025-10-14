package com.delogica.springboot.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.delogica.springboot.dto.ProductInputDTO;
import com.delogica.springboot.dto.ProductOutputDTO;
import com.delogica.springboot.exceptions.NotFoundException;
import com.delogica.springboot.exceptions.ResourceAlreadyExistsException;
import com.delogica.springboot.mapper.ProductMapper;
import com.delogica.springboot.model.Product;
import com.delogica.springboot.repository.ProductRepository;
import com.delogica.springboot.service.interfaces.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Page<ProductOutputDTO> findAll(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(productMapper::toDto);
    }

    @Override
    public List<ProductOutputDTO> findByName(String name) {
        List<Product> productName = productRepository.findByNameContainingIgnoreCase(name);

        return productName.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductOutputDTO> findByIsActiveTrue() {
        List<Product> productActive = productRepository.findByActiveTrue();

        return productActive.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductOutputDTO create(ProductInputDTO dto) {

        Optional<Product> existingProduct = productRepository.findBySku(dto.getSku());

        if(existingProduct.isPresent()){
            throw new ResourceAlreadyExistsException("Ya existe un producto con el SKU: " + dto.getSku());
        }
       
        Product product = productMapper.toEntity(dto);

        Product savedProduct = productRepository.save(product);

        ProductOutputDTO outputDTO = productMapper.toDto(savedProduct);

        return outputDTO;
    }

    @Override
    public ProductOutputDTO update(Long id, ProductInputDTO dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado con id: " + id));

        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setActive(dto.getActive());

        Product updatedProduct = productRepository.save(product);

    return productMapper.toDto(updatedProduct);

    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
        .orElseThrow(()-> new NotFoundException("Producto no encontrado con id: " + id));

        product.setActive(false);
        productRepository.save(product);
    }

}
