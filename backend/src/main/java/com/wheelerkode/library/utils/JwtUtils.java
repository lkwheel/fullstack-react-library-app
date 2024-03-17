package com.wheelerkode.library.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public class JwtUtils {

    public static boolean isAdmin(Jwt jwt) {
        List<String> permissions = getPermissionsFromJwt(jwt);
        return permissions != null && permissions.contains("admin");
    }

    public static Jwt getJwtFromAuthentication(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            return null;
        }
        return (Jwt) authentication.getPrincipal();
    }

    public static String getUserIdFromJwt(Jwt jwt) {
        return jwt.getClaimAsString("sub");
    }

    private static List<String> getPermissionsFromJwt(Jwt jwt) {
        return jwt.getClaimAsStringList("permissions");
    }
}

