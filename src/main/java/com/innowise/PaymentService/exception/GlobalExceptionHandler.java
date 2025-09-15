package com.innowise.PaymentService.exception;

import com.innowise.common.exception.DuplicateResourceCustomException;
import com.innowise.common.exception.ExternalApiResponseCustomException;
import com.innowise.common.exception.InvalidDateRangeCustomException;
import com.innowise.common.exception.ResourceNotFoundCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundCustomException.class)
    public ResponseEntity<String> resourceNotFoundCustomException(ResourceNotFoundCustomException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceCustomException.class)
    public ResponseEntity<String> duplicateResourceCustomException(DuplicateResourceCustomException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ExternalApiResponseCustomException.class)
    public ResponseEntity<String> externalApiResponseCustomException(ExternalApiResponseCustomException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FAILED_DEPENDENCY);
    }

    @ExceptionHandler(InvalidDateRangeCustomException.class)
    public ResponseEntity<String> invalidDateRangeCustomException(InvalidDateRangeCustomException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
