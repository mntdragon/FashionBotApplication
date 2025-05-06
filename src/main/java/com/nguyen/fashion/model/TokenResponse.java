package com.nguyen.fashion.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * POJO for token issuance response.
 */
@Data
@AllArgsConstructor
public class TokenResponse {
    private String token;
}
