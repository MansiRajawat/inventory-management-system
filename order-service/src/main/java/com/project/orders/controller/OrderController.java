package com.project.orders.controller;

import com.project.orders.model.OrderResponse;
import com.project.orders.model.Orders;
import com.project.orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController  // <-- Add this annotation
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService service;

    @PostMapping("/create")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody Orders orders) {
        OrderResponse orderResponse = service.processOrders(orders);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

    @GetMapping("/getAllOrders")
    public ResponseEntity<List<Orders>> viewOrders() {
        List<Orders> getOrderDetails = service.retrieveListOfOrders();
        return new ResponseEntity<>(getOrderDetails, HttpStatus.OK);
    }

    @GetMapping("/getOrder/{id}")
    public ResponseEntity<Optional<Orders>> viewOrdersById(@PathVariable int id) {
        Optional<Orders> retrieveOrder = service.getOrdersById(id);
        return ResponseEntity.ok(retrieveOrder);
    }

    @DeleteMapping("/deleteOrder/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable int id) {
        Optional<Orders> deleteOrder = service.deleteOrderById(id);
        if (deleteOrder.isPresent()) {
            return ResponseEntity.ok("Order Deleted");
        }
        return new ResponseEntity<>("Order Not Present In Database", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/deleteOrders")
    public ResponseEntity<?> deleteOrders(@RequestBody Orders orders) {
        try {
            Optional<Orders> deletedOrders = service.bulkOrdersDelete(orders);
            if (deletedOrders.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("deletedOrders", deletedOrders.get());
                response.put("message", deletedOrders.get().getOrderDetails().size() + " orders successfully deleted");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "deletion failed");
                response.put("message", "No orders were deleted");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to delete orders: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
