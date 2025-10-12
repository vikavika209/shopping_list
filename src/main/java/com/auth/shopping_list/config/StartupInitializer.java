package com.auth.shopping_list.config;

import com.auth.shopping_list.client.RateClient;
import com.auth.shopping_list.dto.CurrencyDTO;
import com.auth.shopping_list.entity.Currency;
import com.auth.shopping_list.repository.CurrencyRepository;
import com.auth.shopping_list.service.CurrencyService;
import com.auth.shopping_list.service.RateRefreshScheduler;
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

    private final CurrencyService service;
    private final CurrencyRepository repo;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("Запуск приложения");
        CurrencyDTO currencyEur = new CurrencyDTO("EUR", BigDecimal.valueOf(10), "No_comment");
        service.save(currencyEur);
        log.info("Доступно валют: {}", repo.findAllNames().size());

    }
}
