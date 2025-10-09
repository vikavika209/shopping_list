package com.auth.shopping_list.service;

import com.auth.shopping_list.dto.CurrencyDTO;
import com.auth.shopping_list.entity.Currency;
import com.auth.shopping_list.exception.CurrencyNotFoundException;
import com.auth.shopping_list.mapper.CurrencyMapper;
import com.auth.shopping_list.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    public Currency save(CurrencyDTO currencyDTO) {
        log.info("Сохранение валюты: {}", currencyDTO.toString());
        Currency product = currencyMapper.productEntity(currencyDTO);
        Currency savedProduct = currencyRepository.saveAndFlush(product);
        log.info("Валюта сохранена: {}", savedProduct.toString());
        return savedProduct;
    }

    public Page<Currency> getAll(Pageable pageable) {
        log.info("Запрос списка всех валют");
        Page<Currency> all = currencyRepository.findAll(pageable);
        log.info("Получен список всех валют размером: {}", all.getSize());
        return all;
    }

    public Currency getByName(String name) {
        log.info("Поиск валюты {}", name);
        Optional<Currency> productByName = currencyRepository.findProductByName(name);
        if (productByName.isPresent()) {
            Currency product = productByName.get();
            log.info("Валюта найдена: {}", product.toString());
            return product;
        }
        else {
            log.error("Валюта {} отсутствует", name);
            throw new CurrencyNotFoundException("Валюта не найдена: " + name);
        }
    }

    public Currency update (CurrencyDTO currencyDTO) {
        log.info("Изменение валюты: {}", currencyDTO.getName());
        Currency product = getByName(currencyDTO.getName());
        product.setName(currencyDTO.getName());
        product.setQty(currencyDTO.getQty());
        product.setPrice(currencyDTO.getPrice());
        product.setDescription(currencyDTO.getDescription());
        Currency save = currencyRepository.save(product);
        log.info("Валюта обновлена: {}", save.toString());
        return save;
    }

    public void delete (String name) {
        log.info("Удаление валюты: {}", name);
        Currency byName = getByName(name);
        currencyRepository.delete(byName);
        log.info("Валюта успешно удалена: {}", byName.getName());
    }

    public Currency addComment (String comment, String currencyName){
        log.info("Добавление комментария: {}", comment);
        Currency currency = getByName(currencyName);
        currency.setDescription(comment);
        Currency save = currencyRepository.save(currency);
        log.info("Добавлен комментарий: {}", save.toString());
        return save;
    }
}
