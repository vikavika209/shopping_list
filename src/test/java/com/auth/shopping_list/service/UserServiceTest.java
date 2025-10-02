package com.auth.shopping_list.service;

import com.auth.shopping_list.dto.UserDTO;
import com.auth.shopping_list.entity.Role;
import com.auth.shopping_list.entity.User;
import com.auth.shopping_list.exception.UserNotFoundException;
import com.auth.shopping_list.mapper.UserMapper;
import com.auth.shopping_list.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserDTO dto(String username, String password) {
        UserDTO dto = new UserDTO();
        dto.setUsername(username);
        dto.setPassword(password);
        return dto;
    }

    private User user(String username, String encodedPassword) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(encodedPassword);
        return u;
    }

    @Test
    void save_encodesPassword_mapsAndPersists_andReturnsSaved() {
        // given
        UserDTO request = dto("alice", "raw");
        User mapped = user("alice", null);
        User saved = user("alice", "ENC(raw)");

        when(passwordEncoder.encode("raw")).thenReturn("ENC(raw)");
        when(userMapper.userEntity(request)).thenReturn(mapped);
        when(userRepository.save(mapped)).thenReturn(saved);

        // when
        User result = userService.save(request);

        // then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User passedToSave = captor.getValue();

        assertThat(passedToSave.getUsername()).isEqualTo("alice");
        assertThat(passedToSave.getPassword()).isEqualTo("ENC(raw)");
        assertThat(result.getPassword()).isEqualTo("ENC(raw)");
        assertThat(result.getRoles().size()).isEqualTo(1);

        verify(passwordEncoder).encode("raw");
        verify(userMapper).userEntity(request);
        verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
    }

    @Test
    void updateUser_findsByUsername_encodesPassword_andSaves() {
        // given
        UserDTO request = dto("bob", "newPass");
        User existing = user("bob", "oldEnc");
        User saved = user("bob", "ENC(newPass)");

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newPass")).thenReturn("ENC(newPass)");
        when(userRepository.save(existing)).thenReturn(saved);

        // when
        User result = userService.updateUser(request);

        // then
        assertThat(existing.getPassword()).isEqualTo("ENC(newPass)");
        assertThat(result.getPassword()).isEqualTo("ENC(newPass)");
        verify(userRepository).findByUsername("bob");
        verify(passwordEncoder).encode("newPass");
        verify(userRepository).save(existing);
        verifyNoMoreInteractions(userRepository, passwordEncoder);
        verifyNoInteractions(userMapper);
    }

    @Test
    void makeAdmin_addsRoleAndSaves() {
        // given
        User existing = user("kate", "enc");
        when(userRepository.findByUsername("kate")).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        User result = userService.makeAdmin("kate");

        // then
        assertThat(result.getRoles().size()).isEqualTo(2);
        verify(userRepository).findByUsername("kate");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper, passwordEncoder);
    }

    @Test
    void removeAdmin_removesRoleAndSaves() {
        // given
        User existing = user("mike", "enc");
        existing.getRoles().add(Role.ROLE_ADMIN);

        when(userRepository.findByUsername("mike")).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        User result = userService.removeAdmin("mike");

        // then
        assertThat(result.getRoles().size()).isEqualTo(1);
        verify(userRepository).findByUsername("mike");
        verify(userRepository).save(existing);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper, passwordEncoder);
    }

    @Test
    void getUserByUsername_returnsUser_whenFound() {
        User u = user("john", "enc");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(u));

        User result = userService.getUserByUsername("john");

        assertThat(result).isSameAs(u);
        verify(userRepository).findByUsername("john");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserByUsername_throws_whenNotFound() {
        when(userRepository.findByUsername("absent")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserByUsername("absent"));

        verify(userRepository).findByUsername("absent");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllUsers_delegatesToRepository() {
        var pageable = PageRequest.of(0, 10);
        List<User> content = List.of(
                user("a", "enc"),
                user("b", "enc")
        );
        Page<User> page = new PageImpl<>(content, pageable, content.size());

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<User> result = userService.getAllUsers(pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(userRepository).findAll(pageable);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUserByUsername_findsThenDeletes() {
        User u = user("toDelete", "enc");
        when(userRepository.findByUsername("toDelete")).thenReturn(Optional.of(u));

        userService.deleteUserByUsername("toDelete");

        verify(userRepository).findByUsername("toDelete");
        verify(userRepository).delete(u);
        verifyNoMoreInteractions(userRepository);
    }


}