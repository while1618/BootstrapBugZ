package org.bootstrapbugz.api.shared.validator.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.bootstrapbugz.api.shared.validator.FieldMatch;

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
      final Object firstObj = BeanUtils.getProperty(obj, firstFieldName);
      final Object secondObj = BeanUtils.getProperty(obj, secondFieldName);

      return firstObj == null && secondObj == null
          || firstObj != null && firstObj.equals(secondObj);
    } catch (final Exception ignore) {
      // no need to do anything
    }
    return true;
  }
}
