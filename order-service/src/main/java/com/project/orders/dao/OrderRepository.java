package com.project.orders.dao;

import com.project.orders.model.Orders;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrderRepository extends MongoRepository<Orders, String> {
    Optional<Orders> findByCustomerId(int customerId);
}
