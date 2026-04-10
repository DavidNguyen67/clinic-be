package com.camel.clinic.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class MapperUtils {
    private static final Set<String> DEFAULT_EXCLUDED_FIELDS = Set.of("href");
    private static final Set<String> FIELDS_TO_KEEP = Set.of("id", "href", "atSchemaLocation");

    public static void overrideObject(Object source, Object target) {
        String[] ignoreProperties = getNullAndExcludedPropertyNames(source);
        BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    private static String[] getNullAndExcludedPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        var pds = src.getPropertyDescriptors();

        Set<String> emptyOrExcluded = new HashSet<>(DEFAULT_EXCLUDED_FIELDS);
        for (var pd : pds) {
            Object value = src.getPropertyValue(pd.getName());
            if (value == null) {
                emptyOrExcluded.add(pd.getName());
            }
        }
        return emptyOrExcluded.toArray(new String[0]);
    }

    // Bước 1: Set tất cả field = null (trừ các field cần giữ lại)
    private static void resetTargetFields(Object target) {
        Class<?> clazz = target.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) continue;
                if (FIELDS_TO_KEEP.contains(field.getName())) continue;

                field.setAccessible(true);
                try {
                    if (field.getType().isPrimitive()) {
                        if (field.getType() == boolean.class) field.setBoolean(target, false);
                        else if (field.getType() == char.class) field.setChar(target, '\u0000');
                        else field.set(target, 0);
                    } else {
                        field.set(target, null);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot reset field: " + field.getName(), e);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    public static void convertModelToEntity(Object source, Object target) {
        if (source == null || target == null) return;

        // Bước 1: Set tất cả field của target = null (trừ FIELDS_TO_KEEP)
        resetTargetFields(target);
        // Bước 2: Sao chép các trường từ source sang target, bỏ qua các trường null và các trường đã giữ lại
        overrideObject(source, target);
    }

    public static void mergeDataSourceToTarget(Object source, Object target) {
        if (source == null || target == null) return;
        overrideObject(source, target);
    }
}
