package com.inditex.gym_lorza.service;

import com.inditex.gym_lorza.dto.UserRequestDTO;
import com.inditex.gym_lorza.dto.UserResponseDTO;
import com.inditex.gym_lorza.exception.ObjectNotFoundException;
import com.inditex.gym_lorza.mapper.UserMapper;
import com.inditex.gym_lorza.model.User;
import com.inditex.gym_lorza.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseDTO> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    public UserResponseDTO findUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("usuaria", id));
        return UserMapper.toDTO(user);
    }

    public UserResponseDTO addUser(UserRequestDTO dto) {
        User user = UserMapper.toEntity(dto);
        return UserMapper.toDTO(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("usuaria", id));

        existingUser.setName(dto.getName());
        existingUser.setSurname(dto.getSurname());
        existingUser.setDni(dto.getDni());
        existingUser.setStartYear(dto.getStartYear());
        existingUser.setIsActive(dto.getIsActive());
        existingUser.setImage(dto.getImage());

        return UserMapper.toDTO(userRepository.save(existingUser));
    }
}