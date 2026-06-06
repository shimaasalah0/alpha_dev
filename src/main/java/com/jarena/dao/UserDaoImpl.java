package com.jarena.dao;

import com.jarena.model.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public User findById(long id) {
        return sessionFactory.getCurrentSession().get(User.class, id);
    }

    @Override
    @Transactional
    public User findByEmail(String email) {
        List<User> results = sessionFactory.getCurrentSession()
                .createQuery("FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @Transactional
    public void save(User user) {
        sessionFactory.getCurrentSession().save(user);
    }

    @Override
    @Transactional
    public boolean emailExists(String email) {
        Long count = sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .uniqueResult();
        return count != null && count > 0;
    }

    @Override
    @Transactional
    public long countAll() {
        Long count = sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(u) FROM User u", Long.class)
                .uniqueResult();
        return count != null ? count : 0L;
    }
}
