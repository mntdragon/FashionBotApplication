package com.nguyen.weekend.controller;


import com.nguyen.weekend.model.PriceCheckResponse;
import com.nguyen.weekend.model.TokenResponse;
import com.nguyen.weekend.service.TokenService;
import com.nguyen.weekend.service.ZalandoPriceCheckService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class PriceCheckController {

    private final ZalandoPriceCheckService priceCheckService;
    private final TokenService tokenService;

    public PriceCheckController(ZalandoPriceCheckService priceCheckService, TokenService tokenService) {
        this.priceCheckService = priceCheckService;
        this.tokenService = tokenService;
    }

    /**
     * Trigger a manual price check for all watch items.
     * Requires authentication.
     */
    @GetMapping("/price-check")
    public Flux<PriceCheckResponse> checkAllPrices(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Flux.error(new RuntimeException("Missing or invalid Authorization header"));
        }
        String token = authHeader.substring(7);
        if (!tokenService.validateToken(token)) {
            return Flux.error(new RuntimeException("Invalid or expired token"));
        }
        priceCheckService.checkAll();
        // Fetch all watch items from DB and map to responses
        return priceCheckService.getAllWatchItems();
    }

    @PostMapping("/token")
    public Mono<TokenResponse> getToken() {
        return Mono.just(new TokenResponse(tokenService.generateToken()));
    }
}
