package com.project.orders.service;

import com.project.orders.dao.OrderRepository;
import com.project.orders.helper.ResponseBuilderHelper;
import com.project.orders.model.*;
import com.project.orders.serviceImpl.OrderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderService implements OrderServiceImpl {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ResponseBuilderHelper helper;

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final String INVENTORY_URL = "http://localhost:8080/inventory";

    public OrderResponse processOrders(Orders ordersDetails) {
        Orders order = new Orders();
        order.setCustomerName(ordersDetails.getCustomerName());
        order.setAddress(ordersDetails.getAddress());
        order.setEmailId(ordersDetails.getEmailId());
        order.setCustomerId(ordersDetails.getCustomerId());
        order.setPhoneNumber(ordersDetails.getPhoneNumber());

        List<OrderDetails> processedDetails = new ArrayList<>();
        List<OrderDetails> reservedProducts = new ArrayList<>();
        List<String> failureMessages = new ArrayList<>();
        boolean hasFailure = false;

        for (OrderDetails orderDetails : ordersDetails.getOrderDetails()) {
            try {
                ResponseEntity<ProductDetails> productResponse = restTemplate.getForEntity(
                        INVENTORY_URL + "/getProductById/" + orderDetails.getProductId(),
                        ProductDetails.class);
                ProductDetails productDetails = productResponse.getBody();

                if ( productDetails == null ) {
                    logger.error("Product not found for ID: {}", orderDetails.getProductId());
                    failureMessages.add("Product not found for productId: " + orderDetails.getProductId());
                    hasFailure = true;
                    continue;
                }

                if ( productDetails.getProductCount() < orderDetails.getQuantity() ) {
                    logger.warn("Insufficient stock for product ID {}. Available: {}, Requested: {}",
                            productDetails.getProductId(), productDetails.getProductCount(), orderDetails.getQuantity());
                    failureMessages.add("Product is out of stock.");
                    hasFailure = true;
                    continue;
                }

                //check for the count of the inventory reaches threshold
                if ( productDetails.getProductCount() == 5 ) {
                    logger.warn("Threshold alert: Only 5 items left for product ID {}", productDetails.getProductId());
                    failureMessages.add("Only 5 items left! Proceeding with order.");
                }

                if ( orderDetails.getOrderId() == 0 ) {
                    orderDetails.setOrderId(Math.abs(UUID.randomUUID().hashCode()));
                }

                //Deduct stock from the inventory
                ResponseEntity<ProductDetails> updateResponse = updateProductInventoryDetails(orderDetails, productDetails);
                if ( !updateResponse.getStatusCode().is2xxSuccessful() ) {
                    logger.error("Inventory update failed for product ID {}", productDetails.getProductId());
                    failureMessages.add("Failed to update inventory.");
                    hasFailure = true;
                    continue;
                }
                reservedProducts.add(orderDetails);

            } catch (Exception e) {
                logger.error("Exception while processing product ID {}", orderDetails.getProductId(), e);
                failureMessages.add("Internal error for productId: " + orderDetails.getProductId());
                hasFailure = true;
            }

            processedDetails.add(orderDetails);
        }
        if ( hasFailure ) {
            restoreProductDetails(reservedProducts);
        } else {
            order.setOrderDetails(processedDetails);
            orderRepository.save(order);
            if ( ordersDetails.getEmailId() != null && !ordersDetails.getEmailId().isEmpty() ) {
                try {
                   // emailService.sendOrderConfirmationEmail(ordersDetails.getEmailId(), helper.buildResponse(order, processedDetails));
                   // logger.info("Confirmation email sent to {}", ordersDetails.getEmailId());
                } catch (Exception e) {
                    logger.error("Failed to send email to {}", ordersDetails.getEmailId(), e);
                }
            }
        }

        OrderResponse buildResponse = helper.buildResponse(order, processedDetails);
        if ( hasFailure ) {
            buildResponse.setOrderMessage("Order processing failed: " + String.join("; ", failureMessages));
        } else {
            buildResponse.setOrderMessage("Order processed successfully.");
        }
        return buildResponse;
    }

    private ResponseEntity<ProductDetails> updateProductInventoryDetails(OrderDetails orderDetails, ProductDetails productDetails) {
        int updatedCount = productDetails.getProductCount() - orderDetails.getQuantity();
        productDetails.setProductCount(updatedCount);

        HttpEntity<ProductDetails> requestUpdate = new HttpEntity<>(productDetails);
        ResponseEntity<ProductDetails> updateResponse = restTemplate.exchange(
                INVENTORY_URL + "/updateProductDetails/" + productDetails.getProductId(),
                HttpMethod.PUT,
                requestUpdate,
                ProductDetails.class
        );
        return updateResponse;
    }

    private void restoreProductDetails(List<OrderDetails> reservedProducts) {
        for (OrderDetails reserved : reservedProducts) {
            try {
                ResponseEntity<String> restoreResponse = restTemplate.exchange(
                        INVENTORY_URL + "/restoreProductCount/" + reserved.getProductId()
                                + "?quantity=" + reserved.getQuantity(),
                        HttpMethod.PUT,
                        null,
                        String.class
                );
                logger.info("Inventory restored for product ID {}: {}", reserved.getProductId(),
                        restoreResponse.getStatusCode());
            } catch (Exception e) {
                logger.error("Failed to restore inventory for product ID {}", reserved.getProductId(), e);
            }
        }
    }

    @Override
    public List<OrderResponse> retrieveListOfOrders() {
        return Optional.ofNullable(orderRepository.findAll()).map(
                        orders ->
                            orders.stream()
                                    .map(
                                    order -> helper.buildResponse(order, order.getOrderDetails()))
                                    .collect(Collectors.toList()))
                .orElse(List.of());

    }

    @Override
    public Optional<OrderResponse> getOrdersById(int orderId) {
        return orderRepository.findByOrderDetailsOrderId(orderId)
                .map(order -> {
                    List<OrderDetails> filteredDetails = order.getOrderDetails().stream()
                            .filter(detail -> detail.getOrderId() == orderId)
                            .collect(Collectors.toList());

                    return helper.buildResponse(order, filteredDetails);
                });
    }

    @Override
    public Optional<Orders> deleteOrderById(int orderId) {
        Optional<Orders> ordersOpt = validateOrder(orderId);
        ordersOpt.ifPresent(orderRepository::delete);
        return ordersOpt;
    }

    private Optional<Orders> validateOrder(int orderId) {
        return orderRepository.findByOrderDetailsOrderId(orderId)
                .map(order -> {
                    List<OrderDetails> filteredDetails = order.getOrderDetails().stream()
                            .filter(detail -> detail.getOrderId() == orderId)
                            .collect(Collectors.toList());
                    return order;
                });
    }


    @Override
    public Optional<OrderResponse> bulkOrdersDelete(Orders orders) {

        List<Integer> deletedOrderIds = new ArrayList<>();
        List<OrderDetails> deletedOrdersList = new ArrayList<>();
        List<Integer> orderIds = orders.getOrderDetails().stream()
                .map(i -> i.getOrderId())
                .collect(Collectors.toList());

        Optional<Orders> validateCustomer = orderRepository.findByCustomerId(orders.getCustomerId());

        if ( validateCustomer.isPresent() ) {
            Orders existingOrders = validateCustomer.get();
            List<OrderDetails> remainingOrders = new ArrayList<>();
            for (OrderDetails orderDetail : existingOrders.getOrderDetails()) {
                if ( !orderIds.contains(orderDetail.getOrderId()) ) {
                    remainingOrders.add(orderDetail);
                } else {
                    deletedOrderIds.add(orderDetail.getOrderId());
                    deletedOrdersList.add(orderDetail);
                }
            }

            if ( deletedOrderIds.size() > 0 ) {
                existingOrders.setOrderDetails(remainingOrders);
                if ( remainingOrders.isEmpty() ) {
                    orderRepository.delete(existingOrders);
                } else {
                    orderRepository.save(existingOrders);
                }
                restoreProductDetails(deletedOrdersList);
            }
            return Optional.of(helper.buildResponse(existingOrders, deletedOrdersList));
        }
        return Optional.empty();
    }
}


