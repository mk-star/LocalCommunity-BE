package com.example.backend.global.util.s3.exception;

import com.example.backend.global.apiPayload.exception.GeneralException;
import lombok.Getter;

@Getter
public class S3Exception extends GeneralException {
    public S3Exception(S3ErrorCode code) {
        super(code);
    }
}