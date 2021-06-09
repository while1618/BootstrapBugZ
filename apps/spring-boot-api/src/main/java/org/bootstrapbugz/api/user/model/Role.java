package org.bootstrapbugz.api.user.model;

import java.io.Serial;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Role implements Serializable {
  @Serial private static final long serialVersionUID = 3717126169522609755L;

  @Id
  @Enumerated(EnumType.STRING)
  @Column(name = "role_name")
  private RoleName name;

  public enum RoleName {
    USER,
    ADMIN
  }
}
