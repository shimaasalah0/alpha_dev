package com.jarena.service;

import com.jarena.dao.FieldDao;
import com.jarena.model.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldServiceImpl implements FieldService {

    @Autowired
    private FieldDao fieldDao;

    @Override
    public List<Field> getAllFields() {
        return fieldDao.findAll();
    }

    @Override
    public Field getFieldById(long id) {
        return fieldDao.findById(id);
    }

    @Override
    public void addField(Field field) {
        fieldDao.save(field);
    }

    @Override
    public void updateField(Field field) {
        fieldDao.update(field);
    }

    @Override
    public void deleteField(long id) {
        fieldDao.delete(id);
    }

    @Override
    public List<Field> getAvailableFields() {
        return fieldDao.findAvailable();
    }
}
