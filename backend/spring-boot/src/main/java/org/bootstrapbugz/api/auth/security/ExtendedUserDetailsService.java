package org.bootstrapbugz.api.auth.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface ExtendedUserDetailsService extends UserDetailsService {
  UserDetails loadUserByUserId(Long userId);
}
