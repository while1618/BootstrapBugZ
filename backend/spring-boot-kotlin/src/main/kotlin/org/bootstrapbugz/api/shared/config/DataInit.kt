package org.bootstrapbugz.api.shared.config

import net.datafaker.Faker
import org.bootstrapbugz.api.user.model.Role
import org.bootstrapbugz.api.user.model.Role.RoleName
import org.bootstrapbugz.api.user.model.User
import org.bootstrapbugz.api.user.repository.RoleRepository
import org.bootstrapbugz.api.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Profile("dev", "test")
@Component
class DataInit(
  private val userRepository: UserRepository,
  private val roleRepository: RoleRepository,
  private val bCryptPasswordEncoder: PasswordEncoder,
  private val environment: Environment
) : ApplicationRunner {
  private val userRole = Role(RoleName.USER)
  private val adminRole = Role(RoleName.ADMIN)

  @Value("\${spring.security.user.password}") private val password: String? = null

  private val faker: Faker = Faker()

  override fun run(args: ApplicationArguments) {
    saveRoles()
    saveUsers()
  }

  private fun saveRoles() {
    roleRepository.saveAll(java.util.List.of(userRole, adminRole))
  }

  private fun saveUsers() {
    userRepository.saveAll(
      listOf(
        User(
          firstName = "Admin",
          lastName = "Admin",
          username = "admin",
          email = "admin@localhost",
          password = bCryptPasswordEncoder.encode(password),
          active = true,
          lock = false,
          roles = setOf(userRole, adminRole)
        ),
        User(
          firstName = "User",
          lastName = "User",
          username = "user",
          email = "user@localhost",
          password = bCryptPasswordEncoder.encode(password),
          active = true,
          lock = false,
          roles = setOf(userRole)
        ),
      )
    )
    val activeProfile = environment.activeProfiles.firstOrNull()
    when (activeProfile) {
      "dev" -> devUsers()
      "test" -> testUsers()
    }
  }

  private fun devUsers() {
    val users =
      (1..100).map {
        User(
          firstName = faker.name().firstName(),
          lastName = faker.name().lastName(),
          username = faker.internet().username(),
          email = faker.internet().emailAddress(),
          password = bCryptPasswordEncoder.encode(password),
          active = true,
          lock = false,
          roles = setOf(userRole)
        )
      }
    userRepository.saveAll(users)
  }

  private fun testUsers() {
    userRepository.saveAll(
      listOf(
        User(
          firstName = "Deactivated1",
          lastName = "Deactivated1",
          username = "deactivated1",
          email = "deactivate1d@localhost",
          password = bCryptPasswordEncoder.encode(password),
          active = false,
          lock = false,
          roles = setOf(userRole)
        ),
        User(
          firstName = "Deactivated2",
          lastName = "Deactivated2",
          username = "deactivated2",
          email = "deactivated2@localhost",
          password = bCryptPasswordEncoder.encode(password),
          active = false,
          lock = false,
          roles = setOf(userRole)
        ),
        User(
          firstName = "Locked",
          lastName = "Locked",
          username = "locked",
          email = "locked@localhost",
          password = bCryptPasswordEncoder.encode(password),
          active = true,
          lock = true,
          roles = setOf(userRole)
        ),
        User(
          firstName = "Update1",
          lastName = "Update1",
          username = "update1",
          email = "update1@localhost",
          password = bCryptPasswordEncoder.encode(password),
          active = true,
          lock = false,
          roles = setOf(userRole)
        ),
        User(
          firstName = "Update2",
          lastName = "Update2",
          username = "update2",
          email = "update2@localhost",
          password = bCryptPasswordEncoder.encode(password),
          active = true,
          lock = false,
          roles = setOf(userRole)
        ),
        User(
          firstName = "Update3",
          lastName = "Update3",
          username = "update3",
          email = "update3@localhost",
          password = bCryptPasswordEncoder.encode(password),
          active = true,
          lock = false,
          roles = setOf(userRole)
        ),
        User(
          firstName = "Update4",
          lastName = "Update4",
          username = "update4",
          email = "update4@localhost",
          password = bCryptPasswordEncoder.encode(password),
          active = true,
          lock = false,
          roles = setOf(userRole)
        ),
        User(
          firstName = "Delete1",
          lastName = "Delete1",
          username = "delete1",
          email = "delete1@localhost",
          password = bCryptPasswordEncoder.encode(password),
          active = true,
          lock = false,
          roles = setOf(userRole)
        ),
        User(
          firstName = "Delete2",
          lastName = "Delete2",
          username = "delete2",
          email = "delete2@localhost",
          password = bCryptPasswordEncoder.encode(password),
          active = true,
          lock = false,
          roles = setOf(userRole)
        )
      )
    )
  }
}
