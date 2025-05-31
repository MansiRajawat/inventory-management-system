package com.project.orders.model;

import lombok.Data;

import java.util.List;

@Data
public class OrderResponse {
    List<OrderDetailsResponse> orderDetailsResponses;
    private int customerId;
    private String customerName;
    private String address;
    private String emailId;
    private int phoneNumber;
    private String orderMessage;
}
