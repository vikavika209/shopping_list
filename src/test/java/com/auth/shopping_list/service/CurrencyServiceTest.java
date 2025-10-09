package com.auth.shopping_list.service;

import com.auth.shopping_list.dto.CurrencyDTO;
import com.auth.shopping_list.entity.Currency;
import com.auth.shopping_list.exception.CurrencyNotFoundException;
import com.auth.shopping_list.mapper.CurrencyMapper;
import com.auth.shopping_list.repository.CurrencyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {
    @Mock
    CurrencyRepository currencyRepository;
    @Mock
    CurrencyMapper currencyMapper;

    @InjectMocks
    CurrencyService currencyService;

    private CurrencyDTO dto(String name, BigDecimal price, BigDecimal qty, String desc) {
        CurrencyDTO d = new CurrencyDTO();
        d.setName(name);
        d.setPrice(price);
        d.setQty(qty);
        d.setDescription(desc);
        return d;
    }

    private Currency entity(String name, BigDecimal price, BigDecimal qty, String desc) {
        Currency p = new Currency();
        p.setName(name);
        p.setPrice(price);
        p.setQty(qty);
        p.setDescription(desc);
        return p;
    }

    @Test
    void save_mapsAndPersists() {
        CurrencyDTO dto = dto("apple", new BigDecimal(10), new BigDecimal(2), "green");
        Currency mapped = entity("apple", new BigDecimal(10), new BigDecimal(2), "green");
        Currency saved  = entity("apple", new BigDecimal(10), new BigDecimal(2), "green");

        when(currencyMapper.productEntity(dto)).thenReturn(mapped);
        when(currencyRepository.saveAndFlush(mapped)).thenReturn(saved);

        Currency result = currencyService.save(dto);

        assertThat(result.getName()).isEqualTo("apple");
        verify(currencyMapper).productEntity(dto);
        verify(currencyRepository).saveAndFlush(mapped);
        verifyNoMoreInteractions(currencyMapper, currencyRepository);
    }

    @Test
    void getAll_delegatesToRepo() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Currency> list = List.of(entity("a",new BigDecimal(1),new BigDecimal(1),"No comment"), entity("b",new BigDecimal(2),new BigDecimal(1),"No comment"));
        Page<Currency> page = new PageImpl<>(list, pageable, list.size());

        when(currencyRepository.findAll(pageable)).thenReturn(page);

        Page<Currency> result = currencyService.getAll(pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(currencyRepository).findAll(pageable);
    }

    @Test
    void getByName_returns_whenFound() {
        Currency p = entity("book", new BigDecimal(5), new BigDecimal(1), "");
        when(currencyRepository.findProductByName("book")).thenReturn(Optional.of(p));

        Currency found = currencyService.getByName("book");

        assertThat(found).isSameAs(p);
        verify(currencyRepository).findProductByName("book");
    }

    @Test
    void getByName_throws_whenAbsent() {
        when(currencyRepository.findProductByName("404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> currencyService.getByName("404"))
                .isInstanceOf(CurrencyNotFoundException.class);
        verify(currencyRepository).findProductByName("404");
    }

    @Test
    void update_copiesFields_andSaves() {
        Currency existing = entity("pen", new BigDecimal(1), new BigDecimal(1), "old");
        when(currencyRepository.findProductByName("pen")).thenReturn(Optional.of(existing));
        when(currencyRepository.save(existing)).thenReturn(existing);

        CurrencyDTO dto = dto("pen", new BigDecimal(2), new BigDecimal(3), "new");
        Currency saved = currencyService.update(dto);

        assertThat(saved.getPrice()).isEqualTo(BigDecimal.valueOf(2));
        assertThat(saved.getQty()).isEqualTo(BigDecimal.valueOf(3));
        assertThat(saved.getDescription()).isEqualTo("new");

        verify(currencyRepository).findProductByName("pen");
        verify(currencyRepository).save(existing);
    }

    @Test
    void delete_findsThenDeletes() {
        Currency p = entity("milk", new BigDecimal(1), new BigDecimal(1), "");
        when(currencyRepository.findProductByName("milk")).thenReturn(Optional.of(p));

        currencyService.delete("milk");

        verify(currencyRepository).findProductByName("milk");
        verify(currencyRepository).delete(p);
    }

    @Test
    void add_comment(){
        Currency currency = new Currency();
        currency.setName("EUR");
        currency.setPrice(new BigDecimal(100));
        currency.setQty(new BigDecimal(2));

        Optional<Currency> currencyOptional = Optional.of(currency);

        when(currencyRepository.findProductByName("EUR")).thenReturn(currencyOptional);
        when(currencyRepository.save(currency)).thenReturn(currency);

        Currency withComment = currencyService.addComment("New comment", "EUR");

        verify(currencyRepository).save(currency);
        assertThat(withComment.getDescription().equals("New comment"));
    }



}