package com.app.webapp.model.user;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
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

    private LocalDateTime updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    private LocalDateTime logoutFromAllDevicesAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    private boolean activated = false;

    private boolean nonLocked = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void updateUpdatedAt() {
        this.updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    public void updateLogoutFromAllDevicesAt() {
        this.logoutFromAllDevicesAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
