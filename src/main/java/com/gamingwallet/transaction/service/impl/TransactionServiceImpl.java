package com.gamingwallet.transaction.service.impl;

import com.gamingwallet.transaction.entity.Transaction;
import com.gamingwallet.transaction.enums.TransactionStatus;
import com.gamingwallet.transaction.enums.TransactionType;
import com.gamingwallet.transaction.repository.TransactionRepository;
import com.gamingwallet.transaction.service.TransactionService;
import com.gamingwallet.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository repository;

    @Override
    public Transaction createPayIn(User user, BigDecimal amount, String reference) {
        Transaction transaction = Transaction.builder()
                .reference(reference)
                .user(user)
                .amount(amount)
                .transactionType(TransactionType.PAYIN)
                .transactionStatus(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return repository.save(transaction);
    }

    @Override
    public Transaction createPayOut(User user, BigDecimal amount, String reference) {
        Transaction transaction = Transaction.builder()
                .reference(reference)
                .user(user)
                .amount(amount)
                .transactionType(TransactionType.PAYOUT)
                .transactionStatus(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return repository.save(transaction);
    }

    @Override
    public Transaction save(Transaction transaction) {
        return repository.save(transaction);
    }

    @Override
    public Optional<Transaction> findByVirtualAccountNumber(String reference) {
        return repository.findByVirtualAccountNumber(reference);
    }
}
