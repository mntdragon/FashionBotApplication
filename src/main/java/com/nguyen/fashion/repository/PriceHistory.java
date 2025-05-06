package com.nguyen.fashion.repository;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a historical price check record for a WatchItem.
 */

@Entity
@Table(name = "price_history")
@Getter
@Setter
@NoArgsConstructor
public class PriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watch_item_id", nullable = false)
    private WatchItem watchItem;

    @Column(name = "checked_at", nullable = false)
    private LocalDateTime checkedAt;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    @Column(name = "product_name", nullable = false, length = 1024)
    private String productName;

    public PriceHistory(WatchItem item, String name, LocalDateTime now, BigDecimal price) {
        this.watchItem = item;
        this.productName = name;
        this.checkedAt = now;
        this.price = price;
    }
}
