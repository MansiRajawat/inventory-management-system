package com.project.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data

@Entity(name = "product")
@Table(name = "product")
public class ProductDetails {
    @Id
    @Column(name = "productId")
    private Long productId;
    @Column(name = "productName")
    private String productName;
    @Column(name = "productPrice")
    private double productPrice;
    @Column(name = "productCount")
    private int productCount;
}
