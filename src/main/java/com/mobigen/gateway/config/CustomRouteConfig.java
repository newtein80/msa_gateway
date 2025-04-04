package com.mobigen.gateway.config;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import reactor.core.publisher.Mono;

@Configuration
public class CustomRouteConfig {
    @Bean
    public RouteLocator mobigenRouteConfig(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                // .route(p -> p
                //         .path("/mobigen/accounts/**")
                //         .filters(f -> f.rewritePath("/mobigen/accounts/(?<segment>.*)", "/${segment}")
                //                 .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                //                 .circuitBreaker(config -> config.setName("accountsCircuitBreaker")
                //                         .setFallbackUri("forward:/contactSupport")))
                //         .uri("lb://ACCOUNTS"))
                // .route(p -> p
                //         .path("/mobigen/loans/**")
                //         .filters(f -> f.rewritePath("/mobigen/loans/(?<segment>.*)", "/${segment}")
                //                 .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                //                 .retry(retryConfig -> retryConfig.setRetries(3)
                //                         .setMethods(HttpMethod.GET)
                //                         .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true)))
                //         .uri("lb://LOANS"))
                .route(p -> p
                        .path("/mobigen/cards/**")
                        .filters(f -> f.rewritePath("/mobigen/cards/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver())))
                        .uri("lb://CARDS"))
                .build();

                // TEST tool: brew install httpd
                // TEST cmd: ab -n 10 -c 2 -v 3 http://localhost:9000/mobigen/cards/api/check/build-info
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(10))
                        .build())
                .build());
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 1, 1);
    }

    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user"))
                .defaultIfEmpty("anonymous");
    }
}
