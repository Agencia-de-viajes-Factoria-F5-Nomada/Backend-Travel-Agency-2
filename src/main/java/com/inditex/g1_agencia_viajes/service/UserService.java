package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.UserRequestDTO;
import com.inditex.g1_agencia_viajes.dto.UserResponseDTO;
import com.inditex.g1_agencia_viajes.exception.EmailAlreadyExistsException;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.mapper.UserMapper;
import com.inditex.g1_agencia_viajes.model.User;
import com.inditex.g1_agencia_viajes.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserResponseDTO create(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }
        User user = userMapper.toEntity(dto);
        if (dto.getTutorId() != null) {
            User tutor = userRepository.findById(dto.getTutorId())
                    .orElseThrow(() -> new ResourceNotFoundException("el tutor", dto.getTutorId()));
            user.setTutorId(tutor);
        }
        return userMapper.toDTO(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el cliente", id));
        return userMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getActive(Pageable pageable) {
        return userRepository.findByActive(true, pageable)
                .map(userMapper::toDTO);
    }

    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el cliente", id));
        userMapper.updateFromDto(dto, user);
        if (dto.getTutorId() != null) {
            User tutor = userRepository.findById(dto.getTutorId())
                    .orElseThrow(() -> new ResourceNotFoundException("el tutor", dto.getTutorId()));
            user.setTutorId(tutor);
        }
        return userMapper.toDTO(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el cliente", id));
        user.setActive(false);
        userRepository.save(user);
    }
}