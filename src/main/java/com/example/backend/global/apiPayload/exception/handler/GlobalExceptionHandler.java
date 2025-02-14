package com.example.backend.global.apiPayload.exception.handler;

import com.example.backend.global.apiPayload.ApiResponse;
import com.example.backend.global.apiPayload.code.GeneralErrorCode;
import com.example.backend.global.apiPayload.code.status.BaseErrorCode;
import com.example.backend.global.apiPayload.exception.GeneralException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ConstraintViolationException 추출 도중 에러 발생"));

        return ResponseEntity
                .status(GeneralErrorCode.VALIDATION_FAILED.getHttpStatus())
                .body(ApiResponse.onFailure(GeneralErrorCode.VALIDATION_FAILED.getCode(), errorMessage));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        fieldErrors.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ApiResponse<Map<String, String>> errorResponse = ApiResponse.onFailure(
                GeneralErrorCode.VALIDATION_FAILED.getCode(),
                GeneralErrorCode.VALIDATION_FAILED.getMessage(),
                errors
        );
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(GeneralException e) {
        BaseErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorCode.getErrorResponse());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleAllException(Exception e) {
        ApiResponse<String> errorResponse = ApiResponse.onFailure(
                GeneralErrorCode._INTERNAL_SERVER_ERROR.getCode(),
                GeneralErrorCode._INTERNAL_SERVER_ERROR.getMessage(),
                e.getMessage()
        );
        return ResponseEntity
                .status(GeneralErrorCode._INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(errorResponse);
    }
}