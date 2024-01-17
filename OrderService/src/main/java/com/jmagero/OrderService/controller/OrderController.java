package com.jmagero.OrderService.controller;

import com.jmagero.OrderService.external.intercept.ResourceServerProxy;
import com.jmagero.OrderService.model.OrderRequest;
import com.jmagero.OrderService.model.OrderResponse;
import com.jmagero.OrderService.service.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {

    private final OrderServiceImpl orderServiceImpl;
    private final ResourceServerProxy proxy;

    @Autowired
    public OrderController(OrderServiceImpl orderServiceImpl, ResourceServerProxy proxy) {
        this.orderServiceImpl = orderServiceImpl;
        this.proxy = proxy;
    }

    @PreAuthorize("hasAuthority('Customer')")
    @PostMapping("/placeOrder")
    public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest){
        Long orderId = orderServiceImpl.placeOrder(orderRequest);
        log.info("Order Id: {}", orderId);
        return new ResponseEntity<>(orderId, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer')" )
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(  @PathVariable("orderId") long orderId){
        return new ResponseEntity<>(orderServiceImpl.getOrderDetails(orderId),HttpStatus.OK);

    }
    @GetMapping("/test")
    public String test() {
        return proxy.callDemo();
    }


}
