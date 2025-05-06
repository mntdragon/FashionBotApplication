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
import smile.timeseries.ARMA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Scheduled(cron = "0 0 * * * *")
    public void checkAll() {
        List<WatchItem> items = watchItemRepository.findAll();
        for (WatchItem item : items) {
            retailedClient.fetchProduct(item.getProductUrl())
                    .retryWhen(Retry.fixedDelay(MAX_FETCH_RETRIES, RETRY_DELAY))
                    .subscribe(product -> {
                        BigDecimal currPrice = BigDecimal.valueOf(
                                product.getVariants().getFirst().getPrice(), 2
                        );
                        String productName = product.getName();
                        LocalDateTime now = LocalDateTime.now();

                        // Save new history entry
                        PriceHistory entry = new PriceHistory(item, productName, now, currPrice);
                        priceHistoryRepository.save(entry);

                        // Retrieve recent history for anomaly detection
                        List<PriceHistory> history = priceHistoryRepository
                                .findByWatchItemOrderByCheckedAtDesc(item)
                                .stream().limit(30).collect(Collectors.toList());

                        if (history.size() >= 10) {
                            // Extract prices for ARIMA forecasting
                            double[] series = history.stream()
                                    .map(ph -> ph.getPrice().doubleValue())
                                    .mapToDouble(Double::doubleValue)
                                    .toArray();
                            // Fit ARIMA(p,d,q) model: simple example (1,1,1)
                            ARMA model = ARMA.fit(series, 1, 1);
                            double forecast = model.forecast(1)[0];
                            double threshold = forecast * 1.10; // 10% above forecast

                            // If current price is anomalous (e.g. > threshold)
                            if (currPrice.doubleValue() > threshold) {
                                String msg = String.format(
                                        "Anomalous price for %s: %.2f exceeds forecast %.2f",
                                        productName, currPrice, forecast
                                );
                                log.warn(msg);
                                notificationService.sendErrorAlert(msg);
                            }
                        }

                        // Check conventional drop > threshold
                        List<PriceHistory> recentTwo = priceHistoryRepository
                                .findTop2ByWatchItemOrderByCheckedAtDesc(item);
                        if (recentTwo.size() == 2) {
                            BigDecimal prevPrice = recentTwo.get(1).getPrice();
                            double dropPct = prevPrice.subtract(currPrice)
                                    .divide(prevPrice, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100))
                                    .doubleValue();
                            if (dropPct >= item.getThresholdPct()) {
                                // Email notification
                                notificationService.sendPriceDropAlert(
                                        item.getProductUrl(),
                                        prevPrice.doubleValue(),
                                        currPrice.doubleValue(),
                                        dropPct
                                );
                            }
                        }
                    }, err -> {
                        log.warn("Fetch failed for {}: {}", item.getProductUrl(), err.getMessage());
                        notificationService.sendErrorAlert(
                                String.format("Fetch failed for %s after %d retries: %s",
                                        item.getProductUrl(), MAX_FETCH_RETRIES, err.getMessage())
                        );
                    });
        }
    }
}
