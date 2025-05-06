package com.nguyen.weekend;

import com.nguyen.weekend.integration.RetailedClient;
import com.nguyen.weekend.model.Variant;
import com.nguyen.weekend.model.ZalandoProduct;
import com.nguyen.weekend.repository.PriceHistory;
import com.nguyen.weekend.repository.PriceHistoryRepository;
import com.nguyen.weekend.repository.WatchItem;
import com.nguyen.weekend.repository.WatchItemRepository;
import com.nguyen.weekend.service.NotificationService;
import com.nguyen.weekend.service.ZalandoPriceCheckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class ZalandoPriceCheckServiceTest {

    private WatchItemRepository watchRepo;
    private PriceHistoryRepository historyRepo;
    private RetailedClient retailedClient;
    private NotificationService notifier;
    private ZalandoPriceCheckService service;

    @BeforeEach
    void setup() {
        watchRepo = mock(WatchItemRepository.class);
        historyRepo = mock(PriceHistoryRepository.class);
        retailedClient = mock(RetailedClient.class);
        notifier = mock(NotificationService.class);
        service = new ZalandoPriceCheckService(watchRepo, historyRepo, notifier, retailedClient);
    }

    @Test
    void testCheckAll_savesAndAlertsOnDrop() {
        WatchItem item = new WatchItem("http://example.com/p1", null, 10.0);
        item.setId(1L);
        when(watchRepo.findAll()).thenReturn(List.of(item));

        // Mock two price points: old 100.00, new 85.00 (15% drop)
        ZalandoProduct product = ZalandoProduct
                .builder()
                .brand("adidos")
                .sku("sku")
                .name("cap")
                .images(Collections.emptyList())
                .variants(
                        Collections.singletonList(Variant.builder()
                                .sku("sku")
                                .price(8500)
                                .priceCurrency("EUR").build())
                )
                .build();
        // First call returns current, stub retries sequence
        when(retailedClient.fetchProduct(item.getProductUrl()))
                .thenReturn(Mono.just(product));
        // Historical record: previous price 100.00
        PriceHistory prev = new PriceHistory(item, "name", LocalDateTime.now().minusDays(1), new BigDecimal("100.00"));
        when(historyRepo.save(any())).thenReturn(null);
        when(historyRepo.findTop2ByWatchItemOrderByCheckedAtDesc(item))
                .thenReturn(List.of(
                        new PriceHistory(item, "name", LocalDateTime.now(), new BigDecimal("85.00")),
                        prev
                ));

        service.checkAll();

        // Verify save called for new history
        ArgumentCaptor<PriceHistory> captor = ArgumentCaptor.forClass(PriceHistory.class);
        verify(historyRepo, times(1)).save(captor.capture());
        PriceHistory saved = captor.getValue();
        assertEquals(item, saved.getWatchItem());
        assertEquals(new BigDecimal("85.00"), saved.getPrice());

        // Verify alert sent
        verify(notifier, times(1)).sendPriceDropAlert(anyString(), eq(100.0), eq(85.0), eq(15.0));
    }
}

