package com.devprofileproject.devprofileaast.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice   //class handles exceptions men kol el controllers 
public class GlobalExceptionHandle 
{
    

    //404 not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound (ResourceNotFoundException ex)
    {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    
    //409 CONFLICT
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate (DuplicateResourceException ex)
    {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }
    

    //409 Buisness rule violation
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessRule (BusinessRuleException ex)
    {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }
    
    //400 Bad JSON in req body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleBadJson (HttpMessageNotReadableException ex)
    {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid request body");
    }

    //400 Validation (@NotBlank failed)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) 
    {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> fieldErrors.put(error.getField(), error.getCode()));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", 400);
        body.put("error", "Validation Failed");
        body.put("fields", fieldErrors);

        return ResponseEntity.badRequest().body(body);
    }

    //standard error ResponseEntity
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message)
    {
        Map<String, Object> body = new HashMap<>();
        
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
