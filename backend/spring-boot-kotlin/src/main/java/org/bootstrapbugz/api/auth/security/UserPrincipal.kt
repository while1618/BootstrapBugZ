package org.bootstrapbugz.api.auth.security

import org.bootstrapbugz.api.user.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

data class UserPrincipal(
    val id: Long? = null,
    private val firstName: String?,
    private val lastName: String?,
    private val username: String?,
    private val email: String?,
    private val password: String?,
    private val enabled: Boolean,
    private val accountNonLocked: Boolean,
    private val createdAt: LocalDateTime?,
    private val authorities: Collection<GrantedAuthority>?
) : UserDetails {

    companion object {
        private const val serialVersionUID = 5954870422841373076L

        fun create(user: User): UserPrincipal {
            val authorities = user.roles?.map { SimpleGrantedAuthority(it.name.name) }
            return UserPrincipal(
                user.id,
                user.firstName,
                user.lastName,
                user.username,
                user.email,
                user.password,
                user.active,
                !user.lock,
                user.createdAt,
                authorities
            )
        }
    }

    override fun getUsername(): String? = username
    override fun getPassword(): String? = password
    override fun getAuthorities(): Collection<GrantedAuthority>? = authorities
    override fun isEnabled(): Boolean = enabled
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = accountNonLocked
    override fun isCredentialsNonExpired(): Boolean = true
}