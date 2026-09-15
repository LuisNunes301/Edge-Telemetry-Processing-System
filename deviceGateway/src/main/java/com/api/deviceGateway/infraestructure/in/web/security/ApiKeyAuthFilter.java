package com.api.deviceGateway.infrastructure.in.web.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${gateway.api.key}")
    private String validApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        System.out.println("Interceptando URI: " + requestUri); // Log temporário para debug

        // Aplica o filtro estritamente em rotas que contêm /telemetry
        if (requestUri.contains("/telemetry")) {
            String requestApiKey = request.getHeader("X-API-Key");

            if (requestApiKey == null || !requestApiKey.equals(validApiKey)) {
                System.out.println("BLOQUEADO: API Key inválida ou ausente!");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: Invalid or missing API Key");
                return;
            }
            System.out.println("AUTORIZADO: API Key válida!");
        }

        filterChain.doFilter(request, response);
    }
}