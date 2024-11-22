package course.udemy.springboot.gatewayserver;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                                .filters(f -> f.rewritePath("/course/loans/(?<segment>.*)", "/api/loans/${segment}")
                                        .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                                .uri("lb://LOANS"))
                .route(p ->
                        p.path("/course/cards/**")
                                .filters(f -> f.rewritePath("/course/cards/(?<segment>.*)", "/api/cards/${segment}")
                                        .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                                .uri("lb://CARDS"))
                .build();
    }
}
