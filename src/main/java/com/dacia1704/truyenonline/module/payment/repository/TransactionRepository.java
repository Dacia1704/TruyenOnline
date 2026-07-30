package com.dacia1704.truyenonline.module.payment.repository;

import com.dacia1704.truyenonline.module.payment.entity.Transaction;
import com.dacia1704.truyenonline.module.payment.entity.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    Optional<Transaction> findByVnpTxnRef(String vnpTxnRef);

    List<Transaction> findByUserIdOrderByCreatedAtDesc(String userId);

    Page<Transaction> findAllByUserId(String userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.status = :status")
    List<Transaction> findByUserIdAndStatus(String userId, TransactionStatus status);
}