package dev.gustavo.math.infra.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.gustavo.math.exception.RateLimitExceededException;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SubmissionRateLimiter {

    private static final long SUBMISSION_CAPACITY = 30;
    private static final Duration SUBMISSION_REFILL_PERIOD = Duration.ofMinutes(1);

    private final Cache<UUID, Bucket> submissionBuckets = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(Duration.ofHours(1))
            .build();

    public void checkSubmissionAllowed(UUID userId) {
        Bucket bucket = submissionBuckets.get(userId, key -> newSubmissionBucket());
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed())
            return;

        long retryAfterSeconds = Math.max(1, TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));
        log.warn("Submission rate limit exceeded: userId={} retryAfterSeconds={}", userId, retryAfterSeconds);

        throw new RateLimitExceededException("Too many submission attempts", SUBMISSION_CAPACITY, retryAfterSeconds);
    }

    private Bucket newSubmissionBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(SUBMISSION_CAPACITY)
                        .refillGreedy(SUBMISSION_CAPACITY, SUBMISSION_REFILL_PERIOD))
                .build();
    }
}
