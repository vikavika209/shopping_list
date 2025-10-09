package com.auth.shopping_list.repository;

import com.auth.shopping_list.entity.Currency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findProductByName(String productName);
    Page<Currency> findAll(Pageable pageable);

}
