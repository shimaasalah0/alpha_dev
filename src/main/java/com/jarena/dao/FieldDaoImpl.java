package com.jarena.dao;

import com.jarena.model.Field;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class FieldDaoImpl implements FieldDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public List<Field> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Field f ORDER BY f.name ASC", Field.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Field findById(long id) {
        return sessionFactory.getCurrentSession().get(Field.class, id);
    }

    @Override
    @Transactional
    public void save(Field field) {
        sessionFactory.getCurrentSession().save(field);
    }

    @Override
    @Transactional
    public void update(Field field) {
        sessionFactory.getCurrentSession().update(field);
    }

    @Override
    @Transactional
    public void delete(long id) {
        Field field = sessionFactory.getCurrentSession().get(Field.class, id);
        if (field != null) {
            sessionFactory.getCurrentSession().delete(field);
        }
    }

    @Override
    @Transactional
    public List<Field> findAvailable() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Field f WHERE f.available = true ORDER BY f.name ASC", Field.class)
                .getResultList();
    }
}
