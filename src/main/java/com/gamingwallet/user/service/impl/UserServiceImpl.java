package com.gamingwallet.user.service.impl;

import com.gamingwallet.common.exceptions.UserNotFoundException;
import com.gamingwallet.user.entity.User;
import com.gamingwallet.user.repository.UserRepository;
import com.gamingwallet.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User create(String fullName){
        User user = User.builder()
                .fullName(fullName)
                .walletBalance(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        System.out.println("Searching for user: " + id);
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(id));
    }
}
