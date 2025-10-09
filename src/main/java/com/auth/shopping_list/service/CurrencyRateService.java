package com.auth.shopping_list.service;

import com.auth.shopping_list.client.RateClient;
import com.auth.shopping_list.entity.CurrencyRate;
import com.auth.shopping_list.exception.NoThisRateException;
import com.auth.shopping_list.repository.CurrencyRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CurrencyRateService {
    private final CurrencyRateRepository currencyRateRepository;
    private final RateClient rateClient;

    @CachePut(value = "rates", key = "#result.name")
    public CurrencyRate createCurrencyRate(String name) {
        log.info("Создание курса для {}", name);
        BigDecimal rate = rateClient.fetchRate(name);

        var existing = currencyRateRepository.findByName(name);
        if (existing.isPresent()) {
            var e = existing.get();
            e.setRate(rate);
            e.setDate(LocalDate.now().toString());
            return currencyRateRepository.save(e);
        }

        var entity = new CurrencyRate();
        entity.setName(name);
        entity.setRate(rate);
        entity.setDate(LocalDate.now().toString());
        return currencyRateRepository.save(entity);
    }

    @Cacheable(value = "rates", key = "#name")
    public CurrencyRate getCurrencyRate(String name) {
        log.info("Поиск курса для {} в базе данных", name);
        return currencyRateRepository.findByName(name).orElse(null);
    }

    @CachePut(value = "rates", key = "#result.name")
    public CurrencyRate updateCurrencyRate(CurrencyRate currencyRate, BigDecimal newRate) {
        log.info("Обновление курса для {}", currencyRate.getName());
        currencyRate.setRate(newRate);
        currencyRate.setDate(LocalDate.now().toString());
        return currencyRateRepository.save(currencyRate);
    }

    public List<CurrencyRate> allCurrencyRate(){
        List<CurrencyRate> all = currencyRateRepository.findAll();
        log.info("Получен список всех курсов длинной {}", all.size());
        return all;
    }

    @CacheEvict(value = "rates", key = "#name")
    public void deleteCurrencyRate(String name){
        currencyRateRepository.findByName(name).ifPresentOrElse(currencyRateRepository::delete,
                () -> log.warn("Не найден курс {}", name));
    }
}

