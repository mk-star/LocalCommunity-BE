package com.example.backend.signLogin;

import com.example.backend.user.User;
import com.example.backend.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final Key secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<Cookie> jwtTokenCookie = Arrays.stream(cookies)
                .filter(cookie -> "jwtToken".equals(cookie.getName()))
                .findFirst();

        if (jwtTokenCookie.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtTokenCookie.get().getValue();

        try {
            if (JwtTokenUtil.isExpired(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String userId = JwtTokenUtil.getuserId(token);

            System.out.println("Extracted userId from token: " + userId);

            User loginUser = userService.getLoginUserByuserId(userId);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginUser.getUserId(), null);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e) {
            // JWT validation failed, continue filter chain without authentication
        }

        filterChain.doFilter(request, response);
    }
}
