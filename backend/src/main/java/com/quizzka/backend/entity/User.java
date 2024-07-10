package com.quizzka.backend.entity;

import com.quizzka.backend.payload.response.QuizResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {
    @Id
    @Getter @Setter
    private String id;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is mandatory")
    @Indexed(unique = true)
    @Getter @Setter
    private String email;

    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$",
            message = "Password must be at least 8 characters long, contain at least one digit, one lower case letter, one upper case letter, and one special character"
    )
    @NotBlank(message = "Password is mandatory")
    private String password;

    @Getter @Setter
    private String phoneNumber;

    @NotBlank(message = "First name is mandatory")
    @Getter @Setter
    private String firstname;

    @NotBlank(message = "Last name is mandatory")
    @Getter @Setter
    private String lastname;

    @NotBlank(message = "Username is mandatory")
    @Indexed(unique = true)
    @Setter
    private String username;

    @NotBlank(message = "Gender is mandatory")
    @Getter @Setter
    private String gender;

    @NotNull(message = "Date of birth is mandatory")
    @Getter @Setter
    private LocalDateTime dob;

    @NotBlank(message = "Account type is mandatory")
    @Getter @Setter
    private String accountType;

    @Getter @Setter
    private int age;

    @NotBlank(message = "Country is mandatory")
    @Getter @Setter
    private String country;

    @Getter @Setter
    private String resetToken;

    @Getter @Setter
    private LocalDateTime resetTokenExpiry;

    @Getter @Setter
    private String loginType;

    @Getter @Setter
    private String league;

    @Getter @Setter
    private int xp;

    @Getter @Setter
    private int score;

    @Getter @Setter
    private boolean rememberMe;

    @Getter @Setter
    private Date createdAt;

    @Getter @Setter
    private Date updatedAt;

    public String getFullName() {
        return firstname + " " + lastname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username; // Changed to return username instead of email
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}