package com.delogica.springboot.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.delogica.springboot.dto.ProductInputDTO;
import com.delogica.springboot.dto.ProductOutputDTO;
import com.delogica.springboot.exceptions.NotFoundException;
import com.delogica.springboot.exceptions.ResourceAlreadyExistsException;
import com.delogica.springboot.mapper.ProductMapper;
import com.delogica.springboot.model.Product;
import com.delogica.springboot.repository.ProductRepository;
import com.delogica.springboot.service.interfaces.ProductService;
import com.delogica.springboot.utils.Pageables;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de productos que orquesta operaciones de consulta y comando
 * Usa ProductRepository para la persistencia y ProductMapper para el mapeo DTO
 * ↔ entidad
 */
@Service
@Validated
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final Pageables pageables;

    /**
     * Devuelve una página de productos según los criterios de paginación.
     * Si el pageable no trae sort, aplica el orden por defecto (name ASC, id DESC).
     */
    @Override
    public Page<ProductOutputDTO> findAll(Pageable pageable) {
        Pageable effective = pageables.withDefaultSort(pageable, pageables.productDefaultSort());
        return productRepository.findAll(effective).map(productMapper::toDto);
    }

    /**
     * Busca productos cuyo nombre contenga el texto indicado, ignorando mayúsculas
     *
     * @param name fragmento de nombre a buscar
     * @return lista de productos que coinciden, mapeados a DTO
     */
    @Override
    public List<ProductOutputDTO> findByName(@NotBlank String name) {
        List<Product> productName = productRepository.findByNameContainingIgnoreCase(name);

        return productName.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve todos los productos marcados como activos
     *
     * @return lista de productos activos mapeados a DTO
     */
    @Override
    public List<ProductOutputDTO> findByIsActiveTrue() {
        List<Product> productActive = productRepository.findByActiveTrue();

        return productActive.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo producto si el SKU no existe
     *
     * @param dto datos de entrada del producto
     * @return producto creado mapeado a DTO
     * @throws ResourceAlreadyExistsException si ya existe un producto con el SKU
     *                                        indicado
     */
    @Override
    public ProductOutputDTO create(@Valid @NotNull ProductInputDTO dto) {

        Optional<Product> existingProduct = productRepository.findBySku(dto.getSku());

        if (existingProduct.isPresent()) {
            throw new ResourceAlreadyExistsException("Ya existe un producto con el SKU: " + dto.getSku());
        }

        Product product = productMapper.toEntity(dto);

        Product savedProduct = productRepository.save(product);

        ProductOutputDTO outputDTO = productMapper.toDto(savedProduct);

        return outputDTO;
    }

    /**
     * Actualiza un producto existente por id
     *
     * @param id  identificador del producto a actualizar
     * @param dto datos a modificar del producto
     * @return producto actualizado mapeado a DTO
     * @throws NotFoundException si no existe un producto con el id indicado
     */
    @Override
    public ProductOutputDTO update(@NotNull @Positive Long id, @Valid @NotNull ProductInputDTO dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado con id: " + id));

        // Evita duplicados de SKU al actualizar
        if (!product.getSku().equalsIgnoreCase(dto.getSku())) {
            productRepository.findBySku(dto.getSku())
                    .ifPresent(p -> {
                        throw new ResourceAlreadyExistsException(
                                "Ya existe un producto con el SKU: " + dto.getSku());
                    });
        }

        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setActive(dto.getActive());

        return productMapper.toDto(productRepository.save(product));

    }

    /**
     * Desactiva lógicamente un producto marcándolo como no activo
     *
     * @param id identificador del producto a desactivar
     * @throws NotFoundException si no existe un producto con el id indicado
     */
    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado con id: " + id));

        product.setActive(false);
        productRepository.save(product);
    }

}
