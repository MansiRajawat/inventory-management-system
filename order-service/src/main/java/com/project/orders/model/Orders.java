package com.project.orders.model;

import lombok.Data;

import java.util.List;

@Data
public class Orders {
    private List<OrderDetails> orderDetails;
    private String customerName;
    private  String address;
    private String emailId;
    private int customerId;
    private int phoneNumber;
}
