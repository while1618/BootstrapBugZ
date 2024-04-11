package org.bootstrapbugz.api.shared.validator

import jakarta.validation.Constraint
import jakarta.validation.Payload
import org.bootstrapbugz.api.shared.validator.impl.UsernameOrEmailImpl
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.ANNOTATION_CLASS)
@Retention(
    AnnotationRetention.RUNTIME
)
@Constraint(validatedBy = [UsernameOrEmailImpl::class])
@MustBeDocumented
annotation class UsernameOrEmail(
    val message: String = "",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
