package com.gamingwallet.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank
        String fullName
) {
}
