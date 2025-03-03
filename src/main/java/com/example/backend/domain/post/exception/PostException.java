package com.example.backend.domain.post.exception;

import com.example.backend.global.apiPayload.exception.GeneralException;

public class PostException extends GeneralException {
    public PostException(PostErrorCode code) {
        super(code);
    }
}