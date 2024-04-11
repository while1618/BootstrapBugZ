package org.bootstrapbugz.api.user.payload.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.Objects
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class UserDTO(
  val id: Long?,
  val firstName: String?,
  val lastName: String?,
  val username: String?,
  val email: String?,
  @JsonInclude(Include.NON_NULL) val active: Boolean?,
  @JsonInclude(Include.NON_NULL) val lock: Boolean?,
  @JsonProperty("roles") @SerializedName("roles") val roleDTOs: Set<RoleDTO>?,
  val createdAt: LocalDateTime?
) {
  override fun equals(o: Any?): Boolean {
    if (this === o) return true
    if (o == null || this::class != o::class) return false
    val (id1, firstName1, lastName1, username1, email1, active1, lock1, roleDTOs1) = o as UserDTO
    return (active === active1 &&
      lock === lock1 &&
      Objects.equal(id, id1) &&
      Objects.equal(firstName, firstName1) &&
      Objects.equal(lastName, lastName1) &&
      Objects.equal(username, username1) &&
      Objects.equal(email, email1) &&
      Objects.equal(roleDTOs, roleDTOs1))
  }

  override fun hashCode(): Int {
    return Objects.hashCode(id, firstName, lastName, username, email, active, lock, roleDTOs)
  }
}
