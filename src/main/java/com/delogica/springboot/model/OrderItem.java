package com.delogica.springboot.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name= "orders_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
	private Order order;
	
	@ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
	private Product product;
	
	private int quantity;
	
	private BigDecimal unitPrice;
	
	public BigDecimal getLineTotal() {
	    return unitPrice.multiply(BigDecimal.valueOf(quantity));
	}

}
