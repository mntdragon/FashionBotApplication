package com.nguyen.weekend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * POJO for price check trigger response.
 */
@Data
@AllArgsConstructor
public class PriceCheckResponse {
    private String productName;
    private double price;
}
