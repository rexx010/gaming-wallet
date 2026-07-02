package com.gamingwallet.wallet.controller;

import com.gamingwallet.common.dto.ApiResponse;
import com.gamingwallet.wallet.dto.request.FundWalletRequest;
import com.gamingwallet.wallet.dto.request.WithdrawRequest;
import com.gamingwallet.wallet.dto.response.FundWalletResponse;
import com.gamingwallet.wallet.dto.response.VerifyWithdrawalResponse;
import com.gamingwallet.wallet.dto.response.WithdrawResponse;
import com.gamingwallet.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/fund")
    public ResponseEntity<FundWalletResponse> fundWallet(
            @Valid @RequestBody FundWalletRequest request
    ) {
        FundWalletResponse response = walletService.fundWallet(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/withdraw")
    public ApiResponse<WithdrawResponse> withdraw(
            @Valid @RequestBody WithdrawRequest request
    ) {
        return ApiResponse.success(
                "Withdrawal initiated",
                walletService.withdraw(request)
        );
    }

    @GetMapping("/withdraw/{reference}")
    public ApiResponse<VerifyWithdrawalResponse> verifyWithdrawal(
            @PathVariable String reference
    ) {
        return ApiResponse.success(
                "Transfer fetched successfully",
                walletService.verifyWithdrawal(reference)
        );
    }
}
