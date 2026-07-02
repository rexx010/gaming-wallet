package com.gamingwallet.user.dto.response;

import java.math.BigDecimal;

public record UserResponse (
        Long id,
        String fullName,
        BigDecimal walletBalance
){
}
