package com.dacia1704.truyenonline.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Auth & User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Người dùng không tồn tại"),
    USER_EXISTED(HttpStatus.BAD_REQUEST, "Tên đăng nhập hoặc email đã tồn tại"),
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập để thực hiện thao tác này"),
    NOT_ACTIVE(HttpStatus.FORBIDDEN, "Tài khoản của bạn đã bị khóa hoặc chưa được kích hoạt."),
    UNAUTHORIZED(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập"),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Role không tồn tại"),
    REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "Yêu cầu không tồn tại"),

    // Story & Chapter
    STORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy bộ truyện này"),
    PREMIUM_REQUIRED(HttpStatus.FORBIDDEN, "Chương này yêu cầu tài khoản Premium"),

    // VNPay
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "Giao dịch thanh toán thất bại");

    private final HttpStatus code; // Mã HTTP Status
    private final String message; // Lời nhắn hiển thị cho user

    ErrorCode(HttpStatus code, String message) {
        this.code = code;
        this.message = message;
    }

    // (Tuỳ chọn) Hàm hỗ trợ lấy ra mã số nguyên (VD: 400, 404)
    // Dùng cho trường hợp Frontend cần 1 trường "statusCode" là số trong cục JSON trả về
    public int getStatusCodeValue() {
        return this.code.value();
    }
}
