package com.project.orders.model;

import lombok.Data;

@Data
public class OrderDetails {
    private int orderId;
    private String orderDescription;
    private double price;
    private int productId;

}
