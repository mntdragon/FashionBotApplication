package com.nguyen.weekend.repository;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "watch_list")
public class WatchItem {
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The Zalando product page URL to monitor.
     */
    @Column(name = "product_url", nullable = false, length = 1024)
    private String productUrl;

    /**
     * Optional specific variant SKU to track (else null).
     */
    @Column(name = "variant_sku")
    private String variantSku;

    /**
     * Percentage drop threshold (e.g. 10.0 for 10%).
     */
    @Column(name = "threshold_pct", nullable = false)
    private double thresholdPct;

    public WatchItem(String productUrl, String variantSku, double thresholdPct) {
        this.productUrl   = productUrl;
        this.variantSku   = variantSku;
        this.thresholdPct = thresholdPct;
    }

}
