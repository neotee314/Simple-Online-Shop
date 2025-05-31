package com.neotee.ecommercesystem.exception;

// GlobalExceptionHandler.java

import com.neotee.ecommercesystem.ShopException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ThingQuantityNotAvailableException.class)
    public ResponseEntity<String> handleThingQuantity(ThingQuantityNotAvailableException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(ThingNotInShoppingBasketException.class)
    public ResponseEntity<String> handleThingNotInShoppingBasket(ThingNotInShoppingBasketException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(EntityIdNullException.class)
    public ResponseEntity<String> handleEntityIdNull(EntityIdNullException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ex.getMessage());
    }

    @ExceptionHandler(QuantityNegativeException.class)
    public ResponseEntity<String> handleQuantityNegative(QuantityNegativeException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
    }

    @ExceptionHandler(ValueObjectNullOrEmptyException.class)
    public ResponseEntity<String> handleValueObjectNullOrEmpty(ValueObjectNullOrEmptyException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ex.getMessage());
    }

    @ExceptionHandler(ShoppingBasketEmptyException.class)
    public ResponseEntity<String> handleBasketEmpty(ShoppingBasketEmptyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntitytNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}
