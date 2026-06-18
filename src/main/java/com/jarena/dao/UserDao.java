package com.jarena.dao;

import com.jarena.model.User;
import java.util.List;

public interface UserDao {
    User findByEmail(String email);
    User findById(long id);
    void save(User user);
    boolean emailExists(String email);
    long countAll();
    List<User> findAll();
}
