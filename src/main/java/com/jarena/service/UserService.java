package com.jarena.service;

import com.jarena.model.User;

public interface UserService {
    User login(String email, String password);
    void register(User user);
    boolean emailExists(String email);
}
