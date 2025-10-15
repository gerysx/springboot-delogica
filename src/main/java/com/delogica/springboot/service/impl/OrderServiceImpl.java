package com.delogica.springboot.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.delogica.springboot.dto.OrderInputDTO;
import com.delogica.springboot.dto.OrderItemInputDTO;
import com.delogica.springboot.dto.OrderOutputDTO;
import com.delogica.springboot.exceptions.NotFoundException;
import com.delogica.springboot.mapper.OrderItemMapper;
import com.delogica.springboot.mapper.OrderMapper;

import com.delogica.springboot.model.Order;
import com.delogica.springboot.model.OrderItem;
import com.delogica.springboot.model.OrderStatus;
import com.delogica.springboot.repository.OrderRepository;
import com.delogica.springboot.service.interfaces.OrderService;
import com.delogica.springboot.utils.Pageables;

import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Servicio de Orders que orquesta operaciones de consulta
 * Usa OrderRepository para la persistencia y OrderMapper y OrderItemMapper para
 * el mapeo DTO ↔ entidad
 */
@Service
@Validated
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final Pageables pageables;
    private final OrderItemMapper orderItemMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper,
            Pageables pageables, OrderItemMapper orderItemMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.pageables = pageables;
        this.orderItemMapper = orderItemMapper;
    }

    private static LocalDateTime[] normalize(LocalDateTime start, LocalDateTime end) {
        return end.isBefore(start) ? new LocalDateTime[] { end, start } : new LocalDateTime[] { start, end };
    }

    /**
     * Devuelve las órdenes de un cliente con paginación
     *
     * @param customerId id del cliente, debe ser > 0
     * @param pageable   criterios de paginación y orden, si no trae sort se aplica
     *                   el orden por defecto del recurso
     * @return página de órdenes mapeadas a DTO
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OrderOutputDTO> findByCustomerId(@NotNull @Positive Long customerId, Pageable pageable) {

        Pageable effective = pageables.withDefaultSort(pageable, pageables.orderDefaultSort());
        return orderRepository
                .findByCustomerId(customerId, effective)
                .map(orderMapper::toDto);
    }

    /**
     * Devuelve órdenes dentro de un rango de fechas, con paginación
     * Si el rango viene invertido, se normaliza para que start ≤ end
     *
     * @param start    fecha y hora de inicio inclusive
     * @param end      fecha y hora de fin inclusive
     * @param pageable criterios de paginación y orden, si no trae sort se aplica el
     *                 orden por defecto del recurso
     * @return página de órdenes mapeadas a DTO
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OrderOutputDTO> findByOrderDateBetween(@NotNull LocalDateTime start, @NotNull LocalDateTime end,
            Pageable pageable) {

        LocalDateTime[] r = normalize(start, end);

        Pageable effective = pageables.withDefaultSort(pageable, pageables.orderDefaultSort());

        return orderRepository
                .findByOrderDateBetween(r[0], r[1], effective)
                .map(orderMapper::toDto);
    }

    /**
     * Crea una nueva orden a partir del DTO de entrada
     * Fija estado inicial CREATED, enlaza ítems y delega en hooks del modelo el
     * cálculo del total y fecha
     *
     * @param input DTO de entrada validado
     * @return orden creada mapeada a DTO
     */
    @Override
    @Transactional
    public OrderOutputDTO create(@Valid @NotNull OrderInputDTO input) {

        Order order = orderMapper.toEntity(input);

        order.setStatus(OrderStatus.CREATED);

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                item.setOrder(order);
            }
        }

        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    /**
     * Actualiza una orden existente del cliente indicado
     * Reemplaza los ítems aplicando orphanRemoval, recalculándose total
     * en @PreUpdate
     *
     * @param customerId id del cliente propietario de la orden
     * @param orderId    id de la orden a actualizar
     * @param input      DTO de entrada validado con los nuevos datos de la orden
     * @return orden actualizada mapeada a DTO
     * @throws NotFoundException        si la orden no existe
     * @throws IllegalArgumentException si la orden no pertenece al cliente indicado
     */
    @Override
    @Transactional
    public OrderOutputDTO update(
            @NotNull @Positive Long customerId,
            @NotNull @Positive Long orderId,
            @Valid @NotNull OrderInputDTO input) {

        Order existing = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order no encontrado con id: " + orderId));

        if (existing.getCustomer() == null || !existing.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("El pedido no pertenece al customer indicado");
        }

        // Reemplazar ítems (orphanRemoval = true se encarga de borrar los antiguos)
        existing.getItems().clear();
        if (input.getItems() != null && !input.getItems().isEmpty()) {
            for (OrderItemInputDTO itemDto : input.getItems()) {
                OrderItem item = orderItemMapper.toEntity(itemDto);
                item.setOrder(existing);
                existing.getItems().add(item);
            }
        }

        Order saved = orderRepository.save(existing);
        return orderMapper.toDto(saved);
    }

    /**
     * Cambia el estado de una orden del cliente indicado
     * Aplica las reglas de transición definidas en el método de dominio
     * changeStatus
     *
     * @param customerId id del cliente propietario de la orden
     * @param orderId    id de la orden
     * @param newStatus  nuevo estado a aplicar
     * @return orden con el nuevo estado mapeada a DTO
     * @throws NotFoundException        si la orden no existe
     * @throws IllegalStateException    si la transición de estado no es válida
     * @throws IllegalArgumentException si la orden no pertenece al cliente indicado
     */
    @Override
    @Transactional
    public OrderOutputDTO updateStatus(
            @NotNull @Positive Long customerId,
            @NotNull @Positive Long orderId,
            @NotNull OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order no encontrada"));

        if (order.getCustomer() == null || !order.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("El pedido no pertenece al customer indicado");
        }

        order.changeStatus(newStatus);
        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    /**
     * Devuelve órdenes de un cliente dentro de un rango de fechas, con paginación
     * Si el rango viene invertido, se normaliza para que start ≤ end
     *
     * @param customerId id del cliente, debe ser > 0
     * @param start      fecha y hora de inicio inclusive
     * @param end        fecha y hora de fin inclusive
     * @param pageable   criterios de paginación y orden, si no trae sort se aplica
     *                   el orden por defecto del recurso
     * @return página de órdenes mapeadas a DTO
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OrderOutputDTO> findByCustomerIdAndOrderDateBetween(
            @NotNull @Positive Long customerId,
            @NotNull LocalDateTime start,
            @NotNull LocalDateTime end,
            Pageable pageable) {

        LocalDateTime[] r = normalize(start, end);

        Pageable effective = pageables.withDefaultSort(pageable, pageables.orderDefaultSort());

        return orderRepository
                .findByCustomerIdAndOrderDateBetween(customerId, r[0], r[1], effective)
                .map(orderMapper::toDto);
    }

}
