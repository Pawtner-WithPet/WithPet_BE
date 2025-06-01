package com.pawtner.WithPet.global.exception;

import com.pawtner.WithPet.global.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 서버 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e, HttpServletRequest request) {
        ApiErrorResponse response = ApiErrorResponse.of(
                500,
                request.getRequestURI(),
                "SERVER_ERROR",
                "서버에 오류가 발생했습니다. 다시 시도해주세요."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 반려견 정보 조회 실패
    @ExceptionHandler(PetInfoNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePetInfoNotFound(PetInfoNotFoundException e, HttpServletRequest request) {
        ApiErrorResponse response = ApiErrorResponse.of(
                404,
                request.getRequestURI(),
                "DOG_NOT_FOUND",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}