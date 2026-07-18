package dev.gustavo.math.infra.ratelimit;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AuthRateLimitingFilterTest {

    @Test
    @DisplayName("Should allow login requests within rate limit")
    void shouldAllowLoginRequestsWithinRateLimit() throws Exception {
        var filter = new AuthRateLimitingFilter();
        FilterChain filterChain = mock(FilterChain.class);

        for (int i = 0; i < 5; i++) {
            var request = loginRequest("192.168.0.10");
            var response = new MockHttpServletResponse();

            filter.doFilter(request, response, filterChain);

            assertEquals(200, response.getStatus());
        }

        verify(filterChain, times(5)).doFilter(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Should reject login requests when rate limit is exceeded")
    void shouldRejectLoginRequestsWhenRateLimitIsExceeded() throws Exception {
        var filter = new AuthRateLimitingFilter();
        FilterChain filterChain = mock(FilterChain.class);

        for (int i = 0; i < 5; i++)
            filter.doFilter(loginRequest("192.168.0.20"), new MockHttpServletResponse(), filterChain);

        var rejectedResponse = new MockHttpServletResponse();
        filter.doFilter(loginRequest("192.168.0.20"), rejectedResponse, filterChain);

        assertEquals(429, rejectedResponse.getStatus());
        assertEquals("0", rejectedResponse.getHeader("X-Rate-Limit-Remaining"));
        assertEquals("{\"error\":\"Too many login attempts\"}", rejectedResponse.getContentAsString());
        verify(filterChain, times(5)).doFilter(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Should ignore non-login requests")
    void shouldIgnoreNonLoginRequests() throws Exception {
        var filter = new AuthRateLimitingFilter();
        FilterChain filterChain = mock(FilterChain.class);
        var request = new MockHttpServletRequest("POST", "/api/v1/auth/register");
        var response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertEquals(200, response.getStatus());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    private MockHttpServletRequest loginRequest(String remoteAddress) {
        var request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRemoteAddr(remoteAddress);
        return request;
    }
}
