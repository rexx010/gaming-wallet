package com.gamingwallet.transaction.repository;

import com.gamingwallet.transaction.entity.Transaction;
import com.gamingwallet.transaction.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByVirtualAccountNumber(String virtualAccountNumber);
    Optional<Transaction> findByReference(String reference);
    List<Transaction> findByTransactionStatusAndCreatedAtBefore(TransactionStatus status, LocalDateTime createdAt);
}
