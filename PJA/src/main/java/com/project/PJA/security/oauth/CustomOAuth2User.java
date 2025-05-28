package com.project.PJA.security.oauth;

import com.project.PJA.user.entity.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oAuth2User;
    private final String uid;  // 사용자 고유 식별자
    private final String role;

    public CustomOAuth2User(OAuth2User oAuth2User, String uid) {
        this.oAuth2User = oAuth2User;
        this.uid = uid;
        this.role = UserRole.ROLE_USER.toString();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oAuth2User.getName(); // 일반적으로 "sub" 또는 고유 식별자
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return (String) oAuth2User.getAttributes().get("email");
    }

    public String getRole() {
        return role;
    }
}
