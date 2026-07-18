package dev.gustavo.math.exception;

public class RateLimitExceededException extends RuntimeException {

    private final long limit;
    private final long retryAfterSeconds;

    public RateLimitExceededException(String message, long limit, long retryAfterSeconds) {
        super(message);
        this.limit = limit;
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getLimit() {
        return limit;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
