package com.jarena.service;

import com.jarena.dao.UserDao;
import com.jarena.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User login(String email, String password) {
        User user = userDao.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public void register(User user) {
        user.setRole("CUSTOMER");
        user.setCreatedAt(LocalDateTime.now());
        userDao.save(user);
    }

    @Override
    public boolean emailExists(String email) {
        return userDao.emailExists(email);
    }
}
