package com.gamingwallet.wallet.service.impl;

import com.gamingwallet.common.exceptions.InsufficientBalanceException;
import com.gamingwallet.common.exceptions.UserNotFoundException;
import com.gamingwallet.common.util.ReferenceGenerator;
import com.gamingwallet.spotflow.dto.DynamicAccountResponse;
import com.gamingwallet.spotflow.dto.payout.TransferResponse;
import com.gamingwallet.spotflow.service.SpotflowService;
import com.gamingwallet.transaction.entity.Transaction;
import com.gamingwallet.transaction.enums.TransactionStatus;
import com.gamingwallet.transaction.enums.TransactionType;
import com.gamingwallet.transaction.repository.TransactionRepository;
import com.gamingwallet.user.entity.User;
import com.gamingwallet.user.repository.UserRepository;
import com.gamingwallet.wallet.dto.request.FundWalletRequest;
import com.gamingwallet.wallet.dto.request.WithdrawRequest;
import com.gamingwallet.wallet.dto.response.FundWalletResponse;
import com.gamingwallet.wallet.dto.response.VerifyWithdrawalResponse;
import com.gamingwallet.wallet.dto.response.WithdrawResponse;
import com.gamingwallet.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final SpotflowService spotflowService;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    // Separate bean to hold @Transactional atomic writes — fixes the
    // self-invocation AOP proxy problem (see WalletTransactionHelper javadoc)
    private final WalletTransactionHelper txHelper;

    @Override
    public FundWalletResponse fundWallet(FundWalletRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));

        String reference = ReferenceGenerator.generate();

        // Commit 1: save PENDING record — short, no HTTP involved
        Transaction transaction = txHelper.createPendingPayIn(user, request.amount(), reference);

        // HTTP call to Spotflow — outside any DB transaction
        DynamicAccountResponse account = spotflowService.createTemporaryAccount(
                user.getFullName(), request.amount()
        );

        // Commit 2: attach virtual account details
        txHelper.attachVirtualAccount(transaction, account);

        return new FundWalletResponse(
                transaction.getReference(),
                transaction.getTransactionStatus().name(),
                "Transfer the exact amount into the account below.",
                account.accountNumber(),
                account.bankName(),
                account.accountName()
        );
    }

    // ---------------------------------------------------------------
    // PAYOUT
    // Atomic conditional debit via SQL WHERE clause (Bug #6 fix)
    // ---------------------------------------------------------------
    @Override
    @Transactional
    public WithdrawResponse withdraw(WithdrawRequest request) {
        int rowsUpdated = userRepository.debitBalance(request.userId(), request.amount());
        if (rowsUpdated == 0) {
            throw new InsufficientBalanceException();
        }

        String reference = ReferenceGenerator.generate();
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));

        Transaction transaction = Transaction.builder()
                .reference(reference)
                .amount(request.amount())
                .transactionType(TransactionType.PAYOUT)
                .transactionStatus(TransactionStatus.PENDING)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);

        try {
            TransferResponse response = spotflowService.transfer(request, reference);
            transaction.setSpotflowReference(response.getSpotflowReference());
            transactionRepository.save(transaction);
            return new WithdrawResponse(response.getReference(), response.getStatus(), "Withdrawal initiated successfully");
        } catch (Exception ex) {
            // Spotflow call failed — reverse the local debit (compensating action)
            log.error("Spotflow transfer failed for {}: {}", reference, ex.getMessage());
            userRepository.creditBalance(request.userId(), request.amount());
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw ex;
        }
    }

    // ---------------------------------------------------------------
    // VERIFY (manual poll of a specific payout's real status)
    // ---------------------------------------------------------------
    @Override
    @Transactional
    public VerifyWithdrawalResponse verifyWithdrawal(String reference) {
        Transaction transaction = transactionRepository
                .findByReference(reference)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + reference));

        TransferResponse response = spotflowService.getTransfer(reference);
        TransactionStatus latestStatus = parseStatus(response.getStatus());

        if (transaction.getTransactionStatus() != latestStatus) {
            transaction.setTransactionStatus(latestStatus);
            transaction.setSpotflowReference(response.getSpotflowReference());
            transactionRepository.save(transaction);
        }

        return new VerifyWithdrawalResponse(
                response.getReference(),
                response.getStatus(),
                BigDecimal.valueOf(response.getAmount()),
                response.getDestination().getAccountNumber(),
                response.getDestination().getAccountName(),
                response.getDestination().getBankName()
        );
    }

    // ---------------------------------------------------------------
    // RECONCILIATION — Bug #5 fix: only PAYOUT goes to Spotflow poll;
    // stuck PAYIN gets marked ABANDONED (virtual account expired)
    // ---------------------------------------------------------------
    @Override
    public void reconcilePendingTransactions() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Transaction> pending = transactionRepository
                .findByTransactionStatusAndCreatedAtBefore(TransactionStatus.PENDING, oneHourAgo);

        for (Transaction transaction : pending) {
            try {
                if (transaction.getTransactionType() == TransactionType.PAYOUT) {
                    verifyWithdrawal(transaction.getReference());
                } else {
                    // PAYIN stuck > 1hr: virtual account has expired, no money arrived
                    transaction.setTransactionStatus(TransactionStatus.ABANDONED);
                    transactionRepository.save(transaction);
                    log.info("[reconciliation] PAYIN {} → ABANDONED (virtual account expired)",
                            transaction.getReference());
                }
            } catch (Exception ex) {
                log.error("[reconciliation] failed for {}: {}", transaction.getReference(), ex.getMessage());
            }
        }
    }

    private TransactionStatus parseStatus(String status) {
        if (status == null) return TransactionStatus.PENDING;
        return switch (status.toUpperCase()) {
            case "SUCCESS", "SUCCESSFUL" -> TransactionStatus.SUCCESS;
            case "FAILED" -> TransactionStatus.FAILED;
            default -> TransactionStatus.PENDING;
        };
    }
}
