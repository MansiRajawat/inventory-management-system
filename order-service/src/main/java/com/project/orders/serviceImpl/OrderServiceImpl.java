package com.project.orders.serviceImpl;

import com.project.orders.model.OrderResponse;
import com.project.orders.model.Orders;

import java.util.List;
import java.util.Optional;

public interface OrderServiceImpl {
public List<Orders> retrieveListOfOrders();
public Optional<Orders> getOrdersById(int orderId);
public Optional<Orders> deleteOrderById(int orderId);
public Optional<OrderResponse> bulkOrdersDelete(Orders orders);
}
