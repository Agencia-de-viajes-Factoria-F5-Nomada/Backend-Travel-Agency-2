package com.inditex.g1_agencia_viajes.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MINUTES = 15;

    private final ConcurrentHashMap<String, AtomicInteger> attempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Instant> windowStarts = new ConcurrentHashMap<>();

    public boolean isBlocked(String ip) {
        Instant windowStart = windowStarts.get(ip);
        if (windowStart == null) {
            return false;
        }

        long elapsedMinutes = java.time.Duration.between(windowStart, Instant.now()).toMinutes();
        if (elapsedMinutes >= WINDOW_MINUTES) {
            resetAttempts(ip);
            return false;
        }

        AtomicInteger count = attempts.get(ip);
        return count != null && count.get() >= MAX_ATTEMPTS;
    }

    public void recordFailedAttempt(String ip) {
        Instant now = Instant.now();
        windowStarts.putIfAbsent(ip, now);

        Instant windowStart = windowStarts.get(ip);
        long elapsedMinutes = java.time.Duration.between(windowStart, Instant.now()).toMinutes();
        if (elapsedMinutes >= WINDOW_MINUTES) {
            windowStarts.put(ip, now);
            attempts.put(ip, new AtomicInteger(1));
            return;
        }

        attempts.computeIfAbsent(ip, k -> new AtomicInteger(0)).incrementAndGet();
    }

    public void resetAttempts(String ip) {
        attempts.remove(ip);
        windowStarts.remove(ip);
    }

    public long getRemainingBlockSeconds(String ip) {
        Instant windowStart = windowStarts.get(ip);
        if (windowStart == null) {
            return 0;
        }
        long elapsedSeconds = java.time.Duration.between(windowStart, Instant.now()).getSeconds();
        long windowSeconds = WINDOW_MINUTES * 60;
        return Math.max(0, windowSeconds - elapsedSeconds);
    }
}
