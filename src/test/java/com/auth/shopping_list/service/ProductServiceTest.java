package com.auth.shopping_list.service;

import com.auth.shopping_list.dto.ProductDTO;
import com.auth.shopping_list.entity.Product;
import com.auth.shopping_list.exception.ProductNotFoundException;
import com.auth.shopping_list.mapper.ProductMapper;
import com.auth.shopping_list.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    ProductRepository productRepository;
    @Mock
    ProductMapper productMapper;

    @InjectMocks
    ProductService productService;

    private ProductDTO dto(String name, BigDecimal price, BigDecimal qty, String desc) {
        ProductDTO d = new ProductDTO();
        d.setName(name);
        d.setPrice(price);
        d.setQty(qty);
        d.setDescription(desc);
        return d;
    }

    private Product entity(String name, BigDecimal price, BigDecimal qty, String desc) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setQty(qty);
        p.setDescription(desc);
        return p;
    }

    @Test
    void save_mapsAndPersists() {
        ProductDTO dto = dto("apple", new BigDecimal(10), new BigDecimal(2), "green");
        Product mapped = entity("apple", new BigDecimal(10), new BigDecimal(2), "green");
        Product saved  = entity("apple", new BigDecimal(10), new BigDecimal(2), "green");

        when(productMapper.productEntity(dto)).thenReturn(mapped);
        when(productRepository.saveAndFlush(mapped)).thenReturn(saved);

        Product result = productService.save(dto);

        assertThat(result.getName()).isEqualTo("apple");
        verify(productMapper).productEntity(dto);
        verify(productRepository).saveAndFlush(mapped);
        verifyNoMoreInteractions(productMapper, productRepository);
    }

    @Test
    void getAll_delegatesToRepo() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Product> list = List.of(entity("a",new BigDecimal(1),new BigDecimal(1),"No comment"), entity("b",new BigDecimal(2),new BigDecimal(1),"No comment"));
        Page<Product> page = new PageImpl<>(list, pageable, list.size());

        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<Product> result = productService.getAll(pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(productRepository).findAll(pageable);
    }

    @Test
    void getByName_returns_whenFound() {
        Product p = entity("book", new BigDecimal(5), new BigDecimal(1), "");
        when(productRepository.findProductByName("book")).thenReturn(Optional.of(p));

        Product found = productService.getByName("book");

        assertThat(found).isSameAs(p);
        verify(productRepository).findProductByName("book");
    }

    @Test
    void getByName_throws_whenAbsent() {
        when(productRepository.findProductByName("404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getByName("404"))
                .isInstanceOf(ProductNotFoundException.class);
        verify(productRepository).findProductByName("404");
    }

    @Test
    void update_copiesFields_andSaves() {
        Product existing = entity("pen", new BigDecimal(1), new BigDecimal(1), "old");
        when(productRepository.findProductByName("pen")).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        ProductDTO dto = dto("pen", new BigDecimal(2), new BigDecimal(3), "new");
        Product saved = productService.update(dto);

        assertThat(saved.getPrice()).isEqualTo(BigDecimal.valueOf(2));
        assertThat(saved.getQty()).isEqualTo(BigDecimal.valueOf(3));
        assertThat(saved.getDescription()).isEqualTo("new");

        verify(productRepository).findProductByName("pen");
        verify(productRepository).save(existing);
    }

    @Test
    void delete_findsThenDeletes() {
        Product p = entity("milk", new BigDecimal(1), new BigDecimal(1), "");
        when(productRepository.findProductByName("milk")).thenReturn(Optional.of(p));

        productService.delete("milk");

        verify(productRepository).findProductByName("milk");
        verify(productRepository).delete(p);
    }

}