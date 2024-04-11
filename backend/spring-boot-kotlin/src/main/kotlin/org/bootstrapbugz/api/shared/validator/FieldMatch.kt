package org.bootstrapbugz.api.shared.validator

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass
import org.bootstrapbugz.api.shared.validator.impl.FieldMatchImpl

@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [FieldMatchImpl::class])
@MustBeDocumented
annotation class FieldMatch(
  val message: String = "",
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = [],
  val first: String,
  val second: String
)
