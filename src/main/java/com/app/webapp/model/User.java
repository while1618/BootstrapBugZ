package com.app.webapp.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotEmpty(message = "Please enter a first name.")
    @Size(min = 2, max = 16, message = "First name must be between 2 and 16 characters.")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "Please enter a valid first name.")
    private String firstName;

    @NotEmpty(message = "Please enter a last name.")
    @Size(min = 2, max = 16, message = "Last name must be between 2 and 16 characters.")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "Please enter a valid last name.")
    private String lastName;

    @NotEmpty(message = "Please enter a last name.")
    @Size(min = 2, max = 16, message = "Username must be between 2 and 16 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Please enter a valid last name.")
    private String username;

    @NotEmpty(message = "Please enter a email.")
    @Email(message = "Please enter valid a email.")
    private String email;

    @NotEmpty(message = "Please enter a password.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",message = "Please enter a valid password.")
    private String password;

    @NotEmpty(message = "Please repeat a password.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",message = "Please enter a valid password.")
    private String confirmPassword;

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
}
