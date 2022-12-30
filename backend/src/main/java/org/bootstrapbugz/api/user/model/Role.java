package org.bootstrapbugz.api.user.model;

import com.google.common.base.Objects;
import java.io.Serial;
import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Role implements Serializable {
  @Serial private static final long serialVersionUID = 3717126169522609755L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "role_id")
  private Long id;

  @Column(unique = true, name = "role_name")
  @Enumerated(EnumType.STRING)
  private RoleName name;

  public Role(RoleName name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Role role)) return false;
    return Objects.equal(id, role.id) && Objects.equal(name, role.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, name);
  }

  public enum RoleName {
    USER,
    ADMIN
  }
}
