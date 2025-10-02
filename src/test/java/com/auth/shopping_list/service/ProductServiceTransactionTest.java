package com.auth.shopping_list.service;

import com.auth.shopping_list.config.ModelMapperConfig; // если у тебя есть отдельный конфиг
import com.auth.shopping_list.dto.ProductDTO;
import com.auth.shopping_list.entity.Product;
import com.auth.shopping_list.repository.ProductRepository;
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
public class ProductServiceTransactionTest {
    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    TxProbe txProbe;

    private static ProductDTO dto(String name, BigDecimal price, BigDecimal qty, String desc) {
        ProductDTO d = new ProductDTO();
        d.setName(name);
        d.setPrice(price);
        d.setQty(qty);
        d.setDescription(desc);
        return d;
    }

    @TestConfiguration
    class TestBeans {
        @Bean
        TxProbe txProbe(ProductService productService) {
            return new TxProbe(productService);
        }
    }

    @Nested
    class TxProbe {
        private final ProductService productService;
        @PersistenceContext EntityManager em;

        TxProbe(ProductService productService) {
            this.productService = productService;
        }

        @Transactional
        public void saveTwoAndFail(ProductDTO a, ProductDTO b) {
            productService.save(a);
            productService.save(b);
            em.flush();
            throw new RuntimeException("boom");
        }

        @BeforeEach
        void clean() {
            productRepository.deleteAll();
        }

        @Test
        void save_and_getByName_works_endToEnd() {
            productService.save(dto("apple", new BigDecimal(10), new BigDecimal(2), "green"));
            Product p = productService.getByName("apple");
            assertThat(p.getName()).isEqualTo("apple");
            assertThat(p.getPrice()).isEqualTo(BigDecimal.valueOf(10));
            assertThat(p.getQty()).isEqualTo(BigDecimal.valueOf(2));
        }
    }
}
