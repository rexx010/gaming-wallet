package com.gamingwallet.user.controller;

import com.gamingwallet.common.dto.ApiResponse;
import com.gamingwallet.user.dto.request.CreateUserRequest;
import com.gamingwallet.user.dto.request.getUserRequest;
import com.gamingwallet.user.dto.response.UserResponse;
import com.gamingwallet.user.entity.User;
import com.gamingwallet.user.mapper.UserMapper;
import com.gamingwallet.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> create(
            @Valid
            @RequestBody
            CreateUserRequest request){
        User user = userService.create(request.fullName());
        return new ApiResponse<>(
                true,
                "User created successfully",
                UserMapper.toResponse(user)

        );
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(
            @PathVariable Long id
    ) {
        User user = userService.findById(id);
    
        return new ApiResponse<>(
                true,
                "User retrieved successfully",
                UserMapper.toResponse(user)
        );
    }
}
