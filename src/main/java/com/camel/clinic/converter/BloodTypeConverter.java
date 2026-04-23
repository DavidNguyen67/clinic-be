// converter/BloodTypeConverter.java
package com.camel.clinic.converter;

import com.camel.clinic.entity.Patient.BloodType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BloodTypeConverter implements AttributeConverter<BloodType, String> {

    @Override
    public String convertToDatabaseColumn(BloodType attribute) {
        if (attribute == null) return null;
        return attribute.getDisplayName(); // lưu "A+" vào DB
    }

    @Override
    public BloodType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return BloodType.fromValue(dbData); // đọc "A+" từ DB → enum
    }
}