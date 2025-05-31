package com.project.orders.model;

import lombok.Data;

@Data
public class OrderDetailsResponse {
    private int orderId;
    private String orderName;
    private double price;
    private int quantity;
}
