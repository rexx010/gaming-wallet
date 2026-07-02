package com.gamingwallet.common.exceptions;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Insufficient wallet balance");
    }
}
