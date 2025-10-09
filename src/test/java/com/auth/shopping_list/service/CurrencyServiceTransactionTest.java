package com.auth.shopping_list.service;

import com.auth.shopping_list.dto.CurrencyDTO;
import com.auth.shopping_list.entity.Currency;
import com.auth.shopping_list.repository.CurrencyRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driverClassName=org.h2.Driver"
})
public class CurrencyServiceTransactionTest {
    @Autowired
    CurrencyService currencyService;

    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    TxProbe txProbe;

    private static CurrencyDTO dto(String name, BigDecimal price, BigDecimal qty, String desc) {
        CurrencyDTO d = new CurrencyDTO();
        d.setName(name);
        d.setPrice(price);
        d.setQty(qty);
        d.setDescription(desc);
        return d;
    }

    @TestConfiguration
    class TestBeans {
        @Bean
        TxProbe txProbe(CurrencyService currencyService) {
            return new TxProbe(currencyService);
        }
    }

    @Nested
    class TxProbe {
        private final CurrencyService currencyService;
        @PersistenceContext EntityManager em;

        TxProbe(CurrencyService currencyService) {
            this.currencyService = currencyService;
        }

        @Transactional
        public void saveTwoAndFail(CurrencyDTO a, CurrencyDTO b) {
            currencyService.save(a);
            currencyService.save(b);
            em.flush();
            throw new RuntimeException("boom");
        }

        @BeforeEach
        void clean() {
            currencyRepository.deleteAll();
        }

        @Test
        void save_and_getByName_works_endToEnd() {
            currencyService.save(dto("apple", new BigDecimal(10), new BigDecimal(2), "green"));
            Currency p = currencyService.getByName("apple");
            assertThat(p.getName()).isEqualTo("apple");
            assertThat(p.getPrice()).isEqualTo(BigDecimal.valueOf(10));
            assertThat(p.getQty()).isEqualTo(BigDecimal.valueOf(2));
        }
    }
}
