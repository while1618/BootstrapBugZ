package com.app.webapp.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_tokens")
@Getter
@ToString
@NoArgsConstructor
public class VerificationToken {
    private static int MAX_NUMBER_OF_SENT = 3;
    private static int NUMBER_OF_DAYS = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_token_id")
    @Setter
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private LocalDateTime expirationDate = LocalDateTime.now().plusDays(NUMBER_OF_DAYS);

    @Setter
    private boolean used = false;

    private int numberOfSent = 0;

    public VerificationToken(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public void updateToken(String token) {
        this.token = token;
    }

    public void extendExpirationDate() {
        expirationDate = LocalDateTime.now().plusDays(NUMBER_OF_DAYS);
    }

    public void increaseNumberOfSent() {
        numberOfSent++;
    }

    public static int getMaxNumberOfSent() {
        return MAX_NUMBER_OF_SENT;
    }
}
