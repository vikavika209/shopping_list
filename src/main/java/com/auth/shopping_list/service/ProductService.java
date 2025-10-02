package com.auth.shopping_list.service;

import com.auth.shopping_list.dto.ProductDTO;
import com.auth.shopping_list.entity.Product;
import com.auth.shopping_list.exception.ProductNotFoundException;
import com.auth.shopping_list.mapper.ProductMapper;
import com.auth.shopping_list.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Product save(ProductDTO productDTO) {
        log.info("Сохранение продукта: {}", productDTO.toString());
        Product product = productMapper.productEntity(productDTO);
        Product savedProduct = productRepository.saveAndFlush(product);
        log.info("Сохранён продукт: {}", savedProduct.toString());
        return savedProduct;
    }

    public Page<Product> getAll(Pageable pageable) {
        log.info("Запрос списка всех продуктов");
        Page<Product> all = productRepository.findAll(pageable);
        log.info("Получен список всех продуктов размером: {}", all.getSize());
        return all;
    }

    public Product getByName(String name) {
        log.info("Поиск продукта {}", name);
        Optional<Product> productByName = productRepository.findProductByName(name);
        if (productByName.isPresent()) {
            Product product = productByName.get();
            log.info("Продукт найден: {}", product.toString());
            return product;
        }
        else {
            log.error("Продукт с именем {} отсутствует", name);
            throw new ProductNotFoundException("Продукт не найден: " + name);
        }
    }

    public Product update (ProductDTO productDTO) {
        log.info("Изменение продукта: {}", productDTO.getName());
        Product product = getByName(productDTO.getName());
        product.setName(productDTO.getName());
        product.setQty(productDTO.getQty());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        Product save = productRepository.save(product);
        log.info("Продукт успешно обновлён: {}", save.toString());
        return save;
    }

    public void delete (String name) {
        log.info("Удаление продукта: {}", name);
        Product byName = getByName(name);
        productRepository.delete(byName);
        log.info("Продукт успешно удалён: {}", byName.getName());
    }
}
