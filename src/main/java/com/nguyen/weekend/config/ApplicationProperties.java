package com.nguyen.weekend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;


@ConfigurationProperties(prefix = "fashion.bot")
@Data
@Component
public class ApplicationProperties {
    @NestedConfigurationProperty
    private Retailed retailed;

    @NestedConfigurationProperty
    private MailProperties mail;

    @NestedConfigurationProperty
    private DatasourceProperties datasource;


    @Data
    public static class Retailed {
        private String apiKey;
        private String zalandoUrl;
        @NestedConfigurationProperty
        private CircuitBreaker circuitbreaker;

        @Data
        public static class CircuitBreaker {
            private float failureRateThreshold;
            private Duration waitDurationInOpenState;
            private float slowCallRateThreshold;
            private Duration slowCallDurationThreshold;
            private int permittedNumberOfCallsInHalfOpenState;
            private int slidingWindowSize;
        }
    }
    @Data
    public static class DatasourceProperties {
        private String url;
        private String username;
        private String password;
    }
}
