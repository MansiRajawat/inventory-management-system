package com.project.inventory.model;

import lombok.Data;

@Data
public class ProductResponse {
    private Long productId;
    private String productName;
    private double productPrice;
    private int productCount;
}
