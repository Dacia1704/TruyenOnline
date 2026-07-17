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
    CHAPTER_NOT_FOUND(HttpStatus.NOT_FOUND, "Chapter không tồn tại"),
    STORY_PUBLISH_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "Yêu cầu xuất bản truyện không tồn tại"),
    GENRE_NOT_FOUND(HttpStatus.NOT_FOUND, "Thể loại không tồn tại"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Bình luận không tồn tại"),
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "Dấu trang không tồn tại"),
    SESSION_INVALID(HttpStatus.UNAUTHORIZED, "Session không hợp lệ hoặc không được để trống"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Request không hợp lệ"),
    CHAPTER_NOT_SAME_STORY(HttpStatus.BAD_REQUEST, "Các chapter gửi lên không thuộc cùng 1 truyện"),
    STORY_PUBLISH_REQUEST_APPROVED(HttpStatus.BAD_REQUEST, "Yêu cầu xuất bản truyện đã được duyệt"),
    STORY_PUBLISH_REQUEST_PENDING(HttpStatus.BAD_REQUEST, "Yêu cầu xuất bản truyện đã được tạo và đang chờ duyệt"),
    STORY_PUBLISH_REQUEST_CONFIRMED(HttpStatus.BAD_REQUEST, "Yêu cầu xuất bản truyện đã được phê duyệt"),
    STORY_ALREADY_BANNED(HttpStatus.BAD_REQUEST, "Truyện này hiện đang bị hạn chế xuất bản."),
    CHAPTER_ALREADY_BANNED(HttpStatus.BAD_REQUEST, "Chương truyện này hiện đang bị hạn chế xuất bản."),
    USER_ALREADY_BANNED(HttpStatus.BAD_REQUEST, "Tài khoản này hiện đang bị tạm khóa."),
    COMMENT_ALREADY_BANNED(HttpStatus.BAD_REQUEST, "Bình luận này hiện đang bị ẩn do vi phạm."),
    STORY_NOT_GET_BANNED(HttpStatus.BAD_REQUEST, "Truyện này hiện đang không bị hạn chế xuất bản."),
    CHAPTER_NOT_GET_BANNED(HttpStatus.BAD_REQUEST, "Chương truyện này hiện đang không bị hạn chế xuất bản."),
    USER_NOT_GET_BANNED(HttpStatus.BAD_REQUEST, "Tài khoản này hiện đang không bị tạm khóa."),
    COMMENT_NOT_GET_BANNED(HttpStatus.BAD_REQUEST, "Bình luận này hiện đang không bị ẩn do vi phạm."),

    NO_PERMISSION(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện hành động này"),

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
