package com.auth.shopping_list.config;

import com.auth.shopping_list.client.RateClient;
import com.auth.shopping_list.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupInitializer {
    private final RateClient rateClient;
    private final CurrencyService currencyService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("Запуск приложения");
        log.info("________________________________________________________");
        log.info("Получен курс: {}", currencyService.getRate("EUR"));
        log.info("________________________________________________________");
        log.info("Получен курс: {}", currencyService.getRate("EUR"));
    }
}
