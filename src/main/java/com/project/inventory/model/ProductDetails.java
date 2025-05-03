package com.project.inventory.model;

import lombok.Data;

@Data
public class ProductDetails {
    private int productId;
    private String productName;
    private double productPrice;
    private int productCount;
}
