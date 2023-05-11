package org.bootstrapbugz.api.shared.validator.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.bootstrapbugz.api.shared.validator.FieldMatch;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchImpl implements ConstraintValidator<FieldMatch, Object> {
  private String firstFieldName;
  private String secondFieldName;

  @Override
  public void initialize(FieldMatch constraint) {
    firstFieldName = constraint.first();
    secondFieldName = constraint.second();
  }

  public boolean isValid(Object obj, ConstraintValidatorContext context) {
    try {
      final var wrapper = new BeanWrapperImpl(obj);

      final Object firstObj = wrapper.getPropertyValue(firstFieldName);
      final Object secondObj = wrapper.getPropertyValue(secondFieldName);

      return firstObj == null && secondObj == null
          || firstObj != null && firstObj.equals(secondObj);
    } catch (final Exception ignore) {
      // no need to do anything
    }
    return true;
  }
}
