package com.Ecommerce.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleOrderValidation(
            OrderValidationException ex) {

        ApiResponse<Object> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setStatus("ORDER_FAILED");
        response.setMessage("Some items are unavailable");
        response.setErrors(ex.getErrors());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
