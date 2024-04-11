package org.bootstrapbugz.api.auth.security

import org.bootstrapbugz.api.shared.error.exception.UnauthorizedException
import org.bootstrapbugz.api.shared.message.service.MessageService
import org.bootstrapbugz.api.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository,
    private val messageService: MessageService
) : UserDetailsService {

    @Transactional
    fun loadUserByUserId(userId: Long): UserDetails =
        userRepository.findById(userId)
            .map(UserPrincipal.Companion::create)
            .orElseThrow { UnauthorizedException(messageService.getMessage("token.invalid")) }

    @Transactional
    override fun loadUserByUsername(username: String): UserDetails =
        userRepository.findByUsernameOrEmail(username, username)
            .map(UserPrincipal.Companion::create)
            .orElseThrow { UnauthorizedException(messageService.getMessage("token.invalid")) }
}
