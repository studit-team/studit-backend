package com.studit.domain.user.security;

import com.studit.domain.user.dto.UserDTO;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final UserDTO user;

    public CustomUserDetails(UserDTO user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 코드를 Spring Security 권한으로 변환
        String authorCode = user.getAuthorCode();
        if (authorCode == null || authorCode.isEmpty()) {
            return Collections.emptyList();
        }
        String authority = authorCode.startsWith("ROLE_") ? authorCode : "ROLE_" + authorCode;
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // 이메일을 username으로 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // lgn_aprv_yn이 'Y'이면 잠금 해제
        return "Y".equals(user.getLgnAprvYn());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getUserStatusCode() == null ||
                "Y".equals(user.getUserStatusCode()) ||
                "Y".equals(user.getUserStatusCode());
    }

    public String getUserId() {
        return user.getUserId();
    }

    public String getAuthorCode() {
        return user.getAuthorCode();
    }

    public String getName() {return user.getName();}
}