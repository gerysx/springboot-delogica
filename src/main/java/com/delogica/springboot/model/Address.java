package com.delogica.springboot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

	@Column(nullable = false)
	@NotBlank(message = "La línea 1 es obligatoria")
	private String line1;

	private String line2;

	@Column(nullable = false)
	@NotBlank(message = "La ciudad es obligatoria")
	private String city;

	@Column(nullable = false)
	@NotNull(message = "El código postal es obligatorio")
	private String postalCode;

	@Column(nullable = false)
	@NotBlank(message = "El país es obligatorio")
	private String country;
	
	private boolean isDefault;
	
	
}
