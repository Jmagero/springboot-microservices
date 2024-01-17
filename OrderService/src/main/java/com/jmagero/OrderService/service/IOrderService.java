package com.jmagero.OrderService.service;


import com.jmagero.OrderService.model.OrderRequest;
import com.jmagero.OrderService.model.OrderResponse;

public interface IOrderService {

    long placeOrder(OrderRequest productRequest);

    OrderResponse getOrderDetails(long orderId);

}
