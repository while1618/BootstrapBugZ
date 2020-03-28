package com.app.webapp.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class VerificationToken {
    public static int MAX_NUMBER_OF_SENT = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_token_id")
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private LocalDateTime expirationDate;

    private boolean used;

    private int numberOfSent;

    public VerificationToken() {
        this.expirationDate = LocalDateTime.now().plusDays(1);
        this.used = false;
    }

    public VerificationToken(User user, String token) {
        this.user = user;
        this.token = token;
        this.expirationDate = LocalDateTime.now().plusDays(1);
        this.used = false;
    }
}
