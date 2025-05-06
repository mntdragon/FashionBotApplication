package com.nguyen.weekend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;

@Configuration
@ConfigurationProperties(prefix = "proxy")
public class ProxyProperties {

    private String host;
    private int port;
    private String user;
    private String pass;

    /**
     * Fixed User-Agent string representing Chrome on Windows 11.
     */
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/117.0.5938.132 Safari/537.36";

    /**
     * Returns an HTTP proxy configured from application properties.
     */
    public Proxy getProxy() {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
    }

    /**
     * Encodes proxy credentials for the "Proxy-Authorization" header.
     */
    public String getEncodedProxyAuth() {
        String creds = user + ":" + pass;
        return "Basic " + Base64.getEncoder().encodeToString(creds.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Returns the fixed Chrome-on-Windows-11 User-Agent string.
     */
    public String getUserAgent() {
        return USER_AGENT;
    }
}
