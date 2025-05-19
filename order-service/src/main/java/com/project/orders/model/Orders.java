package com.project.orders.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "orders")
public class Orders {
    @Id
    private String id;
    private List<OrderDetails> orderDetails;
    private int customerId;
    private String customerName;
    private  String address;
    private String emailId;
    private int phoneNumber;
}
