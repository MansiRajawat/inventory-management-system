package com.project.orders.model;

import lombok.Data;

@Data
public class OrderDetails {
    private int orderId;
    private String orderName;
    private double price;
    private int productId;
    private int quantity;

}
