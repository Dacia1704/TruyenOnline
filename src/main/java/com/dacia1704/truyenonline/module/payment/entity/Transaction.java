package com.dacia1704.truyenonline.module.payment.entity;

import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
        name = "transactions",
        indexes = {
            @Index(name = "idx_txn_user_id", columnList = "user_id"),
            @Index(name = "idx_txn_vnp_txn_ref", columnList = "vnp_txn_ref"),
            @Index(name = "idx_txn_status", columnList = "status"),
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "CHAR(36)")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "vnp_txn_ref", nullable = false, unique = true, length = 100)
    String vnpTxnRef;

    @Column(name = "vnp_amount", nullable = false)
    Long vnpAmount;

    @Column(name = "vnp_bank_code", length = 20)
    String vnpBankCode;

    @Column(name = "vnp_transaction_no", length = 100)
    String vnpTransactionNo;

    @Column(name = "vnp_response_code", length = 10)
    String vnpResponseCode;

    @Column(name = "vnp_secure_hash", length = 256)
    String vnpSecureHash;

    @Column(name = "vnp_pay_date", length = 20)
    String vnpPayDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    TransactionStatus status;

    @Column(name = "ip_address", length = 45)
    String ipAddress;

    @Column(name = "raw_callback_data", columnDefinition = "JSON")
    String rawCallbackData;

    @Column(name = "completed_at")
    LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "subscription_plan",
            referencedColumnName = "code",
            foreignKey = @ForeignKey(name = "fk_txn_plan")
    )
    private SubscriptionPlan subscriptionPlan;
}
