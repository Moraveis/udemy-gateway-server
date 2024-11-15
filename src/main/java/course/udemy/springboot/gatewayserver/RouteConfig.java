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
                                        f.rewritePath("/course/accounts/(?<segment>.*)", "/${segment}")
                                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                                .uri("lb://ACCOUNTS"))
                .route(p ->
                        p.path("/course/loans/**")
                                .filters(f -> f.rewritePath("/course/loans/(?<segment>.*)", "/${segment}")
                                        .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                                .uri("lb://LOANS"))
                .route(p ->
                        p.path("/course/cards/**")
                                .filters(f -> f.rewritePath("/course/cards/(?<segment>.*)", "/${segment}")
                                        .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                                .uri("lb://CARDS"))
                .build();
    }
}
