package com.jarena.service;

import com.jarena.model.Field;
import java.util.List;

public interface FieldService {
    List<Field> getAllFields();
    Field getFieldById(long id);
    void addField(Field field);
    void updateField(Field field);
    void deleteField(long id);
    List<Field> getAvailableFields();
}
