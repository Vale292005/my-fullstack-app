package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final CustomUserDetailsService userDetailsService;
  private final JwtBlacklistService jwtBlacklistService;

  public JwtAuthenticationFilter(JwtService jwtService,
                                 CustomUserDetailsService userDetailsService,
                                 JwtBlacklistService jwtBlacklistService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
    this.jwtBlacklistService = jwtBlacklistService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)

    throws ServletException, IOException {


    String path = request.getRequestURI();
    System.out.println("Request URI: " + request.getRequestURI());
    System.out.println("Authorization header: " + request.getHeader("Authorization"));

    // ⚡ Ignorar rutas públicas
    if (path.startsWith("/auth") || path.startsWith("/hoteles")) {
      filterChain.doFilter(request, response);
      return;
    }

    final String authHeader = request.getHeader("Authorization");

    // ⚡ Si no hay token o no empieza con Bearer, simplemente pasar
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    final String jwt = authHeader.substring(7);
    final String username = jwtService.extractUsername(jwt);

    // ⚡ Bloquear solo si el token está en blacklist
    if (jwtBlacklistService.isTokenBlacklisted(jwt)) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
      return;
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
          userDetails,
          null,
          userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    filterChain.doFilter(request, response);
  }

}
