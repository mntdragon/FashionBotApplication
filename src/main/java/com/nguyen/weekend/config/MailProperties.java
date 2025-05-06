package com.nguyen.weekend.config;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
public class MailProperties {
    private String host;
    private int port;
    private String username;
    private String password;

    @NestedConfigurationProperty
    private Notification notification;

    @Data
    public static class Notification {
        /**
         * The address to send all alerts to.
         */
        private String sendToEmail;
    }
}
