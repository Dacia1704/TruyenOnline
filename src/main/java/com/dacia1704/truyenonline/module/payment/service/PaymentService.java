package com.dacia1704.truyenonline.module.payment.service;

import com.dacia1704.truyenonline.config.VNPayConfig;
import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import com.dacia1704.truyenonline.module.administration.service.AuditLogService;
import com.dacia1704.truyenonline.module.payment.dto.request.CreatePaymentRequest;
import com.dacia1704.truyenonline.module.payment.dto.response.CreatePaymentResponse;
import com.dacia1704.truyenonline.module.payment.dto.response.PaymentCallbackResult;
import com.dacia1704.truyenonline.module.payment.dto.response.TransactionResponse;
import com.dacia1704.truyenonline.module.payment.entity.SubscriptionPlan;
import com.dacia1704.truyenonline.module.payment.entity.Transaction;
import com.dacia1704.truyenonline.module.payment.entity.TransactionStatus;
import com.dacia1704.truyenonline.module.payment.mapper.TransactionMapper;
import com.dacia1704.truyenonline.module.payment.repository.SubscriptionPlanRepository;
import com.dacia1704.truyenonline.module.payment.repository.TransactionRepository;
import com.dacia1704.truyenonline.module.payment.utils.VNPayUtil;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.module.user.service.UserService;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {

    VNPayConfig vnPayConfig;
    TransactionRepository transactionRepository;
    SubscriptionService subscriptionService;
    ObjectMapper objectMapper;
    UserRepository userRepository;
    SubscriptionPlanRepository subscriptionPlanRepository;
    TransactionMapper transactionMapper;
    AuditLogService auditLogService;
    UserService userService;

    // ----------------------------------------------------------------
    // Bước 1: Tạo giao dịch PENDING + build URL redirect sang VNPay
    // ----------------------------------------------------------------
    @Transactional
    public CreatePaymentResponse createPayment(CreatePaymentRequest request, String clientIp) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User currentUser =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        SubscriptionPlan plan =
                subscriptionPlanRepository
                        .findById(request.getPlanId())
                        .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_PLAN_NOT_FOUND));
        Long amountVND = plan.getPrice();

        if (amountVND == null) {
            throw new AppException(ErrorCode.INVALID_PLAN);
        }

        // Tạo transaction PENDING trong DB
        String txnRef = VNPayUtil.generateTxnRef();
        Transaction transaction =
                Transaction.builder()
                        .user(currentUser)
                        .subscriptionPlan(plan)
                        .vnpTxnRef(txnRef)
                        .vnpAmount(VNPayUtil.toVNPayAmount(amountVND))
                        .status(TransactionStatus.PENDING)
                        .ipAddress(clientIp)
                        .build();
        transactionRepository.save(transaction);

        log.info(
                "Tạo transaction PENDING: txnRef={}, user={}, plan={}, amount={}",
                txnRef,
                currentUser.getId(),
                plan,
                amountVND);

        // Build params gửi sang VNPay
        String payUrl = buildVNPayUrl(txnRef, amountVND, request.getOrderInfo(), clientIp);

        return CreatePaymentResponse.builder()
                .txnRef(txnRef)
                .paymentUrl(payUrl)
                .amount(amountVND)
                .plan(plan)
                .build();
    }

    // ----------------------------------------------------------------
    // Bước 2: Xử lý Return URL (browser redirect về sau khi thanh toán)
    // Chỉ dùng để hiển thị UI — KHÔNG dùng để cập nhật DB chính
    // ----------------------------------------------------------------
    public PaymentCallbackResult handleReturnUrl(Map<String, String> params) {
        String receivedHash = params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        boolean validHash =
                VNPayUtil.verifySecureHash(vnPayConfig.getHashSecret(), params, receivedHash);

        String responseCode = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");

        return PaymentCallbackResult.builder()
                .txnRef(txnRef)
                .success("00".equals(responseCode) && validHash)
                .responseCode(responseCode)
                .validSignature(validHash)
                .build();
    }

    // ----------------------------------------------------------------
    // Bước 3: Xử lý IPN — VNPay server gọi trực tiếp về đây
    // Đây là nơi cập nhật DB chính xác nhất
    // ----------------------------------------------------------------
    @Transactional
    public Map<String, String> handleIPN(Map<String, String> params) {
        Map<String, String> response = new HashMap<>();
        log.info("VNPay IPN params: {}", params);
        try {
            // 1. Tách hash ra khỏi params trước khi verify
            String receivedHash = params.remove("vnp_SecureHash");
            params.remove("vnp_SecureHashType");

            // 2. Verify chữ ký — bảo mật quan trọng nhất
            boolean validHash =
                    VNPayUtil.verifySecureHash(vnPayConfig.getHashSecret(), params, receivedHash);

            if (!validHash) {
                log.warn("VNPay IPN: chữ ký không hợp lệ, params={}", params);
                response.put("RspCode", "97");
                response.put("Message", "Invalid signature");
                return response;
            }

            String txnRef = params.get("vnp_TxnRef");
            String rspCode = params.get("vnp_ResponseCode");
            String vnpAmount = params.get("vnp_Amount");
            String txnNo = params.get("vnp_TransactionNo");
            String bankCode = params.get("vnp_BankCode");
            String payDate = params.get("vnp_PayDate");

            // 3. Tìm transaction
            Transaction transaction = transactionRepository.findByVnpTxnRef(txnRef).orElse(null);

            if (transaction == null) {
                log.warn("VNPay IPN: không tìm thấy txnRef={}", txnRef);
                response.put("RspCode", "01");
                response.put("Message", "Order not found");
                return response;
            }

            // 4. Idempotency check — tránh xử lý 2 lần
            if (transaction.getStatus() != TransactionStatus.PENDING) {
                log.info(
                        "VNPay IPN: txnRef={} đã xử lý rồi (status={})",
                        txnRef,
                        transaction.getStatus());
                response.put("RspCode", "02");
                response.put("Message", "Order already confirmed");
                return response;
            }

            // 5. Kiểm tra số tiền khớp
            long receivedAmount = Long.parseLong(vnpAmount);
            if (receivedAmount != transaction.getVnpAmount()) {
                log.error(
                        "VNPay IPN: số tiền không khớp! expected={}, received={}",
                        transaction.getVnpAmount(),
                        receivedAmount);
                response.put("RspCode", "04");
                response.put("Message", "Invalid amount");
                return response;
            }

            // 6. Lưu raw callback data để debug
            String rawData = objectMapper.writeValueAsString(params);

            // 7. Cập nhật transaction
            transaction.setVnpTransactionNo(txnNo);
            transaction.setVnpBankCode(bankCode);
            transaction.setVnpResponseCode(rspCode);
            transaction.setVnpPayDate(payDate);
            transaction.setVnpSecureHash(receivedHash);
            transaction.setRawCallbackData(rawData);

            if ("00".equals(rspCode)) {
                // Thanh toán thành công
                transaction.setStatus(TransactionStatus.SUCCESS);
                transaction.setCompletedAt(LocalDateTime.now());
                transactionRepository.save(transaction);

                // Kích hoạt gói Premium
                subscriptionService.activateSubscription(
                        transaction.getUser(), transaction.getSubscriptionPlan());

                auditLogService.log(
                        AuditAction.SUCCESS,
                        AuditObjectType.TRANSACTION,
                        transaction.getId(),
                        null,
                        response,
                        null);

                log.info(
                        "VNPay IPN: thanh toán thành công txnRef={}, user={}",
                        txnRef,
                        transaction.getUser().getId());

            } else {
                // Thanh toán thất bại
                transaction.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(transaction);

                auditLogService.log(
                        AuditAction.FAIL,
                        AuditObjectType.TRANSACTION,
                        transaction.getId(),
                        null,
                        response,
                        null);

                log.warn("VNPay IPN: thanh toán thất bại txnRef={}, rspCode={}", txnRef, rspCode);
            }

            response.put("RspCode", "00");
            response.put("Message", "Confirm Success");

        } catch (Exception e) {
            log.error("VNPay IPN: lỗi xử lý", e);
            response.put("RspCode", "99");
            response.put("Message", "Unknown error");
        }

        return response;
    }

    // ----------------------------------------------------------------
    // Private: Build URL thanh toán VNPay
    // ----------------------------------------------------------------
    private String buildVNPayUrl(String txnRef, long amountVND, String orderInfo, String clientIp) {

        String createDate = VNPayUtil.formatDate(LocalDateTime.now());
        String expireDate = VNPayUtil.formatDate(LocalDateTime.now().plusMinutes(15));

        // Dùng TreeMap để đảm bảo sort theo key (VNPay yêu cầu)
        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", vnPayConfig.getVersion());
        params.put("vnp_Command", vnPayConfig.getCommand());
        params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        params.put("vnp_Amount", String.valueOf(VNPayUtil.toVNPayAmount(amountVND)));
        params.put("vnp_CurrCode", vnPayConfig.getCurrencyCode());
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", orderInfo != null ? orderInfo : "Thanh toan goi " + txnRef);
        params.put("vnp_OrderType", vnPayConfig.getOrderType());
        params.put("vnp_Locale", vnPayConfig.getLocale());
        params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        params.put("vnp_IpAddr", clientIp);
        params.put("vnp_CreateDate", createDate);
        params.put("vnp_ExpireDate", expireDate);

        // Tính secure hash
        String hashData = VNPayUtil.buildHashData(params);
        String secureHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData);

        // Build query string + append hash
        String queryString = VNPayUtil.buildQueryString(params);
        return vnPayConfig.getPayUrl() + "?" + queryString + "&vnp_SecureHash=" + secureHash;
    }

    public List<TransactionResponse> getTransactionsByUser() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(transactionMapper::toTransactionResponse)
                .toList();
    }

    public PageResponse<TransactionResponse> getTransactions(int page, int size, String userId) {
        int pageNo = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by("createdAt").descending());
        Page<Transaction> transactionPage;
        if (userId != null) {
            transactionPage = transactionRepository.findAllByUserId(userId, pageable);
        } else {
            transactionPage = transactionRepository.findAll(pageable);
        }
        List<TransactionResponse> transactionResponses =
                transactionPage.getContent().stream()
                        .map(transactionMapper::toTransactionResponse)
                        .toList();
        return PageResponse.<TransactionResponse>builder()
                .currentPage(page)
                .pageSize(transactionPage.getSize())
                .totalPages(transactionPage.getTotalPages())
                .totalElements(transactionPage.getTotalElements())
                .data(transactionResponses)
                .build();
    }
}
