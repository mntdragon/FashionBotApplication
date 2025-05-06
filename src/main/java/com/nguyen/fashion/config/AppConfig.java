package com.nguyen.fashion.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class AppConfig {
    private final ApplicationProperties props;

    public AppConfig(ApplicationProperties props) {
        this.props = props;
    }

    @Bean
    public WebClient retailedWebClient() {
        return WebClient.builder()
                .baseUrl(props.getRetailed().getZalandoUrl())
                .defaultHeader("x-api-key", props.getRetailed().getApiKey())
                .build();
    }

    @Bean
    public CircuitBreaker retailedCircuitBreaker() {
        ApplicationProperties.Retailed.CircuitBreaker cb =
                props.getRetailed().getCircuitbreaker();
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(cb.getFailureRateThreshold())
                .waitDurationInOpenState(cb.getWaitDurationInOpenState())
                .slowCallRateThreshold(cb.getSlowCallRateThreshold())
                .slowCallDurationThreshold(cb.getSlowCallDurationThreshold())
                .permittedNumberOfCallsInHalfOpenState(cb.getPermittedNumberOfCallsInHalfOpenState())
                .slidingWindowSize(cb.getSlidingWindowSize())
                .build();
        return CircuitBreakerRegistry.of(config)
                .circuitBreaker("retailedClientCircuitBreaker");
    }
}
