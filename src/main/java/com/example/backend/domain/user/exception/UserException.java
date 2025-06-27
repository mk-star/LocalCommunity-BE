package com.example.backend.domain.user.exception;

import com.example.backend.global.apiPayload.exception.GeneralException;

public class UserException extends GeneralException {
    public UserException(UserErrorCode code) {
        super(code);
    }
}