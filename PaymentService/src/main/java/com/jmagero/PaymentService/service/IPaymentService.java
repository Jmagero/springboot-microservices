package com.jmagero.PaymentService.service;

import com.jmagero.PaymentService.model.PaymentRequest;
import com.jmagero.PaymentService.model.PaymentResponse;
import com.jmagero.PaymentService.repository.TransactionDetailsRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Repository;


public interface IPaymentService {
    Long doPayment(PaymentRequest paymentRequest);
    PaymentResponse getPaymentDetails(long orderId);
}
