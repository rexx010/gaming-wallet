package com.gamingwallet.user.service;

import com.gamingwallet.user.entity.User;

public interface UserService {
    User create(String fullName);
    User findById(Long id);
    User save(User user);
}
