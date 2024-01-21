package org.bootstrapbugz.api.user.service

import org.bootstrapbugz.api.shared.payload.dto.AvailabilityDTO
import org.bootstrapbugz.api.user.payload.dto.UserDTO
import org.bootstrapbugz.api.user.payload.request.EmailAvailabilityRequest
import org.bootstrapbugz.api.user.payload.request.UsernameAvailabilityRequest
import org.springframework.data.domain.Pageable

interface UserService {
    fun findAll(pageable: Pageable): List<UserDTO>
    fun findById(id: Long?): UserDTO
    fun findByUsername(username: String): UserDTO
    fun usernameAvailability(usernameAvailabilityRequest: UsernameAvailabilityRequest): AvailabilityDTO
    fun emailAvailability(emailAvailabilityRequest: EmailAvailabilityRequest): AvailabilityDTO
}
