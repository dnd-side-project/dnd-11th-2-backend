package com.dnd.runus.auth.userdetails;

import com.dnd.runus.global.constant.MemberRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;

@Getter
@ToString
@RequiredArgsConstructor(access = PRIVATE, staticName = "of")
public class AuthUserDetails implements UserDetails {
    private final long id;
    private final Collection<? extends GrantedAuthority> authorities;

    public static AuthUserDetails of(long memberId, MemberRole role) {
        return AuthUserDetails.of(memberId, List.of(new SimpleGrantedAuthority(role.getValue())));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return String.valueOf(id);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDetails)) return false;
        AuthUserDetails user = (AuthUserDetails) o;
        return Objects.equals(id, user.id);
    }
}
