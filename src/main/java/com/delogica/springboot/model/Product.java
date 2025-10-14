package com.delogica.springboot.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true, nullable = false)
	private String sku;

	@Column(nullable = false)
	@NotBlank
	private String name;

	@Column(nullable = false)
    @DecimalMin("0.0")
    private BigDecimal price;

	@Min(0)
	private Integer stock;

	private boolean active;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	public void discountStock(Integer cantidad) {
		if (cantidad <= 0)
			throw new IllegalArgumentException("La cantidad debe ser superior a 0");
		if (stock < cantidad)
			throw new IllegalStateException("Stock insuficiente");
		this.stock -= cantidad;
	}

	public void incrementStock(Integer cantidad) {
		if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser superior a 0");
		
		this.stock += cantidad;

	}

}
