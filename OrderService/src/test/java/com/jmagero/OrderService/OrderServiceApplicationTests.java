package com.jmagero.OrderService;

import com.jmagero.OrderService.entity.Order;
import com.jmagero.OrderService.exception.CustomException;
import com.jmagero.OrderService.external.client.PaymentService;
import com.jmagero.OrderService.external.client.ProductService;
import com.jmagero.OrderService.external.intercept.TokenManager;
import com.jmagero.OrderService.external.request.PaymentRequest;
import com.jmagero.OrderService.external.response.ProductResponse;
import com.jmagero.OrderService.model.OrderRequest;
import com.jmagero.OrderService.model.OrderResponse;
import com.jmagero.OrderService.model.PaymentMode;
import com.jmagero.OrderService.model.PaymentResponse;
import com.jmagero.OrderService.repository.IOrderRepository;
import com.jmagero.OrderService.service.IOrderService;
import com.jmagero.OrderService.service.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest

class OrderServiceApplicationTests {
	@Mock
	private  IOrderRepository orderRepository;

	@Mock
	private  ProductService productService;

	@Mock
	private  PaymentService paymentService;

	@Mock
	private  RestTemplate restTemplate;

	public static final String AUTHORIZATION = "Authorization";
	@Mock
	private  TokenManager tokenManager;

	@InjectMocks
    IOrderService orderService = new OrderServiceImpl();

	@Test
	@DisplayName("Get Order - Success Scenario")
	void test_When_Order_Success() {
		String token = tokenManager.getAccessToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTHORIZATION, "Bearer " + token);
		HttpEntity<Void> request = new HttpEntity<>(headers);
		Order order = getMockOrder();

		//Mocking
		when(orderRepository.findById(anyLong()))
				.thenReturn(Optional.of(order));

		when(restTemplate.exchange("http://PRODUCTSERVICE/products/" + 1,HttpMethod.GET,request, ProductResponse.class))
				.thenReturn(getMockProduct());

		when(restTemplate.exchange("http://PAYMENTSERVICE/payments/" + 1, HttpMethod.GET, request, PaymentResponse.class))
				.thenReturn(getMockPayment());
		//Actual
		OrderResponse orderResponse =  orderService.getOrderDetails(1);

		//Verification
		verify(orderRepository,times(1)).findById(anyLong());
		verify(restTemplate, times(1)).exchange("http://PRODUCTSERVICE/products/" + 1,HttpMethod.GET,request, ProductResponse.class);
		verify(restTemplate, times(1)).exchange("http://PAYMENTSERVICE/payments/" + 1, HttpMethod.GET, request, PaymentResponse.class);

		//Assert
		assertNotNull(orderResponse);
		assertEquals(order.getId(), orderResponse.getOrderId());

	}

	@Test
	@DisplayName("Get Orders - Failure Scenario")
	void test_When_Get_Order_NOT_FOUND_then_Not_Found() {
		when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

		CustomException exception = assertThrows( CustomException.class, () -> orderService.getOrderDetails(1));

		assertEquals("NOT_FOUND", exception.getErrorCode());
		assertEquals(404, exception.getStatus());

		verify(orderRepository, times(1)).findById(anyLong());
	}

	@Test
	@DisplayName("Place Orders - Success Scenario")
	void test_When_Place_Order_Success() {
		Order order = getMockOrder();
		OrderRequest orderRequest = getMockOrderRequest();
		//Mocking
		when(orderRepository.save(any(Order.class))).thenReturn(order);
		when(productService.reduceQuantity(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
		when(paymentService.doPayment(any(PaymentRequest.class))).thenReturn(new ResponseEntity<>(1L,HttpStatus.OK));

		//Actual
		long orderId  = orderService.placeOrder(orderRequest);

		//verify
		verify(orderRepository, times(2)).save(any());
		verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
		verify(paymentService, times(1)).doPayment(any(PaymentRequest.class));


		//Assert
		assertEquals(order.getId(), orderId);
	}

	private PaymentRequest getMockPaymentRequest() {
		return PaymentRequest.builder()
				.amount(100)
				.paymentMode(PaymentMode.CASH)
				.orderId(1)
				.build();
	}

	private OrderRequest getMockOrderRequest() {
		return OrderRequest.builder()
				.paymentMode(PaymentMode.CASH)
				.productId(1)
				.quantity(10)
				.totalAmount(100)
				.build();
	}

	private ResponseEntity<PaymentResponse> getMockPayment() {
		return  new ResponseEntity<>(PaymentResponse.builder()
				.orderId(getMockOrder().getId())
				.status("ACCEPTED")
				.amount(100)
				.paymentDate(Instant.now())
				.paymentMode(PaymentMode.CASH)
				.paymentId(1)
				.build(), HttpStatus.OK);
	}

	private ResponseEntity<ProductResponse> getMockProduct() {
		return new ResponseEntity<>(ProductResponse.builder()
				.productName("Iphone")
				.price(100)
				.productId(1)
				.quantity(200)
				.build(), HttpStatus.OK);
	}

	private Order getMockOrder() {
		return   Order.builder()
				.quantity(2)
				.orderStatus("PLACED")
				.amount(100)
				.orderDate(Instant.now())
				.id(1)
				.productId(1)
				.build();
	}

}
