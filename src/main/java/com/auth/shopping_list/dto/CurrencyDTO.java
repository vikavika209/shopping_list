package com.auth.shopping_list.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyDTO {
    private String name;
    private BigDecimal price;
    private BigDecimal qty;
    private String description;
}
