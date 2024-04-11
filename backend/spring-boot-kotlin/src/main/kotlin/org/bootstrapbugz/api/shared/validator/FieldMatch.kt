package org.bootstrapbugz.api.shared.validator

import jakarta.validation.Constraint
import jakarta.validation.Payload
import org.bootstrapbugz.api.shared.validator.impl.FieldMatchImpl
import kotlin.reflect.KClass

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
