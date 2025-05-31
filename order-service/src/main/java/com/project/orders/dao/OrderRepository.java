package com.project.orders.dao;

import com.project.orders.model.Orders;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface OrderRepository extends MongoRepository<Orders, String> {
    Optional<Orders> findByCustomerId(int customerId);

    @Query("{'orderDetails.orderId': ?0}")
    Optional<Orders> findByOrderDetailsOrderId(int orderId);
}
