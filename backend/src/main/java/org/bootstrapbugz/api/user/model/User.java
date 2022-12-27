package org.bootstrapbugz.api.user.model;

import com.google.common.base.Objects;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class User implements Serializable {
  @Serial private static final long serialVersionUID = -7881387078460754905L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  private String firstName;

  private String lastName;

  @Column(unique = true)
  private String username;

  @Column(unique = true)
  private String email;

  private String password;

  private boolean activated = false;

  private boolean nonLocked = true;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User user)) return false;
    return activated == user.activated
        && nonLocked == user.nonLocked
        && Objects.equal(id, user.id)
        && Objects.equal(firstName, user.firstName)
        && Objects.equal(lastName, user.lastName)
        && Objects.equal(username, user.username)
        && Objects.equal(email, user.email)
        && Objects.equal(password, user.password)
        && Objects.equal(roles, user.roles);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        id, firstName, lastName, username, email, password, activated, nonLocked, roles);
  }
}
