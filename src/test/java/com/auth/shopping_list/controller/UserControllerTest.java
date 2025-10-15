package com.auth.shopping_list.controller;

import com.auth.shopping_list.dto.UserDTO;
import com.auth.shopping_list.entity.Role;
import com.auth.shopping_list.entity.User;
import com.auth.shopping_list.exception.UserNotFoundException;
import com.auth.shopping_list.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;


@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    @Resource
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Resource
    private ObjectMapper objectMapper;

    private User sampleUser() {
        User u = new User();
        u.setId(1L);
        u.setUsername("vika");
        u.setPassword("$2a$hash");
        return u;
    }

    @Test
    @DisplayName("POST /api/users — создание пользователя OK")
    void createUser_ok() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setUsername("vika");
        dto.setPassword("secret");

        Mockito.when(userService.save(any(UserDTO.class))).thenReturn(sampleUser());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("vika")))
                .andExpect(jsonPath("$.roles", notNullValue()));

        Mockito.verify(userService).save(any(UserDTO.class));
    }

    @Test
    @DisplayName("PUT /api/users — обновление пользователя OK")
    void updateUser_ok() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setUsername("vika");
        dto.setPassword("newpass");

        User updated = sampleUser();
        Mockito.when(userService.updateUser(any(UserDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("vika")));
    }

    @Test
    @DisplayName("GET /api/users/{username} — найден")
    void getUser_found() throws Exception {
        Mockito.when(userService.getUserByUsername("vika")).thenReturn(sampleUser());

        mockMvc.perform(get("/api/users/vika"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("vika")));
    }

    @Test
    @DisplayName("GET /api/users/{username} — не найден -> 404")
    void getUser_notFound() throws Exception {
        Mockito.when(userService.getUserByUsername("nope"))
                .thenThrow(new UserNotFoundException("Пользователь не найден: nope"));

        mockMvc.perform(get("/api/users/nope"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/users — пагинация OK")
    void getAllUsers_ok() throws Exception {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("username").ascending());
        List<User> content = List.of(sampleUser());
        Page<User> page = new PageImpl<>(content, pageable, 1);

        Mockito.when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "username,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].username", is("vika")))
                .andExpect(jsonPath("$.size", is(2)))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    @DisplayName("PATCH /api/users/auth/{username}/make-admin — OK")
    void makeAdmin_ok() throws Exception {
        User admined = sampleUser();
        admined.getRoles().add(Role.ROLE_ADMIN);
        Mockito.when(userService.makeAdmin("vika")).thenReturn(admined);

        mockMvc.perform(patch("/api/users/auth/vika/make-admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles", hasItem("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("PATCH /api/users/auth/{username}/remove-admin — OK")
    void removeAdmin_ok() throws Exception {
        User u = sampleUser();
        Mockito.when(userService.removeAdmin("vika")).thenReturn(u);

        mockMvc.perform(patch("/api/users/auth/vika/remove-admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles", not(hasItem("ROLE_ADMIN"))));
    }

    @Test
    @DisplayName("DELETE /api/users/auth/{username} — 204 No Content")
    void deleteUser_noContent() throws Exception {
        Mockito.doNothing().when(userService).deleteUserByUsername("vika");

        mockMvc.perform(delete("/api/users/auth/vika"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).deleteUserByUsername("vika");
    }

    @Test
    @DisplayName("POST /api/users — валидация: пустой username -> 400")
    void createUser_validationFail() throws Exception {
        UserDTO bad = new UserDTO();
        bad.setUsername("");
        bad.setPassword("123");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());
    }

}