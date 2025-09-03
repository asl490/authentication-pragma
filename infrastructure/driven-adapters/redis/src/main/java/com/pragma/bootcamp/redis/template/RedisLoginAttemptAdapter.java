package com.pragma.bootcamp.redis.template;

import com.pragma.bootcamp.model.gateways.LoginAttemptGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

// En infrastructure/driven-adapters/redis
@Repository
public class RedisLoginAttemptAdapter implements LoginAttemptGateway {

    private static final String ATTEMPT_KEY_PREFIX = "login_attempts:";

    private static final String BLOCK_KEY_PREFIX = "user_blocked:";

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    @Value("${security.login.max-attempts:3}")
    private int maxAttempts;
    @Value("${security.login.block-duration-minutes:15}")
    private long blockDurationMinutes;

    public RedisLoginAttemptAdapter(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Boolean> isUserBlocked(String username) {
        String blockKey = BLOCK_KEY_PREFIX + username;
        return redisTemplate.hasKey(blockKey);
    }

    @Override
    public Mono<Void> recordFailedAttempt(String username) {
        String attemptKey = ATTEMPT_KEY_PREFIX + username;

        return redisTemplate.opsForValue()
                .increment(attemptKey)
                .flatMap(attempts -> {
                    if (attempts == 1) {
                        return redisTemplate.expire(attemptKey,
                                        Duration.ofMinutes(blockDurationMinutes))
                                .then();
                    }
                    if (attempts >= maxAttempts) {
                        return blockUser(username);
                    }
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Void> clearAttempts(String username) {
        String attemptKey = ATTEMPT_KEY_PREFIX + username;
        return redisTemplate.delete(attemptKey).then();
    }

    @Override
    public Mono<Integer> getAttemptCount(String username) {
        String attemptKey = ATTEMPT_KEY_PREFIX + username;
        return redisTemplate.opsForValue()
                .get(attemptKey)
                .map(Integer::parseInt)
                .defaultIfEmpty(0);
    }

    private Mono<Void> blockUser(String username) {
        String blockKey = BLOCK_KEY_PREFIX + username;
        return redisTemplate.opsForValue()
                .set(blockKey, String.valueOf(System.currentTimeMillis()))
                .then(redisTemplate.expire(blockKey, Duration.ofMinutes(blockDurationMinutes)))
                .then();
    }
}