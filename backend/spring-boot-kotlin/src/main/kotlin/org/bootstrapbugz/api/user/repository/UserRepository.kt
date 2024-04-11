package org.bootstrapbugz.api.user.repository

import org.bootstrapbugz.api.user.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {
    @EntityGraph(attributePaths = ["roles"])
    override fun findAll(pageable: Pageable): Page<User>
    @EntityGraph(attributePaths = ["roles"])
    fun findWithRolesById(id: Long): Optional<User>
    fun findByUsername(username: String): Optional<User>
    @EntityGraph(attributePaths = ["roles"])
    fun findWithRolesByUsername(username: String): Optional<User>
    fun findByEmail(email: String): Optional<User>
    fun findByUsernameOrEmail(username: String, email: String): Optional<User>
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
}
