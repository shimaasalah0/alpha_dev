package com.jarena.service;

import com.jarena.model.Field;
import java.util.List;

public interface FieldService {
    List<Field> getAllFields();
    Field getFieldById(long id);
    String addField(Field field);
    String updateField(Field field);
    String deleteField(long id);
    List<Field> getAvailableFields();
    long getTotalFields();
    long getAvailableFieldsCount();
}
