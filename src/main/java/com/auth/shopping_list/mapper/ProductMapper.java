package com.auth.shopping_list.mapper;

import com.auth.shopping_list.dto.ProductDTO;
import com.auth.shopping_list.entity.Product;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    private final ModelMapper modelMapper;

    public ProductMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Product productEntity (ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }

    public ProductDTO productDTO (Product product) {
      return modelMapper.map(product, ProductDTO.class);
    }
}
