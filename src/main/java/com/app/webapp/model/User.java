package com.app.webapp.model;

import com.app.webapp.validator.PasswordMatches;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
@PasswordMatches
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotEmpty(message = "NotEmpty.firstName")
    @Size(min = 2, max = 16, message = "Size.firstName")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "Regex.firstName")
    private String firstName;

    @NotEmpty(message = "NotEmpty.lastName")
    @Size(min = 2, max = 16, message = "Size.lastName")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "Regex.lastName")
    private String lastName;

    @NotEmpty(message = "NotEmpty.username")
    @Size(min = 2, max = 16, message = "Size.username")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Regex.username")
    @Column(unique = true)
    private String username;

    @NotEmpty(message = "NotEmpty.email")
    @Email(message = "Regex.email")
    @Column(unique = true)
    private String email;

    @NotEmpty(message = "NotEmpty.password")
//    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Regex.password")
    private String password;

    @Transient
    private String confirmPassword;

    private boolean activated;

    public User() {
        this.activated = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
