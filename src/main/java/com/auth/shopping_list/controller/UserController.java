package com.auth.shopping_list.controller;

import com.auth.shopping_list.dto.UserDTO;
import com.auth.shopping_list.entity.User;
import com.auth.shopping_list.exception.UserNotFoundException;
import com.auth.shopping_list.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@Validated @RequestBody UserDTO userDTO) {
        log.info("Запрос на создание пользователя: {}", userDTO.getUsername());
        User savedUser = userService.save(userDTO);
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Validated @RequestBody UserDTO userDTO) {
        log.info("Запрос на обновление пользователя: {}", userDTO.getUsername());
        User updatedUser = userService.updateUser(userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/auth/{username}/make-admin")
    public ResponseEntity<User> makeAdmin(@PathVariable String username) {
        log.info("Запрос на назначение администратора: {}", username);
        User updatedUser = userService.makeAdmin(username);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/auth/{username}/remove-admin")
    public ResponseEntity<User> removeAdmin(@PathVariable String username) {
        log.info("Запрос на снятие роли администратора: {}", username);
        User updatedUser = userService.removeAdmin(username);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        try {
            log.info("Запрос на получение пользователя: {}", username);
            User user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        }catch (UserNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        log.info("Запрос на получение всех пользователей");
        Page<User> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/auth/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        log.info("Запрос на удаление пользователя: {}", username);
        userService.deleteUserByUsername(username);
        return ResponseEntity.noContent().build();
    }
}