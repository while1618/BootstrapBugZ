package org.bootstrapbugz.api.user.model;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Role implements Serializable {
  @Serial private static final long serialVersionUID = 3717126169522609755L;

  @Id
  @Enumerated(EnumType.STRING)
  @Column(name = "role_name")
  private RoleName name;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Role role)) return false;
    return name == role.name;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }

  public enum RoleName {
    USER,
    ADMIN
  }
}
