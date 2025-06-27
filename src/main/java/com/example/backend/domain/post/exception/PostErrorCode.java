package com.example.backend.domain.post.exception;

import com.example.backend.global.apiPayload.ApiResponse;
import com.example.backend.global.apiPayload.code.status.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostErrorCode implements BaseErrorCode {

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST401", "존재하지 않는 게시글입니다."),
    POST_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "POST402", "존재하지 않는 게시글 좋아요입니다."),
    POST_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "POST403", "존재하지 않는 게시글 이미지입니다."),
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ApiResponse<Void> getErrorResponse() {
        return ApiResponse.onFailure(code, message);
    }
}