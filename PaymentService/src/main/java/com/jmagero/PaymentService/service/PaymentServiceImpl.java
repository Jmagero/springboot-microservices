package com.jmagero.PaymentService.service;

import com.jmagero.PaymentService.entity.TransactionDetails;
import com.jmagero.PaymentService.exception.PaymentServiceCustomException;
import com.jmagero.PaymentService.model.PaymentMode;
import com.jmagero.PaymentService.model.PaymentRequest;
import com.jmagero.PaymentService.model.PaymentResponse;
import com.jmagero.PaymentService.repository.TransactionDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentServiceImpl implements IPaymentService {

    private final TransactionDetailsRepository detailsRepository;
    @Override
    public Long doPayment(PaymentRequest paymentRequest) {
        log.info("Recording Payment Details: {}", paymentRequest);
        TransactionDetails  transactionDetails =
                TransactionDetails.builder()
                .orderId(paymentRequest.getOrderId())
                .paymentDate(Instant.now())
                .paymentMode(paymentRequest.getPaymentMode().name())
                .paymentStatus("SUCCESS")
                .amount(paymentRequest.getAmount())
                .referenceNumber(paymentRequest.getReferenceNumber())
                .build();
        detailsRepository.save(transactionDetails);
        log.info("Transaction Completed with Id: {}", transactionDetails.getId());
        return transactionDetails.getId();
    }

    @Override
    public PaymentResponse getPaymentDetails(long orderId) {
        log.info("Getting payment details for Order id: {}", orderId);
        TransactionDetails transactionDetails = detailsRepository.findByOrderId(orderId);
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .status(transactionDetails.getPaymentStatus())
                .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
                .paymentDate(transactionDetails.getPaymentDate())
                .amount(transactionDetails.getAmount())
                .orderId(transactionDetails.getOrderId())
                .paymentId(transactionDetails.getId())
                .build();
        return paymentResponse;
    }
}
