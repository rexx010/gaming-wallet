package com.gamingwallet.transaction.service;

import com.gamingwallet.transaction.entity.Transaction;
import com.gamingwallet.user.entity.User;

import java.math.BigDecimal;
import java.util.Optional;

public interface TransactionService {
    Transaction createPayIn(User user, BigDecimal amount, String reference);
    Transaction createPayOut(User user, BigDecimal amount, String reference);
    Transaction save(Transaction transaction);
    Optional<Transaction> findByVirtualAccountNumber(String reference);
}
