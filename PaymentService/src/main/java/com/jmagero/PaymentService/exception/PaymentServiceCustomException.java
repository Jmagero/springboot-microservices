package com.jmagero.PaymentService.exception;

import com.jmagero.PaymentService.model.ErrorResponse;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Data
public class PaymentServiceCustomException extends RuntimeException {
    private String errorCode;
    public PaymentServiceCustomException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    @ControllerAdvice
    public static class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {
            @ExceptionHandler(PaymentServiceCustomException.class)
            public ResponseEntity<ErrorResponse> handleProductServiceException(PaymentServiceCustomException ex){
              return new ResponseEntity<>(new ErrorResponse().builder()
                      .errorMessage(ex.getMessage())
                              .errorCode(ex.getErrorCode())
                                      .errorMessage(ex.getMessage())
                                              .build(), HttpStatus.NOT_FOUND);
            }
    }
}
