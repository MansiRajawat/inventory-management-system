package com.project.orders.service;

import com.project.orders.dao.OrderRepository;
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

                if (productDetails == null) {
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
        if (hasFailure) {
            restoreProductDetails(reservedProducts);
        } else {
            order.setOrderDetails(processedDetails);
            orderRepository.save(order);
        }

        OrderResponse buildResponse = buildResponse(order, processedDetails);
        if (hasFailure) {
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

    private static OrderResponse buildResponse(Orders order, List<OrderDetails> processedDetails) {
        OrderResponse response = new OrderResponse();
        response.setCustomerId(order.getCustomerId());
        response.setCustomerName(order.getCustomerName());
        response.setAddress(order.getAddress());
        response.setEmailId(order.getEmailId());
        response.setPhoneNumber(order.getPhoneNumber());
        response.setOrderDetailsResponses(new ArrayList<>());

        for (OrderDetails detail : processedDetails) {
            int uuidOrderId = Math.abs(UUID.randomUUID().hashCode());
            OrderDetailsResponse odp = new OrderDetailsResponse();
            odp.setOrderId(uuidOrderId);
            odp.setOrderName(detail.getOrderName());
            odp.setPrice(detail.getPrice());
           // odp.setProductId(detail.getProductId());
            odp.setQuantity(detail.getQuantity());
            response.getOrderDetailsResponses().add(odp);

        }
        return response;
    }

    @Override
    public List<Orders> retrieveListOfOrders() {
        return Optional.ofNullable(orderRepository.findAll())
                .orElse(List.of());
    }

    @Override
    public Optional<Orders> getOrdersById(int orderId) {
        List<Orders> allOrders = orderRepository.findAll();

        return allOrders.stream()
                .filter(order -> order.getOrderDetails().stream()
                        .anyMatch(detail -> detail.getOrderId() == orderId))
                .findFirst();
        }

    @Override
    public Optional<Orders> deleteOrderById(int orderId) {
        Optional<Orders> ordersOpt = getOrdersById(orderId);
        ordersOpt.ifPresent(orderRepository::delete);
        return ordersOpt;
    }

    @Override
    public Optional<Orders> bulkOrdersDelete(Orders orders) {

        List<Integer> deletedOrderIds = new ArrayList<>();
        List<OrderDetails> deletedOrdersList = new ArrayList<>();
        List<Integer> orderIds = orders.getOrderDetails().stream()
                .map(i -> i.getOrderId())
                .collect(Collectors.toList());

        Optional<Orders> validateCustomer = orderRepository.findById(String.valueOf(orders.getCustomerId()));

        if(validateCustomer.isPresent()){
            Orders existingOrders = validateCustomer.get();
            List<OrderDetails> remainingOrders = new ArrayList<>();
            for (OrderDetails orderDetail : existingOrders.getOrderDetails()) {
                if (!orderIds.contains(orderDetail.getOrderId())) {
                    remainingOrders.add(orderDetail);
                } else {
                    deletedOrderIds.add(orderDetail.getOrderId());
                    deletedOrdersList.add(orderDetail);
                }
            }

            if (deletedOrderIds.size() > 0) {
                existingOrders.setOrderDetails(remainingOrders);
                if (remainingOrders.isEmpty()) {
                    orderRepository.delete(existingOrders);
                } else {
                    orderRepository.save(existingOrders);
                }
            }

            Orders deletedOrdersResult = new Orders();
            deletedOrdersResult.setId(existingOrders.getId());
            deletedOrdersResult.setCustomerId(existingOrders.getCustomerId());
            deletedOrdersResult.setCustomerName(existingOrders.getCustomerName());
            deletedOrdersResult.setAddress(existingOrders.getAddress());
            deletedOrdersResult.setEmailId(existingOrders.getEmailId());
            deletedOrdersResult.setPhoneNumber(existingOrders.getPhoneNumber());
            deletedOrdersResult.setOrderDetails(deletedOrdersList);

            return Optional.of(deletedOrdersResult);
        }
        return Optional.empty();
    }
}


