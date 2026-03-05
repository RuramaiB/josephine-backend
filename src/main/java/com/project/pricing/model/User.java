package com.project.pricing.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * System user entity for authentication and authorization
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
@org.springframework.data.annotation.TypeAlias("User")
public class User implements UserDetails {

    @Id
    private String id;

    @com.fasterxml.jackson.annotation.JsonBackReference
    @org.springframework.data.mongodb.core.mapping.DBRef
    private java.util.List<com.project.pricing.token.Token> tokens;

    private String firstname;
    private String lastname;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String physicalAddress;
    private String gender;
    private String dateOfBirth;

    private Role role;

    @Builder.Default
    private boolean enabled = true;

    // Optional: Link to Officer profile if this user is an officer
    private String officerId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // Use email as username
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
        return enabled;
    }
}
