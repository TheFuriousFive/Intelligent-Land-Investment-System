package com.example.landapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter // Use Getter/Setter instead of @Data for cleaner JPA inheritance
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(name = "ric_code", unique = true)
    private String ricCode;

    @Column(unique = true, nullable = false)
    private String email;

    private String contactNumber; // RESTORED

    @Column(nullable = false)
    private String passwordHash;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt; // RESTORED

    @UpdateTimestamp
    private LocalDateTime updatedAt; // RESTORED

    // --- UserDetails Implementation ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}