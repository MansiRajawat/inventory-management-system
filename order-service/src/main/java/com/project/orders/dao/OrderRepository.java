package com.project.orders.dao;

import com.project.orders.model.Orders;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Orders, String> {
}
