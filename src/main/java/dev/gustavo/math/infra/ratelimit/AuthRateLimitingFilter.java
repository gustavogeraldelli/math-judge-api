package dev.gustavo.math.infra.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AuthRateLimitingFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/api/v1/auth/login";
    private static final long LOGIN_CAPACITY = 5;
    private static final Duration LOGIN_REFILL_PERIOD = Duration.ofMinutes(1);

    private final Cache<String, Bucket> loginBuckets = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(Duration.ofHours(1))
            .build();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!isLoginRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = request.getRemoteAddr();
        Bucket bucket = loginBuckets.get(clientIp, key -> newLoginBucket());
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.setHeader("X-Rate-Limit-Limit", Long.toString(LOGIN_CAPACITY));
            response.setHeader("X-Rate-Limit-Remaining", Long.toString(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
            return;
        }

        long retryAfterSeconds = Math.max(1, TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));
        log.warn("Login rate limit exceeded: path={} remoteAddress={} retryAfterSeconds={}",
                request.getRequestURI(), clientIp, retryAfterSeconds);

        response.setStatus(429);
        response.setHeader("X-Rate-Limit-Limit", Long.toString(LOGIN_CAPACITY));
        response.setHeader("X-Rate-Limit-Remaining", "0");
        response.setHeader(HttpHeaders.RETRY_AFTER, Long.toString(retryAfterSeconds));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"Too many login attempts\"}");
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return HttpMethod.POST.matches(request.getMethod()) && LOGIN_PATH.equals(request.getRequestURI());
    }

    private Bucket newLoginBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(LOGIN_CAPACITY).refillGreedy(LOGIN_CAPACITY, LOGIN_REFILL_PERIOD))
                .build();
    }
}
