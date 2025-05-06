package com.nguyen.fashion.service;


import com.nguyen.fashion.integration.RetailedClient;
import com.nguyen.fashion.model.PriceCheckResponse;
import com.nguyen.fashion.repository.PriceHistory;
import com.nguyen.fashion.repository.PriceHistoryRepository;
import com.nguyen.fashion.repository.WatchItem;
import com.nguyen.fashion.repository.WatchItemRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ZalandoPriceCheckService {

    private final WatchItemRepository watchItemRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final NotificationService notificationService;
    private final RetailedClient retailedClient;
    /**
     * How many times to retry before giving up
     */
    private static final int MAX_FETCH_RETRIES = 3;
    /**
     * Delay between retries
     */
    private static final Duration RETRY_DELAY = Duration.ofSeconds(5);

    /**
     * Return all watch items—used by the controller to drive fetches.
     */
    public Flux<PriceCheckResponse> getAllWatchItems() {
        return watchItemRepository.findAll()
                .stream()
                .map(item -> retailedClient.fetchProduct(item.getProductUrl())
                        .map(prod -> new PriceCheckResponse(
                                prod.getName(),
                                prod.getVariants().getFirst().getPrice() / 100.0
                        ))
                )
                .map(Mono::flux)
                .reduce(Flux.empty(), Flux::merge);
    }

    @Scheduled(cron = "0 0 * * * *") // every hour
    public void checkAll() {
        List<WatchItem> items = watchItemRepository.findAll();
        items.forEach(item ->
                retailedClient.fetchProduct(item.getProductUrl())
                        .retryWhen(Retry.fixedDelay(MAX_FETCH_RETRIES, RETRY_DELAY))
                        .subscribe(product -> {
                            // Convert minor units to BigDecimal euros
                            BigDecimal price = BigDecimal.valueOf(
                                    product.getVariants().getFirst().getPrice(), 2
                            );

                            // persist the new price check
                            PriceHistory entry = new PriceHistory(
                                    item,
                                    product.getName(),
                                    LocalDateTime.now(),
                                    price
                            );
                            priceHistoryRepository.save(entry);
                        }, err -> {
                            log.warn(String.format(
                                    "Product %s threw %s: %s",
                                    item.getProductUrl(),
                                    err.getClass().getSimpleName(),
                                    err.getMessage()));
                            notificationService.sendErrorAlert(String.format(
                                    "Failed to fetch price for %s after %d attempts: %s",
                                    item.getProductUrl(), MAX_FETCH_RETRIES, err.getMessage()));
                        })
        );
    }
}
