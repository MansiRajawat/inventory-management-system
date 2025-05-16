package com.project.orders.controller;

import com.project.orders.model.Orders;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/orders")
public class OrderController {

    @PostMapping("/create")
    public ResponseEntity<Orders> placeOrder(@RequestBody Orders orders){
        return  null;
    }

    @GetMapping("/getAllOrders")
    public ResponseEntity<Orders> viewOrders(){
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Orders> viewOrdersById(@PathVariable int id){
        return null;
    }

    @DeleteMapping("/deleteOrder/{id}")
    public ResponseEntity<Orders> deleteOrder(@PathVariable int id){
        return null;
    }

    @DeleteMapping
    public ResponseEntity<Orders> deleteOrders(){
        return null;
    }
}
