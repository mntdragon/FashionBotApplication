package com.nguyen.weekend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Variant {
     String sku;
     int price;
    @JsonProperty("price_currency")
     String priceCurrency;
}
