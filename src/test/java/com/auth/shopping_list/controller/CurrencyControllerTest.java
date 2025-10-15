package com.auth.shopping_list.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.auth.shopping_list.controller.CurrencyController;
import com.auth.shopping_list.dto.CurrencyDTO;
import com.auth.shopping_list.entity.Currency;
import com.auth.shopping_list.service.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CurrencyController.class)
@AutoConfigureMockMvc(addFilters = false)
class CurrencyControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CurrencyService currencyService;

    private CurrencyDTO dto(String name, BigDecimal qty, String comment) {
        return new CurrencyDTO(name, qty, comment);
    }

    private Currency entity(String name, BigDecimal price, BigDecimal qty, String comment) {
        Currency c = new Currency();
        c.setId(1L);
        c.setName(name);
        c.setPrice(price);
        c.setQty(qty);
        c.setDescription(comment);
        return c;
    }

    @Nested
    @DisplayName("POST /api/currencies")
    class CreateCurrency {

        @Test
        @DisplayName("201/200 OK: валюта создана и возвращена")
        void create_ok() throws Exception {
            CurrencyDTO body = dto("EUR", new BigDecimal("4"), "init");
            Currency saved = entity("EUR", new BigDecimal("4"), new BigDecimal("4"), "init");

            when(currencyService.save(ArgumentMatchers.any(CurrencyDTO.class))).thenReturn(saved);

            mockMvc.perform(post("/api/currencies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("EUR"))
                    .andExpect(jsonPath("$.price").value(4))
                    .andExpect(jsonPath("$.description").value("init"));

            verify(currencyService, times(1)).save(any(CurrencyDTO.class));
        }

        @Test
        @DisplayName("400 Bad Request: невалидное тело (если DTO помечен @Valid)")
        void create_badRequest_onValidationError() throws Exception {
            CurrencyDTO invalid = dto("", null, null);

            mockMvc.perform(post("/api/currencies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/currencies")
    class GetAllCurrencies {

        @Test
        @DisplayName("200 OK: листинг с пагинацией (page,size,sort)")
        void list_ok_withPagination() throws Exception {
            List<Currency> content = List.of(
                    entity("EUR", new BigDecimal("4"), new BigDecimal("4"), null),
                    entity("USD", new BigDecimal("4"), new BigDecimal("4"),"hot")
            );
            Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());
            Page<Currency> page = new PageImpl<>(content, pageable, 5);

            when(currencyService.getAll(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/api/currencies")
                            .param("page", "0")
                            .param("size", "2")
                            .param("sort", "name,asc"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content[0].name").value("EUR"))
                    .andExpect(jsonPath("$.content[1].name").value("USD"))
                    .andExpect(jsonPath("$.size").value(2))
                    .andExpect(jsonPath("$.number").value(0))
                    .andExpect(jsonPath("$.totalElements").value(5));

            verify(currencyService).getAll(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("GET /api/currencies/{name}")
    class GetByName {

        @Test
        @DisplayName("200 OK: возвращает валюту по имени")
        void get_ok() throws Exception {
            Currency eur = entity("EUR", new BigDecimal("4"), new BigDecimal("4"), "ok");
            when(currencyService.getByName("EUR")).thenReturn(eur);

            mockMvc.perform(get("/api/currencies/{name}", "EUR"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("EUR"))
                    .andExpect(jsonPath("$.price").value(4))
                    .andExpect(jsonPath("$.description").value("ok"));

            verify(currencyService).getByName("EUR");
        }
    }

    @Nested
    @DisplayName("PUT /api/currencies")
    class UpdateCurrency {

        @Test
        @DisplayName("200 OK: обновляет валюту")
        void update_ok() throws Exception {
            CurrencyDTO body = dto("USD", new BigDecimal("3"), "upd");
            Currency updated = entity("USD", new BigDecimal("3"), new BigDecimal("3"),"upd");

            when(currencyService.update(any(CurrencyDTO.class))).thenReturn(updated);

            mockMvc.perform(put("/api/currencies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("USD"))
                    .andExpect(jsonPath("$.price").value(3))
                    .andExpect(jsonPath("$.totalPrice").value(9))
                    .andExpect(jsonPath("$.description").value("upd"));

            verify(currencyService).update(any(CurrencyDTO.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/currencies/auth/{name}")
    class DeleteCurrency {

        @Test
        @DisplayName("204 No Content: удаляет валюту")
        void delete_noContent() throws Exception {
            doNothing().when(currencyService).delete("EUR");

            mockMvc.perform(delete("/api/currencies/auth/{name}", "EUR"))
                    .andExpect(status().isNoContent());

            verify(currencyService).delete("EUR");
        }
    }

    @Nested
    @DisplayName("PATCH /api/currencies/{name}/comment")
    class AddComment {

        @Test
        @DisplayName("200 OK: добавляет/обновляет комментарий")
        void patch_comment_ok() throws Exception {
            Currency withComment = entity("EUR", new BigDecimal("4"), new BigDecimal("4"),"new comment");
            when(currencyService.addComment("new comment", "EUR")).thenReturn(withComment);

            mockMvc.perform(patch("/api/currencies/{name}/comment", "EUR")
                            .param("comment", "new comment"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("EUR"))
                    .andExpect(jsonPath("$.description").value("new comment"));

            verify(currencyService).addComment("new comment", "EUR");
        }
    }

    @Nested
    @DisplayName("GET /api/currencies/{name}/rate")
    class GetRate {

        @Test
        @DisplayName("200 OK: возвращает курс как BigDecimal")
        void get_rate_ok() throws Exception {
            when(currencyService.getRate("EUR")).thenReturn(new BigDecimal("4.56"));

            mockMvc.perform(get("/api/currencies/{name}/rate", "EUR"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string("4.56"));

            verify(currencyService).getRate("EUR");
        }
    }
    }
