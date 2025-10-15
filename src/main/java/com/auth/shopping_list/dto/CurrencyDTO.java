package com.auth.shopping_list.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyDTO {
    @NotBlank(message = "Название валюты не может быть пустым")
    private String name;
    @NotNull(message = "Указание количества обязательно")
    @DecimalMin(value = "0.0", inclusive = false, message = "Количество должно быть > 0")
    private BigDecimal qty;
    private String description;
}
