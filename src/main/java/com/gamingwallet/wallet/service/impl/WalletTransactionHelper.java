package com.gamingwallet.wallet.service.impl;

import com.gamingwallet.spotflow.dto.DynamicAccountResponse;
import com.gamingwallet.transaction.entity.Transaction;
import com.gamingwallet.transaction.enums.TransactionStatus;
import com.gamingwallet.transaction.enums.TransactionType;
import com.gamingwallet.transaction.repository.TransactionRepository;
import com.gamingwallet.transaction.service.TransactionService;
import com.gamingwallet.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WalletTransactionHelper {

    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction createPendingPayIn(User user, BigDecimal amount, String reference) {
        return transactionService.createPayIn(user, amount, reference);
    }

    @Transactional
    public void attachVirtualAccount(Transaction txn, DynamicAccountResponse account) {
        txn.setVirtualAccountNumber(account.accountNumber());
        txn.setVirtualAccountName(account.accountName());
        txn.setBankName(account.bankName());
        transactionService.save(txn);
    }
}
