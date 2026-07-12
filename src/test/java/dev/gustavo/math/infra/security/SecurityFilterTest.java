package dev.gustavo.math.infra.security;

import dev.gustavo.math.exception.TokenDecodingException;
import dev.gustavo.math.service.auth.AccessTokenService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SecurityFilterTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should authenticate request when bearer access token is valid")
    void doFilterShouldAuthenticateRequestWhenAccessTokenIsValid() throws Exception {
        AccessTokenService accessTokenService = mock(AccessTokenService.class);
        SecurityFilter securityFilter = new SecurityFilter(accessTokenService);
        FilterChain filterChain = mock(FilterChain.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        UUID userId = UUID.randomUUID();

        request.addHeader("Authorization", "Bearer valid-access-token");
        when(accessTokenService.validate("valid-access-token")).thenReturn(true);
        when(accessTokenService.getUserId("valid-access-token")).thenReturn(userId);
        when(accessTokenService.getUserRole("valid-access-token")).thenReturn("ROLE_USER");

        securityFilter.doFilter(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals(userId, authentication.getPrincipal());
        assertEquals("ROLE_USER", authentication.getAuthorities().iterator().next().getAuthority());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should return unauthorized when bearer access token is invalid")
    void doFilterShouldReturnUnauthorizedWhenAccessTokenIsInvalid() throws Exception {
        AccessTokenService accessTokenService = mock(AccessTokenService.class);
        SecurityFilter securityFilter = new SecurityFilter(accessTokenService);
        FilterChain filterChain = mock(FilterChain.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer invalid-access-token");
        when(accessTokenService.validate("invalid-access-token")).thenThrow(new TokenDecodingException("failed to decode"));

        securityFilter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Should continue filter chain when authorization header is absent")
    void doFilterShouldContinueWhenAuthorizationHeaderIsAbsent() throws Exception {
        AccessTokenService accessTokenService = mock(AccessTokenService.class);
        SecurityFilter securityFilter = new SecurityFilter(accessTokenService);
        FilterChain filterChain = mock(FilterChain.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        securityFilter.doFilter(request, response, filterChain);

        assertSame(null, SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
