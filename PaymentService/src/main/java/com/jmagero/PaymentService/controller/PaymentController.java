package com.jmagero.PaymentService.controller;

import com.jmagero.PaymentService.model.PaymentRequest;
import com.jmagero.PaymentService.model.PaymentResponse;
import com.jmagero.PaymentService.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final IPaymentService paymentService;

    @PostMapping
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest){
        return new ResponseEntity<>(paymentService.doPayment(paymentRequest), HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public  ResponseEntity<PaymentResponse> getPaymentDetails(@PathVariable("orderId") long orderId){
        return new ResponseEntity<>(paymentService.getPaymentDetails(orderId), HttpStatus.OK);
    }

}
