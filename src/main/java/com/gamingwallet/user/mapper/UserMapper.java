package com.gamingwallet.user.mapper;

import com.gamingwallet.user.dto.response.UserResponse;
import com.gamingwallet.user.entity.User;

public class UserMapper {
    private UserMapper(){}

    public static UserResponse toResponse(User user) {

        return new UserResponse(

                user.getId(),
                user.getFullName(),
                user.getWalletBalance()

        );
    }
}
