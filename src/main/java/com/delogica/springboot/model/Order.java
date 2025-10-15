package com.delogica.springboot.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO) // puedes cambiar a IDENTITY/SEQUENCE según tu BBDD
	private Long id;

	@ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
	@JoinColumn(name = "shipping_address_id")
	private Address shippingAddress;

	private LocalDateTime orderDate;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Column(precision = 19, scale = 2, nullable = false)
	private BigDecimal total = BigDecimal.ZERO;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> items = new ArrayList<>();

	// Hooks de ciclo de vida 
	@PrePersist
	protected void onCreate() {
		if (orderDate == null)
			orderDate = LocalDateTime.now();
		recomputeTotal();
		bindItemsBackref();
	}

	@PreUpdate
	protected void onUpdate() {
		recomputeTotal();
		bindItemsBackref();
	}

	// Helpers para mantener la relación consistente y el total actualizado
	public void addItem(OrderItem item) {
		items.add(item);
		item.setOrder(this);
		recomputeTotal();
	}

	public void removeItem(OrderItem item) {
		items.remove(item);
		item.setOrder(null);
		recomputeTotal();
	}

	private void bindItemsBackref() {
		if (items == null)
			return;
		for (OrderItem i : items) {
			if (i.getOrder() != this)
				i.setOrder(this);
		}
	}

	private void recomputeTotal() {
		if (items == null || items.isEmpty()) {
			total = BigDecimal.ZERO;
			return;
		}

		total = items.stream()
				.map(OrderItem::getSubtotal) 
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public void changeStatus(OrderStatus next) {
    if (next == null) throw new IllegalArgumentException("El estado no puede ser null");
    if (this.status == next) return; 

    switch (this.status) {
        case CREATED -> {
            // Válidas: CREATED → PAID → SHIPPED y CREATED → CANCELLED
            if (next != OrderStatus.PAID && next != OrderStatus.CANCELLED) {
                throw new IllegalStateException("Transición no válida desde CREATED a " + next);
            }
        }
        case PAID -> {
            // Válidas: PAID → SHIPPED y PAID → CANCELLED solo si no se ha enviado
            // Si hubiera un flag/fecha de envío adicional, aquí lo comprobarías
            if (next != OrderStatus.SHIPPED && next != OrderStatus.CANCELLED) {
                throw new IllegalStateException("Transición no válida desde PAID a " + next);
            }
        }
        case SHIPPED -> {
            // No se permite cancelar ni otros cambios tras SHIPPED
            throw new IllegalStateException("No se permiten transiciones desde SHIPPED");
        }
        case CANCELLED -> {
            // Un pedido cancelado no cambia de estado
            throw new IllegalStateException("No se permiten transiciones desde CANCELLED");
        }
        default -> throw new IllegalStateException("Estado actual desconocido: " + this.status);
    }

    this.status = next;
}

}
