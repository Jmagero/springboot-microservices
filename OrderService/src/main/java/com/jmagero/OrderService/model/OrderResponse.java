package com.jmagero.OrderService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private long orderId;
    private long amount;
    private Instant orderDate;
    private String orderStatus;
    private ProductDetails productDetails;
    private PaymentDetails paymentDetails;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static  class ProductDetails {
        private long productId;
        private String productName;
        private long price;
        private  long quantity;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PaymentDetails {
        private long paymentId;
        private PaymentMode paymentMode;
        private Instant paymentDate;
        private String status;
    }
}
