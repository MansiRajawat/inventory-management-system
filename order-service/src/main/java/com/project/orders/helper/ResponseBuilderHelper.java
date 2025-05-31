package com.project.orders.helper;

import com.project.orders.model.OrderDetails;
import com.project.orders.model.OrderDetailsResponse;
import com.project.orders.model.OrderResponse;
import com.project.orders.model.Orders;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResponseBuilderHelper {

    public OrderResponse buildResponse(Orders order, List<OrderDetails> processedDetails) {
        OrderResponse response = new OrderResponse();
        response.setCustomerId(order.getCustomerId());
        response.setCustomerName(StringUtils.isNotEmpty(order.getCustomerName()) ? order.getCustomerName() : "");
        response.setAddress(StringUtils.isNotEmpty(order.getAddress()) ? order.getAddress() : "");
        response.setEmailId(StringUtils.isNotEmpty(order.getEmailId()) ? order.getEmailId(): "");
        response.setPhoneNumber(order.getPhoneNumber());
        response.setOrderDetailsResponses(new ArrayList<>());

            /* commenting to implement the java8 streams */
//    for (OrderDetails detail : processedDetails) {
//        OrderDetailsResponse odp = new OrderDetailsResponse();
//        odp.setOrderId(detail.getOrderId());
//        odp.setOrderName(detail.getOrderName());
//        odp.setPrice(detail.getPrice());
//        odp.setQuantity(detail.getQuantity());
//        response.getOrderDetailsResponses().add(odp);
//
//    }
        response.setOrderDetailsResponses(CollectionUtils.isNotEmpty(processedDetails) ?
                processedDetails.stream().map(this::mapOrderDetailsToResponse).collect(Collectors.toList()) :
                new ArrayList<>());
        return response;
    }

    private OrderDetailsResponse mapOrderDetailsToResponse(OrderDetails orderDetails) {
        OrderDetailsResponse odp = new OrderDetailsResponse();
        odp.setOrderId(orderDetails.getOrderId());
        odp.setOrderName(orderDetails.getOrderName());
        odp.setPrice(orderDetails.getPrice());
        odp.setQuantity(orderDetails.getQuantity());
        return odp;
    }


}
