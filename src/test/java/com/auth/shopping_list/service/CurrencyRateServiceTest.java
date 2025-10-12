package com.auth.shopping_list.service;

import com.auth.shopping_list.client.RateClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "spring.cache.type=simple")
class CurrencyRateServiceTest {

    @MockitoBean
    RateClient rateClient;

    @Autowired
    CurrencyRateService currencyRateService;

    @Test
    void getRate() {
        when(rateClient.fetchRate(anyString()))
                .thenReturn(BigDecimal.valueOf(100));


        BigDecimal a = currencyRateService.getRate("EUR");
        BigDecimal b = currencyRateService.getRate("EUR");

        assertThat(a).isEqualTo(BigDecimal.valueOf(100));
        assertThat(b).isEqualTo(BigDecimal.valueOf(100));

        verify(rateClient, times(1)).fetchRate(anyString());
        verifyNoMoreInteractions(rateClient);
    }

    @Test
    void updateRate() {
        when(rateClient.fetchRate(anyString()))
                .thenReturn(BigDecimal.valueOf(100));

        BigDecimal a = currencyRateService.getRate("EUR");

        when(rateClient.fetchRate(anyString()))
                .thenReturn(BigDecimal.valueOf(90));

        BigDecimal b = currencyRateService.updateRate("EUR");
        BigDecimal c = currencyRateService.getRate("EUR");
        BigDecimal d = currencyRateService.getRate("EUR");


        assertThat(a).isEqualTo(BigDecimal.valueOf(100));
        assertThat(b).isEqualTo(BigDecimal.valueOf(90));
        assertThat(c).isEqualTo(BigDecimal.valueOf(90));
        assertThat(d).isEqualTo(BigDecimal.valueOf(90));
        verify(rateClient, times(2)).fetchRate(anyString());
    }
}