package org.bootstrapbugz.api.user.repository

import org.bootstrapbugz.api.user.model.Role
import org.bootstrapbugz.api.user.model.Role.RoleName
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface RoleRepository : JpaRepository<Role, Long> {
    fun findAllByNameIn(roleNames: Set<RoleName>): List<Role>
    fun findByName(roleName: RoleName): Optional<Role>
}
