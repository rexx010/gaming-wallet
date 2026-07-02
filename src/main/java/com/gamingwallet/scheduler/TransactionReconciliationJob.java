package com.gamingwallet.scheduler;

import com.gamingwallet.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionReconciliationJob {
    private final WalletService walletService;

    @Scheduled(fixedRate = 300000)
    public void reconcilePendingTransactions() {
        walletService.reconcilePendingTransactions();

    }
}
