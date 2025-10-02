package com.auth.shopping_list.mapper;

import com.auth.shopping_list.dto.UserDTO;
import com.auth.shopping_list.entity.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User userEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    public UserDTO userDto(User userEntity) {
        return modelMapper.map(userEntity, UserDTO.class);
    }
}
