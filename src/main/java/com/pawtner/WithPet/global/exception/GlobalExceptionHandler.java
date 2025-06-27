package com.pawtner.WithPet.global.exception;

import com.pawtner.WithPet.domain.awsS3.exception.InvalidImageException;
import com.pawtner.WithPet.domain.awsS3.exception.S3OperationException;
import com.pawtner.WithPet.domain.noseprint.exception.DuplicateNosePrintException;
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

    // 회원 정보 조회 실패
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleMemberNotFound(MemberNotFoundException e, HttpServletRequest request) {
        ApiErrorResponse response = ApiErrorResponse.of(
                404,
                request.getRequestURI(),
                "USER_NOT_FOUND",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 공공 API 호출 실패
    @ExceptionHandler(PetApiException.class)
    public ResponseEntity<ApiErrorResponse> handleExternalApi(PetApiException e, HttpServletRequest request) {
        ApiErrorResponse response = ApiErrorResponse.of(
                502,
                request.getRequestURI(),
                "API_CALL_FAILED",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
    }

    // 이미지 파일 유효성 검증 실패
    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidImageException(InvalidImageException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of(400, request.getRequestURI(), "INVALID_IMAGE", e.getMessage()));
    }

    // 해당 Pet 없음
    @ExceptionHandler(PetNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePetNotFound(PetNotFoundException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.of(404, request.getRequestURI(), "PET_NOT_FOUND", e.getMessage()));
    }

    // 사용자 권한 없음
    @ExceptionHandler(UnauthorizedPetAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(UnauthorizedPetAccessException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiErrorResponse.of(403, request.getRequestURI(), "UNAUTHORIZED", e.getMessage()));
    }

    // S3 업로드/삭제 실패
    @ExceptionHandler(S3OperationException.class)
    public ResponseEntity<ApiErrorResponse> handleS3Error(S3OperationException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of(500, request.getRequestURI(), "S3_ERROR", e.getMessage()));
    }

    // 비문 중복 등록
    @ExceptionHandler(DuplicateNosePrintException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateNosePrint(DuplicateNosePrintException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of(409, request.getRequestURI(), "DUPLICATE_NOSEPRINT", e.getMessage()));
    }

}