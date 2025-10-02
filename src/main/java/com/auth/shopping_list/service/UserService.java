package com.auth.shopping_list.service;

import com.auth.shopping_list.dto.UserDTO;
import com.auth.shopping_list.entity.Role;
import com.auth.shopping_list.entity.User;
import com.auth.shopping_list.exception.UserNotFoundException;
import com.auth.shopping_list.mapper.UserMapper;
import com.auth.shopping_list.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public User save(UserDTO userDTO) {
        log.info("Сохранение пользователя: {}", userDTO.getUsername());
        String password = passwordEncoder.encode(userDTO.getPassword());
        User user = userMapper.userEntity(userDTO);
        user.setPassword(password);
        User savedUser = userRepository.save(user);
        log.info("Пользователь сохранён. Username = {}, Roles: {}", user.getUsername(), savedUser.getRoles());
        return savedUser;
    }

    public User updateUser(UserDTO userDTO) {
        User user = getUserByUsername(userDTO.getUsername());
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User savedUser =  userRepository.save(user);
        log.info("Пользователь обновлён. Username = {}, Roles: {}", user.getUsername(), savedUser.getRoles());
        return savedUser;
    }

    public User makeAdmin (String username) {
        User user = getUserByUsername(username);
        user.getRoles().add(Role.ROLE_ADMIN);
        User savedUser = userRepository.save(user);
        log.info("Пользователь: {} назначен администратором", username);
        log.info("Роли: {}", savedUser.getRoles());
        return savedUser;
    }

    public User removeAdmin (String username) {
        User user = getUserByUsername(username);
        log.info("Роль пользователя {} до изменений: {}", username, user.getRoles());
        user.getRoles().remove(Role.ROLE_ADMIN);
        User savedUser = userRepository.save(user);
        log.info("Пользователь: {} лишен статуса администратор", username);
        log.info("Роль пользователя {} после изменений: {}", username, savedUser.getRoles());
        return savedUser;
    }

    public User getUserByUsername(String username) {
        Optional<User> userOptional= userRepository.findByUsername(username);
        if(userOptional.isPresent()){
            return userOptional.get();
        }
        else {
            log.error("Пользователь не найден: {}", username);
            throw new UserNotFoundException("Пользователь не найден: " + username);
        }
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public void deleteUserByUsername(String username) {
        User user = getUserByUsername(username);
        userRepository.delete(user);
        log.info("Пользователь удалён: {}", username);
    }
}
