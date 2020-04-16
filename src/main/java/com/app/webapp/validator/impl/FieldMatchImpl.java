package com.app.webapp.validator.impl;

import com.app.webapp.validator.FieldMatch;
import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldMatchImpl implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    public void initialize(FieldMatch constraint) {
        firstFieldName = constraint.first();
        secondFieldName = constraint.second();
    }

    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        try {
            final Object firstObj = BeanUtils.getProperty(obj, firstFieldName);
            final Object secondObj = BeanUtils.getProperty(obj, secondFieldName);

            return firstObj == null && secondObj == null || firstObj != null && firstObj.equals(secondObj);
        } catch (final Exception ignore) {}
        return true;
    }
}
