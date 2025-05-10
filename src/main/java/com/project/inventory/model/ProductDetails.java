package com.project.inventory.model;

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
  //  @NotNull
    private Long productId;
    private String productName;
    private double productPrice;
    private int productCount;
}
