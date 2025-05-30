package com.project.inventory.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Unique identifier of the product", example = "736692")
    private Long productId;

    @Column(name = "productName")
    @Schema(description = "Name Of the product", example = "Samsung S24 Ultra")
    private String productName;

    @Column(name = "productPrice")
    @Schema(description = "Price Of the Product", example = "75000.0")
    private double productPrice;

    @Column(name = "productCount")
    @Schema(description = "Inventory Count", example = "75")
    private int productCount;

    public ProductDetails(Long productId, String productName, double productPrice, int productCount) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productCount = productCount;
    }
    public ProductDetails() {
        super();
        // TODO Auto-generated constructor stub
    }

}
