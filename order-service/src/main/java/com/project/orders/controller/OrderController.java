package com.project.orders.controller;

import com.project.orders.config.AppConfig;
import com.project.orders.model.Orders;
import com.project.orders.model.ProductDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


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
