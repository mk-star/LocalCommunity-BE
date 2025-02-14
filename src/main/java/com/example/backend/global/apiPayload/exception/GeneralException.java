package com.example.backend.global.apiPayload.exception;

import com.example.backend.global.apiPayload.code.status.BaseErrorCode;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {
    private final BaseErrorCode errorCode;

    protected GeneralException(BaseErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}