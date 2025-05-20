package com.project.orders.controller;

import com.project.orders.model.OrderDetailsResponse;
import com.project.orders.model.OrderResponse;
import com.project.orders.model.Orders;
import com.project.orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService service;
    @PostMapping("/create")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody Orders orders){
        OrderResponse orderResponse = service.processOrders(orders);

        return  new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }
    @GetMapping("/getAllOrders")
    public ResponseEntity<List<Orders>> viewOrders(){
        List<Orders> getOrderDetails =service.retrieveListOfOrders();
        return new ResponseEntity<>(getOrderDetails, HttpStatus.OK);
    }

    @GetMapping("/getOrder/{id}")
    public ResponseEntity<Optional<Orders>> viewOrdersById(@PathVariable int id){
        Optional<Orders> retrieveOrder = service.getOrdersById(id);
        return ResponseEntity.ok(retrieveOrder);
    }

    @DeleteMapping("/deleteOrder/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable int id){
        Optional<Orders> deleteOrder = service.deleteOrderById(id);
        if (deleteOrder.isPresent()) {
            return ResponseEntity.ok("Order Deleted");
        }
        return new ResponseEntity<>("Order Not Present In Database", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/deleteOrders")
    public ResponseEntity<Orders> deleteOrders(){
        return null;
    }
}
