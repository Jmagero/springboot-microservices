package com.jmagero.OrderService.service;

import com.jmagero.OrderService.entity.Order;
import com.jmagero.OrderService.exception.CustomException;
import com.jmagero.OrderService.external.client.PaymentService;
import com.jmagero.OrderService.external.client.ProductService;
import com.jmagero.OrderService.external.intercept.TokenManager;
import com.jmagero.OrderService.external.request.PaymentRequest;
import com.jmagero.OrderService.external.response.ProductResponse;
import com.jmagero.OrderService.model.OrderRequest;
import com.jmagero.OrderService.model.OrderResponse;
import com.jmagero.OrderService.model.PaymentResponse;
import com.jmagero.OrderService.repository.IOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;


@Service
@Slf4j
public class OrderServiceImpl implements IOrderService {
  @Autowired
  private  IOrderRepository orderRepository;
  @Autowired
  private  ProductService productService;

  @Autowired
  private PaymentService paymentService;

  @Autowired
  private  RestTemplate restTemplate;
  public static final String AUTHORIZATION = "Authorization";
  @Autowired
  private  TokenManager tokenManager;


  @Override
  public long placeOrder(OrderRequest orderRequest) {
//    Order Entity -> Save the data with Status Order Created
//    Product service - Block products( Reduce the Quantity)
//    Payment Service -> Payments -> Success -> Complete , Else
//    Cancelled
    log.info("Placing Order Request: {}", orderRequest);
    productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());
    log.info("Creating order with status CREATED");
    Order order = Order.builder()
            .orderDate(Instant.now())
            .orderStatus("CREATED")
            .amount(orderRequest.getTotalAmount())
            .productId(orderRequest.getProductId())
            .quantity(orderRequest.getQuantity())
            .build();
    orderRepository.save(order);

    log.info("Order Placed successfully with Order id: {}" + order.getId());
    log.info("Calling the PaymentService to complete the payment: {}" );

    PaymentRequest paymentRequest = PaymentRequest.builder()
            .orderId(order.getId())
            .paymentMode(orderRequest.getPaymentMode())
            .amount(orderRequest.getTotalAmount())
            .build();

    String orderStatus = null;
    try {
      paymentService.doPayment(paymentRequest);
      log.info("Payment done Successfully. Changing the Order to status to PLACED");
      orderStatus = "PLACED";
    } catch (Exception ex){
      log.error("Error occurred in payment. Changing order to payment failed");
      orderStatus = "PAYMENT_FAILED";
    }
    order.setOrderStatus(orderStatus);
    orderRepository.save(order);
    return order.getId();
  }

  @Override
  public OrderResponse getOrderDetails(long orderId) {
    String token = tokenManager.getAccessToken();
    HttpHeaders headers = new HttpHeaders();
    headers.add(AUTHORIZATION, "Bearer " + token);
    HttpEntity<Void> request = new HttpEntity<>(headers);


    log.info("Get order details for Order Id: {}", orderId);
    Order order = orderRepository.findById(orderId)
            .orElseThrow( () -> new CustomException("Order not found for the order Id: " + orderId+ " ", "NOT_FOUND", 404 ));

    log.info("Invoking the ProductService to fetch product  for order id: {}", orderId);

    var  responseProduct  = restTemplate.exchange("http://PRODUCTSERVICE/products/" + orderId,HttpMethod.GET,request, ProductResponse.class);
    ProductResponse productResponse = responseProduct.getBody();

    log.info("Getting payment information from payment service");

    var responsePayment = restTemplate.exchange("http://PAYMENTSERVICE/payments/" + orderId, HttpMethod.GET, request, PaymentResponse.class);
    PaymentResponse paymentResponse = responsePayment.getBody();

    OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails.builder()
                    .price(productResponse.getPrice())
                            .productName(productResponse.getProductName())
            .productId(productResponse.getProductId())
            .build();
    OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails.builder()
            .paymentId(paymentResponse.getPaymentId())
            .paymentMode(paymentResponse.getPaymentMode())
            .status(paymentResponse.getStatus())
            .build();
    OrderResponse orderResponse;
    orderResponse = OrderResponse.builder()
            .orderDate(order.getOrderDate())
            .orderStatus(order.getOrderStatus())
            .orderId(order.getId())
            .amount(order.getAmount())
            .productDetails(productDetails)
            .paymentDetails(paymentDetails)
            .build();
    return orderResponse;
  }
}
