package com.dacia1704.truyenonline.shared.exception;

import com.dacia1704.truyenonline.shared.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Bắt các lỗi nghiệp vụ chủ động ném ra (AppException)
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        ApiResponse<Object> response =
                ApiResponse.builder()
                        .code(errorCode.getCode().value())
                        .message(errorCode.getMessage())
                        .build();

        // Trả về HTTP status tương ứng (có thể map từ errorCode.getCode() ra HttpStatus)
        return ResponseEntity.status(errorCode.getCode().value()).body(response);
    }

    // 1. Bắt lỗi Validation (Khi dùng @Valid cho DTO nhưng client gửi sai format)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().getFirst().getDefaultMessage();

        ApiResponse<Object> response =
                ApiResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message(errorMessage)
                        .build();
        return ResponseEntity.badRequest().body(response);
    }

    // 2. Bắt các lỗi Runtime (Lỗi logic nghiệp vụ bạn tự ném ra)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        ApiResponse<Object> response =
                ApiResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message(ex.getMessage())
                        .build();
        return ResponseEntity.badRequest().body(response);
    }

    // 3. Bắt toàn bộ các lỗi hệ thống không lường trước được (tránh sập app)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        ApiResponse<Object> response =
                ApiResponse.builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(
                                "Internal Server Error: "
                                        + ex.getMessage()) // Ở môi trường Prod nên ẩn message
                        // gốc đi
                        .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
