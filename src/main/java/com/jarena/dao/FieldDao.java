package com.jarena.dao;

import com.jarena.model.Field;
import java.util.List;

public interface FieldDao {
    List<Field> findAll();
    Field findById(long id);
    void save(Field field);
    void update(Field field);
    void delete(long id);
    List<Field> findAvailable();
}
