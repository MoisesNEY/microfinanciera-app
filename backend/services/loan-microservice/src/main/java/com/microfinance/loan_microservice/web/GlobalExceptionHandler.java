package com.microfinance.loan_microservice.web;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<Map<String,Object>> notFound(NoSuchElementException ex){
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of("error","NOT_FOUND","message", ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException ex){
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors()
        .forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
    return ResponseEntity.badRequest().body(Map.of("error","VALIDATION_ERROR","fields", errors));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<Map<String,Object>> conflict(IllegalStateException ex){
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(Map.of("error","CONFLICT","message", ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String,Object>> generic(Exception ex){
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error","INTERNAL_ERROR","message", ex.getMessage()));
  }
}
