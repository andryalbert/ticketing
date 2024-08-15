package com.demo.ticketing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "users")
@ToString(exclude = "tickets")
public class User extends AbstractEntity<String> implements UserDetails {

    @Column(unique = true)
    @NotBlank(message = "username ne peut pas être vide")
    private String userName;

    @Email(message = "l'email doit être correcte")
    @Column(unique = true)
    @NotBlank(message = "l'email ne peut pas être vide")
    private String email;

    @Pattern(regexp = "a-z",message = "le mot de passe doit contenir un caractère majuscule,miniscule,spéciaux et numérique")
    @Min(value = 8,message = "le mot de passe doit contenir au minimum huit caractère")
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Ticket> tickets;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
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
