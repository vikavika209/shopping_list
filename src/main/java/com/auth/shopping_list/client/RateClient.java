package com.auth.shopping_list.client;

import com.auth.shopping_list.exception.NoThisRateException;
import com.auth.shopping_list.service.RateRefreshScheduler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateClient {

    @Value("${rates.api.base}")
    private String baseCurrency;

    @Value("${rates.api.url}")
    private String apiUrl;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        log.info("Запрос к API: {}", apiUrl);
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

    public record LatestRatesResponse(String base,
                                      String date,
                                      Map<String, BigDecimal> rates) {}

    public BigDecimal fetchRate(String symbol) {
        LatestRatesResponse resp = webClient.get()
                .uri(uri -> uri
                        .path("/latest")
                        .queryParam("base", baseCurrency)
                        .queryParam("symbols", symbol)
                        .build())
                .retrieve()
                .bodyToMono(LatestRatesResponse.class)
                .block();

        BigDecimal rate = (resp != null && resp.rates() != null)
                ? resp.rates().get(symbol)
                : null;

        if (rate == null) {
            throw new NoThisRateException("Нет курса для %s/%s".formatted(baseCurrency, symbol));
        }
        log.info("Курс {} к {} = {} (дата курса: {})", symbol, baseCurrency, rate, resp.date);
        return rate;
    }
}
