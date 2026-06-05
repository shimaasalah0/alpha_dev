package com.jarena.dao;

import com.jarena.model.User;

public interface UserDao {
    User findByEmail(String email);
    void save(User user);
    boolean emailExists(String email);
}
