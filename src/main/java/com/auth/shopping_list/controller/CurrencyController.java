package com.auth.shopping_list.controller;

import com.auth.shopping_list.dto.CurrencyDTO;
import com.auth.shopping_list.entity.Currency;
import com.auth.shopping_list.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @PostMapping
    public ResponseEntity<Currency> createCurrency(@Validated @RequestBody CurrencyDTO currencyDTO) {
        log.info("Запрос на создание валюты: {}", currencyDTO.getName());
        Currency saved = currencyService.save(currencyDTO);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<Page<Currency>> getAllCurrencies(Pageable pageable) {
        log.info("Запрос списка всех валют");
        Page<Currency> all = currencyService.getAll(pageable);
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Currency> getCurrency(@PathVariable String name) {
        log.info("Запрос валюты по имени: {}", name);
        Currency currency = currencyService.getByName(name);
        return ResponseEntity.ok(currency);
    }

    @PutMapping
    public ResponseEntity<Currency> updateCurrency(@Validated @RequestBody CurrencyDTO currencyDTO) {
        log.info("Запрос на обновление валюты: {}", currencyDTO.getName());
        Currency updated = currencyService.update(currencyDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/auth/{name}")
    public ResponseEntity<Void> deleteCurrency(@PathVariable String name) {
        log.info("Запрос на удаление валюты: {}", name);
        currencyService.delete(name);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{name}/comment")
    public ResponseEntity<Currency> addComment(
            @PathVariable String name,
            @RequestParam String comment
    ) {
        log.info("Запрос на добавление комментария к валюте {}: {}", name, comment);
        Currency updated = currencyService.addComment(comment, name);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{name}/rate")
    public ResponseEntity<BigDecimal> getRate(@PathVariable String name) {
        log.info("Запрос на получение курса валюты: {}", name);
        BigDecimal rate = currencyService.getRate(name);
        return ResponseEntity.ok(rate);
    }
}