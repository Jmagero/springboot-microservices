package com.jmagero.OrderService.service;

import com.jmagero.OrderService.external.client.PaymentService;
import com.jmagero.OrderService.external.client.ProductService;
import com.jmagero.OrderService.external.intercept.TokenManager;
import com.jmagero.OrderService.repository.IOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class OrderServiceImplTest {
    @Mock
    private  IOrderRepository orderRepository;

    @Mock
    private  ProductService productService;
    @Mock
    private  PaymentService paymentService;
    @Mock
    private  RestTemplate restTemplate;
//    public static final String AUTHORIZATION = "Authorization";
    @Mock
    private  TokenManager tokenManager;

    @InjectMocks
    IOrderService orderService;



    @Test
    @DisplayName("Get Order - Success Scenario")
    void test_When_Order_Success() {
        //mocking
        //actual
        //verification
        //assertion
    }
}