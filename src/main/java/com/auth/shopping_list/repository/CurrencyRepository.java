package com.auth.shopping_list.repository;

import com.auth.shopping_list.entity.Currency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findProductByName(String productName);
    Page<Currency> findAll(Pageable pageable);
    @Query("select distinct c.name from Currency c")
    Set<String> findAllNames();
}
