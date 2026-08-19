package com.hotel.hotelservice.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.hotelservice.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
@Order(1)
public class ApiKeyFilter extends OncePerRequestFilter {

    public static final String API_KEY_HEADER = "X-API-KEY";

    private static final Logger log = LoggerFactory.getLogger(ApiKeyFilter.class);

    private static final Set<String> BYPASS_PATHS = Set.of(
            "/v3/api-docs",
            "/swagger-ui.html",
            "/swagger-ui",
            "/actuator",
            "/h2-console",
            "/error",
            "/favicon.ico"
    );

    private final String expectedApiKey;
    private final ObjectMapper objectMapper;

    public ApiKeyFilter(@Value("${app.api-key}") String expectedApiKey, ObjectMapper objectMapper) {
        this.expectedApiKey = expectedApiKey;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isBlank() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        return isSwaggerOrDocsPath(path)
                || isActuatorPath(path)
                || isH2ConsolePath(path)
                || isErrorPath(path)
                || HttpMethod.OPTIONS.matches(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String providedKey = request.getHeader(API_KEY_HEADER);

        if (providedKey == null || !providedKey.equals(expectedApiKey)) {
            log.warn("Rejected request to {} with missing or invalid API key", request.getRequestURI());
            writeUnauthorized(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ErrorResponse body = ErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "Missing or invalid X-API-KEY header"
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private boolean isSwaggerOrDocsPath(String path) {
        return path.equals("/v3/api-docs")
                || path.startsWith("/v3/api-docs/")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/swagger-ui/");
    }

    private boolean isActuatorPath(String path) {
        return path.equals("/actuator") || path.startsWith("/actuator/");
    }

    private boolean isH2ConsolePath(String path) {
        return path.equals("/h2-console") || path.startsWith("/h2-console/");
    }

    private boolean isErrorPath(String path) {
        return path.equals("/error") || path.equals("/favicon.ico");
    }
}
