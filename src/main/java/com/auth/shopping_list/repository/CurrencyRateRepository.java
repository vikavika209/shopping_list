package com.auth.shopping_list.repository;

import com.auth.shopping_list.entity.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {
    Optional<CurrencyRate> findByName(String name);

    @Query("select c.name from CurrencyRate c")
    List<String> findAllNames();
}
