package com.nguyen.weekend.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
/**
 * Represents the full product response from Retailed's Zalando scraper API.
 */
@Data
@Builder
public class ZalandoProduct {
    private String sku;
    private String name;
    private String brand;
    private String color;
    private List<String> images;
    private List<Variant> variants;
    private String description;
    private String manufacturer;


    /**
     * Get the price for our watched variant (or the first one if none).
     * Returns price in minor units (e.g. cents), so you may need to adjust.
     */
    public BigDecimal getPriceFor(String variantSku) {
        Variant v = variants.stream()
                .filter(var -> variantSku != null && variantSku.equals(var.getSku()))
                .findFirst()
                .orElse(variants.getFirst());
        // convert minor units → major (divide by 100)
        return BigDecimal.valueOf(v.getPrice())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
