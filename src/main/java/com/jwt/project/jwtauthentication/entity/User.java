//package com.jwt.project.jwtauthentication.entity;
//
//import java.time.Instant;
//import java.util.Collection;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//import java.time.Instant;
//import java.util.Collection;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@ToString
//@Entity
//@Table(name = "user_table")
//public class User implements UserDetails {
//
//    @Id
//    private String userId;
//    private String name;
//    private String email;
//    private String password;
//    private String about;
//
//    // Fields for password reset
//    private String resetToken;
//    private Instant resetTokenExpiry;
//
//    // Fields for OTP
//    private String twoFactorCode;
//    private Instant twoFactorCodeExpiry;
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return null;
//    }
//
//    @Override
//    public String getUsername() {
//        return this.email;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}


package com.jwt.project.jwtauthentication.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_table")
public class User implements UserDetails {

    @Id
    private String userId;
    private String name;
    private String email;
    private String password;
    private String about;

    // Fields for password reset
    private String resetToken;
    private Instant resetTokenExpiry;

    // Fields for OTP
    private String twoFactorCode;
    private Instant twoFactorCodeExpiry;

    // Field for email verification
    private boolean emailVerified = false;

    // UserDetails interface methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.email;
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
        return emailVerified; // User is enabled only if email is verified
    }
}