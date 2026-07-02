package com.gamingwallet.wallet.service;

import com.gamingwallet.wallet.dto.request.FundWalletRequest;
import com.gamingwallet.wallet.dto.request.WithdrawRequest;
import com.gamingwallet.wallet.dto.response.FundWalletResponse;
import com.gamingwallet.wallet.dto.response.VerifyWithdrawalResponse;
import com.gamingwallet.wallet.dto.response.WithdrawResponse;

public interface WalletService {
    FundWalletResponse fundWallet(FundWalletRequest request);
    WithdrawResponse withdraw(WithdrawRequest request);
    VerifyWithdrawalResponse verifyWithdrawal(String reference);
    void reconcilePendingTransactions();
}
