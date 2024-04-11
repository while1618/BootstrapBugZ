package org.bootstrapbugz.api.user.service.impl

import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException
import org.bootstrapbugz.api.shared.message.service.MessageService
import org.bootstrapbugz.api.shared.payload.dto.AvailabilityDTO
import org.bootstrapbugz.api.user.mapper.UserMapper
import org.bootstrapbugz.api.user.model.User
import org.bootstrapbugz.api.user.payload.dto.UserDTO
import org.bootstrapbugz.api.user.payload.request.EmailAvailabilityRequest
import org.bootstrapbugz.api.user.payload.request.UsernameAvailabilityRequest
import org.bootstrapbugz.api.user.repository.UserRepository
import org.bootstrapbugz.api.user.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val messageService: MessageService
) : UserService {

    override fun findAll(pageable: Pageable): List<UserDTO> =
        userRepository.findAll(pageable)
            .map { UserMapper.userToSimpleUserDTO(it) }
            .toList()

    override fun findById(id: Long): UserDTO =
        userRepository.findById(id)
            .map(UserMapper::userToSimpleUserDTO)
            .orElseThrow {
                ResourceNotFoundException(messageService.getMessage("user.notFound"))
            }

    override fun findByUsername(username: String): UserDTO =
        userRepository.findByUsername(username)
            .map(UserMapper::userToSimpleUserDTO)
            .orElseThrow {
                ResourceNotFoundException(messageService.getMessage("user.notFound"))
            }

    override fun usernameAvailability(usernameAvailabilityRequest: UsernameAvailabilityRequest): AvailabilityDTO {
        val available = !userRepository.existsByUsername(usernameAvailabilityRequest.username)
        return AvailabilityDTO(available)
    }

    override fun emailAvailability(emailAvailabilityRequest: EmailAvailabilityRequest): AvailabilityDTO {
        val available = !userRepository.existsByEmail(emailAvailabilityRequest.email)
        return AvailabilityDTO(available)
    }
}
