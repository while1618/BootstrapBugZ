package org.bootstrapbugz.api.auth.security.user.details;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomUserDetailsService extends UserDetailsService {
  UserDetails loadUserByUserId(Long userId);
}
