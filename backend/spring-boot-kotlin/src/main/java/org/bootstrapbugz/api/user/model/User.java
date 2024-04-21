package org.bootstrapbugz.api.user.model;

import com.google.common.base.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "users",
    indexes = {
      @Index(name = "idx_username", columnList = "username", unique = true),
      @Index(name = "idx_email", columnList = "email", unique = true),
      @Index(name = "idx_username_email", columnList = "username, email")
    })
public class User implements Serializable {
  @Serial private static final long serialVersionUID = -7881387078460754905L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Builder.Default
  @Column(nullable = false)
  private Boolean active = false;

  @Builder.Default
  @Column(nullable = false)
  private Boolean lock = false;

  @Builder.Default
  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  @Column(nullable = false)
  private Set<Role> roles;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return active == user.active
        && lock == user.lock
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
        id, firstName, lastName, username, email, password, active, lock, roles);
  }
}
