package com.auth.shopping_list.config;

import com.auth.shopping_list.client.RateClient;
import com.auth.shopping_list.service.CurrencyRateService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupInitializer {
    private final RateClient rateClient;
    private final CurrencyRateService currencyRateService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("✅ Проверяем кэш...");
        currencyRateService.createCurrencyRate("EUR");
        log.info(currencyRateService.getCurrencyRate("EUR").toString());
        var eur = currencyRateService.getCurrencyRate("EUR");
        currencyRateService.updateCurrencyRate(eur, new BigDecimal("100"));
        log.info(currencyRateService.getCurrencyRate("EUR").toString());
    }
}
