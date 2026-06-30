package com.dacia1704.truyenonline.module.billing.repository;

import com.dacia1704.truyenonline.module.billing.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {}
