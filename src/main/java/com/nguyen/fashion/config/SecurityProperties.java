package com.nguyen.fashion.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "security.oauth")
@Data
@Component
public class SecurityProperties {
    /**
     * Base64-encoded symmetric key for JWT signing.
     * Must decode to at least 256 bits.
     */
    private String jwtSecret;
}
