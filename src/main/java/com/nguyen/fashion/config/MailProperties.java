package com.nguyen.fashion.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import javax.sound.midi.Receiver;

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
