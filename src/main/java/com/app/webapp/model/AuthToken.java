package com.app.webapp.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auth_tokens")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AuthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_token_id")
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private LocalDateTime expirationDate = LocalDateTime.now().plusDays(AuthTokenProperties.NUMBER_OF_DAYS_VALID);

    private String usage;

    public AuthToken(User user, String token, String usage) {
        this.user = user;
        this.token = token;
        this.usage = usage;
    }

    public void updateToken(String token) {
        this.token = token;
    }

    public void extendExpirationDate() {
        expirationDate = LocalDateTime.now().plusDays(AuthTokenProperties.NUMBER_OF_DAYS_VALID);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }
}
