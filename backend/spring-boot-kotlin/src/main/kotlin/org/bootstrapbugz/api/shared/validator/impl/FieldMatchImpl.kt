package org.bootstrapbugz.api.shared.validator.impl

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.bootstrapbugz.api.shared.validator.FieldMatch
import org.springframework.beans.BeanWrapperImpl

class FieldMatchImpl : ConstraintValidator<FieldMatch, Any?> {
  private var firstFieldName: String = ""
  private var secondFieldName: String = ""

  override fun initialize(constraint: FieldMatch) {
    firstFieldName = constraint.first
    secondFieldName = constraint.second
  }

  override fun isValid(obj: Any?, context: ConstraintValidatorContext): Boolean {
    val wrapper = BeanWrapperImpl(obj)
    val first = wrapper.getPropertyValue(firstFieldName) as? String
    val second = wrapper.getPropertyValue(secondFieldName) as? String
    return first == null && second == null || first != null && first == second
  }
}
