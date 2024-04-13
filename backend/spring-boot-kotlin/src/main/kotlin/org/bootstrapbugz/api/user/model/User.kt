package org.bootstrapbugz.api.user.model

import com.google.common.base.Objects
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.io.Serial
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(
  name = "users",
  indexes =
    [
      Index(name = "idx_username", columnList = "username", unique = true),
      Index(name = "idx_email", columnList = "email", unique = true),
      Index(name = "idx_username_email", columnList = "username, email")
    ]
)
class User(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  var id: Long? = null,
  @Column(nullable = false) var firstName: String,
  @Column(nullable = false) var lastName: String,
  @Column(unique = true, nullable = false) var username: String,
  @Column(unique = true, nullable = false) var email: String,
  @Column(nullable = false) var password: String,
  @Column(nullable = false) var active: Boolean = false,
  @Column(nullable = false) var lock: Boolean = false,
  @Column(nullable = false) var createdAt: LocalDateTime = LocalDateTime.now(),
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
    name = "user_roles",
    joinColumns = [JoinColumn(name = "user_id")],
    inverseJoinColumns = [JoinColumn(name = "role_id")]
  )
  var roles: Set<Role> = setOf()
) : Serializable {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || javaClass != other.javaClass) return false
    val user = other as User
    return (active == user.active &&
      lock == user.lock &&
      Objects.equal(id, user.id) &&
      Objects.equal(firstName, user.firstName) &&
      Objects.equal(lastName, user.lastName) &&
      Objects.equal(username, user.username) &&
      Objects.equal(email, user.email) &&
      Objects.equal(password, user.password) &&
      Objects.equal(roles, user.roles))
  }

  override fun hashCode(): Int {
    return Objects.hashCode(id, firstName, lastName, username, email, password, active, lock, roles)
  }

  companion object {
    @Serial private val serialVersionUID = -7881387078460754905L
  }
}
