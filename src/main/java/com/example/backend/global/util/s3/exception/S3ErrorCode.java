package com.example.backend.global.util.s3.exception;

import com.example.backend.global.apiPayload.ApiResponse;
import com.example.backend.global.apiPayload.code.status.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements BaseErrorCode {

    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3501", "S3 파일 업로드에 실패했습니다."),
    IMAGE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3502", "S3 파일 삭제에 실패했습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ApiResponse<Void> getErrorResponse() {
        return ApiResponse.onFailure(code, message);
    }
}