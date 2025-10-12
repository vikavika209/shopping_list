package com.auth.shopping_list.service;

import com.auth.shopping_list.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateRefreshScheduler {
    private final CurrencyRateService currencyRateService;
    private final CurrencyRepository currencyRepository;

    @Scheduled(cron = "0 0 * * * *", zone = "Europe/Warsaw")
    public void refreshAll() {
        Set<String> currencies = currencyRepository.findAllNames();
        currencies.forEach(currencyRateService::updateRate);
    }
}
