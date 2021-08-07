package org.bootstrapbugz.api.auth.security.user.details;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.bootstrapbugz.api.user.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class UserPrincipal implements UserDetails {
  @Serial private static final long serialVersionUID = 5954870422841373076L;
  private final Long id;
  private final String firstName;
  private final String lastName;
  private final String username;
  @JsonIgnore private final String email;
  @JsonIgnore private final String password;
  private final boolean enabled;
  private final boolean accountNonLocked;
  private final Collection<? extends GrantedAuthority> authorities;

  public static UserPrincipal create(User user) {
    return new UserPrincipal(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getUsername(),
        user.getEmail(),
        user.getPassword(),
        user.isActivated(),
        user.isNonLocked(),
        getAuthorities(user));
  }

  private static List<GrantedAuthority> getAuthorities(User user) {
    return user.getRoles().stream()
        .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role.getName().name()))
        .toList();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }
}
