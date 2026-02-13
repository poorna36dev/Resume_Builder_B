package com.svu.resume.filter;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.svu.resume.document.User;
import com.svu.resume.repository.UserRepository;
import com.svu.resume.util.Jwtutil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final Jwtutil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        log.info("Processing request: {} {}", request.getMethod(), path);

        // 🔥 Skip auth endpoints

        String header = request.getHeader("Authorization");
        String token = null;
        String userId = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        } else {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            userId = jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            log.info("Token invalid or expired");
            filterChain.doFilter(request, response);
            return;
        }

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                log.info("Received token: {}", token);
                log.info("Extracted userId: {}", userId);

                if (jwtUtil.validateToken(token)) {

                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
                            new ArrayList<>());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                log.error("JWT validation error: ", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}
