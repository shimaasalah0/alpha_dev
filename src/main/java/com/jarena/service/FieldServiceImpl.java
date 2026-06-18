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
    public String addField(Field field) {
        if (field.getName() == null || field.getName().trim().isEmpty()) {
            return "Field name is required.";
        }
        if (field.getPricePerHour() <= 0) {
            return "Price per hour must be greater than 0.";
        }
        if (fieldDao.existsByName(field.getName().trim(), null)) {
            return "A field with this name already exists.";
        }
        field.setName(field.getName().trim());
        field.setAvailable(true);
        fieldDao.save(field);
        return null;
    }

    @Override
    public String updateField(Field field) {
        if (field.getName() == null || field.getName().trim().isEmpty()) {
            return "Field name is required.";
        }
        if (field.getPricePerHour() <= 0) {
            return "Price per hour must be greater than 0.";
        }
        if (fieldDao.existsByName(field.getName().trim(), field.getId())) {
            return "A field with this name already exists.";
        }
        field.setName(field.getName().trim());
        fieldDao.update(field);
        return null;
    }

    @Override
    public String deleteField(long id) {
        try {
            fieldDao.delete(id);
            return null;
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }

    @Override
    public List<Field> getAvailableFields() {
        return fieldDao.findAvailable();
    }

    @Override
    public long getTotalFields() {
        return fieldDao.countAll();
    }

    @Override
    public long getAvailableFieldsCount() {
        return fieldDao.countAvailable();
    }
}
