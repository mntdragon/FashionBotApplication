package com.nguyen.fashion.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.nguyen.fashion.model.ZalandoProduct;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RetailedClient {
    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;

    /**
     * Fetches the full product details from the Retailed API.
     */
    public Mono<ZalandoProduct> fetchProduct(String productUrl) {
        return webClient.get()
                .uri(uri -> uri.path("/product")
                        .queryParam("query", productUrl)
                        .build())
                .retrieve()
                .bodyToMono(ZalandoProduct.class)
                .timeout(Duration.ofSeconds(5))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    /**
     * Fetches the price in EUR for the tracked variant (or default).
     */
    public Mono<Double> fetchPrice(String productUrl, String variantSku) {
        return fetchProduct(productUrl)
                .map(product -> {
                    var variants = product.getVariants();
                    var selected = variants.stream()
                            .filter(v -> variantSku != null && variantSku.equals(v.getSku()))
                            .findFirst()
                            .orElse(variants.getFirst());
                    // price is in minor units (cents), convert to euros
                    return selected.getPrice() / 100.0;
                });
    }
}
