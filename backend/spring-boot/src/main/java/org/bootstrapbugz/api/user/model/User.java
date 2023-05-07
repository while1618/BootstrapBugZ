package org.bootstrapbugz.api.user.model;

import com.google.common.base.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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

  private LocalDateTime createdAt = LocalDateTime.now();

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
        id, firstName, lastName, username, email, password, activated, nonLocked, createdAt, roles);
  }
}
