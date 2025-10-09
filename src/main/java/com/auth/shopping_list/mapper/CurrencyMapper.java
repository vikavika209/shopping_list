package com.auth.shopping_list.mapper;

import com.auth.shopping_list.dto.CurrencyDTO;
import com.auth.shopping_list.entity.Currency;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CurrencyMapper {
    private final ModelMapper modelMapper;

    public CurrencyMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Currency productEntity (CurrencyDTO currencyDTO) {
        return modelMapper.map(currencyDTO, Currency.class);
    }

    public CurrencyDTO productDTO (Currency product) {
      return modelMapper.map(product, CurrencyDTO.class);
    }
}
