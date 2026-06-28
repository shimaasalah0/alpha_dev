package com.jarena.service;

import com.jarena.model.User;
import java.util.List;

public interface UserService {
    User login(String email, String password);
    void register(User user);
    boolean resetPassword(String email, String newPassword);
    boolean emailExists(String email);
    long countAll();
    List<User> getAllUsers();
}
