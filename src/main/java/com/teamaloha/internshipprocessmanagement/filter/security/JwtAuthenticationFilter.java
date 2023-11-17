package com.teamaloha.internshipprocessmanagement.filter.security;

import com.teamaloha.internshipprocessmanagement.service.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// OncePerRequestFilter is provided us by Spring such that it already implements Filter interface.
// We could have implemented Filter interface too, but this way is easier and better.
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        // Check if the JWT Token is come in the Authorization header.
        if (authHeader == null || authHeader.trim().equals("") || !authHeader.startsWith("Bearer ")) {
            // If "Authorization" header does not exist, then skip this filter and continue to the next filter.
            filterChain.doFilter(request, response);
            return;
        }

        final String jwtToken = authHeader.substring(7);

        try {
            if (jwtService.isTokenValid(jwtToken)) {
                // Try to get user from the database in that way we check if there exists such user.
                // Also check expiration date. (jwtService.isTokenValid does this)
                String username = jwtService.extractUsername(jwtToken);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Update SecurityContextHolder.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            logger.error("There is an error with validating the token and extracting credentials.");
            throw e;
        }
        filterChain.doFilter(request, response);
    }
}
