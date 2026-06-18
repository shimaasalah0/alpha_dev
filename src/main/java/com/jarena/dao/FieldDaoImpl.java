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
        Long active = sessionFactory.getCurrentSession()
                .createQuery(
                    "SELECT COUNT(b) FROM Booking b WHERE b.field.id = :id " +
                    "AND b.status NOT IN ('CANCELLED','COMPLETED')", Long.class)
                .setParameter("id", id)
                .uniqueResult();
        if (active != null && active > 0) {
            throw new IllegalStateException("Cannot delete field with active bookings.");
        }
        Field field = sessionFactory.getCurrentSession().get(Field.class, id);
        if (field != null) {
            sessionFactory.getCurrentSession().delete(field);
        }
    }

    @Override
    @Transactional
    public long countAll() {
        Long c = sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(f) FROM Field f", Long.class).uniqueResult();
        return c != null ? c : 0L;
    }

    @Override
    @Transactional
    public long countAvailable() {
        Long c = sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(f) FROM Field f WHERE f.available = true", Long.class).uniqueResult();
        return c != null ? c : 0L;
    }

    @Override
    @Transactional
    public boolean existsByName(String name, Long excludeId) {
        String hql = excludeId != null
                ? "SELECT COUNT(f) FROM Field f WHERE f.name = :name AND f.id != :excludeId"
                : "SELECT COUNT(f) FROM Field f WHERE f.name = :name";
        var query = sessionFactory.getCurrentSession()
                .createQuery(hql, Long.class)
                .setParameter("name", name);
        if (excludeId != null) query.setParameter("excludeId", excludeId);
        Long c = query.uniqueResult();
        return c != null && c > 0;
    }

    @Override
    @Transactional
    public List<Field> findAvailable() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Field f WHERE f.available = true ORDER BY f.name ASC", Field.class)
                .getResultList();
    }
}
