package course.udemy.springboot.gatewayserver;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.time.Duration;
import java.time.LocalDateTime;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator courseRouterConfig(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route(p ->
                        p.path("/course/accounts/**")
                                .filters(f ->
                                        f.rewritePath("/course/accounts/(?<segment>.*)", "/api/accounts/${segment}")
                                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                                .circuitBreaker(config -> config.setName("accountsCircuitBreaker")
                                                        .setFallbackUri("forward:/contact-support")))
                                .uri("lb://ACCOUNTS"))
                .route(p ->
                        p.path("/course/customers/**")
                                .filters(f ->
                                        f.rewritePath("/course/customers/(?<segment>.*)", "/api/customers/${segment}")
                                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                                .circuitBreaker(config -> config.setName("costumersCircuitBreaker")
                                                        .setFallbackUri("forward:/contact-support")))
                                .uri("lb://ACCOUNTS"))
                .route(p ->
                        p.path("/course/loans/**")
                                .filters(f ->
                                        f.rewritePath("/course/loans/(?<segment>.*)", "/api/loans/${segment}")
                                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                                .retry(retryConfig -> retryConfig.setRetries(3)
                                                        .setMethods(HttpMethod.GET)
                                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true)))
                                .uri("lb://LOANS"))
                .route(p ->
                        p.path("/course/cards/**")
                                .filters(f -> f.rewritePath("/course/cards/(?<segment>.*)", "/api/cards/${segment}")
                                        .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                                .uri("lb://CARDS"))
                .build();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build()).build());
    }
}
