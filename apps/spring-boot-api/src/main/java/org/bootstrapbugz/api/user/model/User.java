package org.bootstrapbugz.api.user.model;

import com.google.common.base.Objects;
import java.io.Serial;
import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
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

  @Column(columnDefinition = "TIMESTAMP (3)")
  private LocalDateTime updatedAt = LocalDateTime.now(Clock.tickMillis(ZoneId.of("UTC")));

  @Column(columnDefinition = "TIMESTAMP (3)")
  private LocalDateTime lastLogout = LocalDateTime.now(Clock.tickMillis(ZoneId.of("UTC")));

  private boolean activated = false;

  private boolean nonLocked = true;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  public void addRole(Role role) {
    this.roles.add(role);
  }

  public boolean isAdmin() {
    boolean admin = false;
    for (Role role : this.roles) {
      if (role.getName().equals(RoleName.ADMIN)) {
        admin = true;
        break;
      }
    }
    return admin;
  }

  public void updateUpdatedAt() {
    this.updatedAt = LocalDateTime.now(Clock.tickMillis(ZoneId.of("UTC")));
  }

  public void updateLastLogout() {
    this.lastLogout = LocalDateTime.now(Clock.tickMillis(ZoneId.of("UTC")));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof User)) {
      return false;
    }
    User user = (User) o;
    return Objects.equal(id, user.id)
        && Objects.equal(username, user.username)
        && Objects.equal(email, user.email);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, username, email);
  }
}
