package org.bootstrapbugz.api.user.model

import com.google.common.base.Objects
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.io.Serial
import java.io.Serializable

@Entity
@Table(
  name = "roles",
  indexes = [Index(name = "idx_role_name", columnList = "role_name", unique = true)]
)
class Role(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "role_id")
  var id: Long? = null,
  @Column(name = "role_name", unique = true, nullable = false)
  @Enumerated(EnumType.STRING)
  var name: RoleName = RoleName.USER
) : Serializable {
  companion object {
    @Serial private val serialVersionUID = 3717126169522609755L
  }

  constructor(name: RoleName) : this(null, name)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || javaClass != other.javaClass) return false
    val role = other as Role
    return Objects.equal(id, role.id) && name == role.name
  }

  override fun hashCode(): Int {
    return Objects.hashCode(id, name)
  }

  enum class RoleName {
    USER,
    ADMIN
  }
}
