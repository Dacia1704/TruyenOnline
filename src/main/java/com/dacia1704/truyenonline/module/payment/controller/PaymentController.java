package com.dacia1704.truyenonline.module.payment.controller;

import com.dacia1704.truyenonline.module.payment.dto.request.CreatePaymentRequest;
import com.dacia1704.truyenonline.module.payment.dto.response.CreatePaymentResponse;
import com.dacia1704.truyenonline.module.payment.dto.response.PaymentCallbackResult;
import com.dacia1704.truyenonline.module.payment.dto.response.TransactionResponse;
import com.dacia1704.truyenonline.module.payment.service.PaymentService;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.shared.response.ApiResponse;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    PaymentService paymentService;
    // ----------------------------------------------------------------
    // POST /api/payment/create
    // User gọi để lấy URL redirect sang VNPay
    // Yêu cầu JWT auth
    // ----------------------------------------------------------------
    @PostMapping("/create")
    public ApiResponse<CreatePaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request, HttpServletRequest httpRequest) {
        String clientIp = getClientIp(httpRequest);
        CreatePaymentResponse response = paymentService.createPayment(request, clientIp);
        return ApiResponse.success(response);
    }

    // ----------------------------------------------------------------
    // GET /api/payment/vnpay-return
    // VNPay redirect browser về đây sau thanh toán
    // KHÔNG yêu cầu JWT (user có thể không còn session)
    // Chỉ dùng để lấy kết quả hiển thị UI — KHÔNG update DB ở đây
    // ----------------------------------------------------------------
    @GetMapping("/vnpay-return")
    public ApiResponse<PaymentCallbackResult> handleReturn(@RequestParam Map<String, String> params) {
        log.info("VNPay Return URL params: {}", params);
        Map<String, String> mutableParams = new HashMap<>(params);
        PaymentCallbackResult result = paymentService.handleReturnUrl(mutableParams);
        return ApiResponse.success(result);
    }

    // ----------------------------------------------------------------
    // GET /api/payment/vnpay-ipn
    // VNPay SERVER gọi trực tiếp về đây (không phải browser)
    // KHÔNG yêu cầu JWT — phải permit trong SecurityConfig
    // Đây là nơi cập nhật DB chính thức
    // ----------------------------------------------------------------
    @GetMapping("/vnpay-ipn")
    public ApiResponse<Map<String, String>> handleIPN(@RequestParam Map<String, String> params) {
        log.info("VNPay IPN received: txnRef={}, responseCode={}", params.get("vnp_TxnRef"), params.get("vnp_ResponseCode"));
        Map<String, String> mutableParams = new HashMap<>(params);
        Map<String, String> response = paymentService.handleIPN(mutableParams);
        return ApiResponse.success(response);
    }

    // ----------------------------------------------------------------
    // GET /api/payment/transactions
    // Lịch sử giao dịch của user hiện tại
    // ----------------------------------------------------------------
    @GetMapping("/transactions/me")
    public ApiResponse<List<TransactionResponse>> getTransactionsMe() {
        List<TransactionResponse> transactions = paymentService.getTransactionsByUser();
        return ApiResponse.success(transactions);
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<TransactionResponse>> getTransactions(@RequestParam(defaultValue = "1") int page,
                                                                          @RequestParam(defaultValue = "10") int size,
                                                                          @RequestParam(required = false) String userId
                                                                          ) {
        PageResponse<TransactionResponse> transactions = paymentService.getTransactions(page, size, userId);
        return ApiResponse.success(transactions);
    }

    // ----------------------------------------------------------------
    // Lấy IP thực của client (qua reverse proxy / load balancer)
    // ----------------------------------------------------------------
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For có thể chứa nhiều IP: "client, proxy1, proxy2"
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}