package com.auth.shopping_list.service;

import com.auth.shopping_list.client.RateClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyRateService {
    private final RateClient rateClient;

    @Cacheable(value = "rates", key = "#currency")
    public BigDecimal getRate (String currency){
        log.info("Запрос курса у клиента");
        return rateClient.fetchRate(currency);
    }
}
