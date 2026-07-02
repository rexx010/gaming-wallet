package com.gamingwallet.transaction.enums;

public enum TransactionStatus {
    PENDING,
    SUCCESS,
    FAILED,
    /**
     * Used by the reconciliation worker for PAYIN transactions that have been
     * PENDING for over an hour — meaning the Spotflow virtual account has expired
     * and money never arrived. No funds moved, so no refund is needed.
     */
    ABANDONED
}
