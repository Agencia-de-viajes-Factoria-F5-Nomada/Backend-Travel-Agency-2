package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.UserRequestDTO;
import com.inditex.g1_agencia_viajes.dto.UserResponseDTO;
import com.inditex.g1_agencia_viajes.exception.EmailAlreadyExistsException;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.mapper.UserMapper;
import com.inditex.g1_agencia_viajes.model.User;
import com.inditex.g1_agencia_viajes.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private UserService userService;

    private User user;
    private User tutor;
    private UserRequestDTO requestDTO;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userMapper);

        tutor = new User();
        tutor.setId(1L);
        tutor.setName("Tutor");
        tutor.setSurname("Test");
        tutor.setEmail("tutor@test.com");
        tutor.setAge(30);
        tutor.setActive(true);

        user = new User();
        user.setId(2L);
        user.setName("User");
        user.setSurname("Test");
        user.setEmail("user@test.com");
        user.setDni("12345678Z");
        user.setAge(25);
        user.setActive(true);

        requestDTO = new UserRequestDTO();
        requestDTO.setName("User");
        requestDTO.setSurname("Test");
        requestDTO.setEmail("user@test.com");
        requestDTO.setDni("12345678Z");
        requestDTO.setAge(25);
        requestDTO.setActive(true);

        responseDTO = new UserResponseDTO();
        responseDTO.setId(2L);
        responseDTO.setName("User");
        responseDTO.setSurname("Test");
        responseDTO.setEmail("user@test.com");
        responseDTO.setDni("12345678Z");
        responseDTO.setAge(25);
        responseDTO.setActive(true);
    }

    @Test
    void create_ShouldReturnUserResponseDTO() {
        when(userRepository.existsByEmail("user@test.com")).thenReturn(false);
        when(userMapper.toEntity(requestDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("user@test.com");
    }

    @Test
    void create_ShouldThrowEmailAlreadyExistsException() {
        when(userRepository.existsByEmail("user@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(requestDTO))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void create_WithTutor_ShouldResolveTutor() {
        requestDTO.setTutorId(1L);
        when(userRepository.existsByEmail("user@test.com")).thenReturn(false);
        when(userMapper.toEntity(requestDTO)).thenReturn(user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(tutor));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.create(requestDTO);

        assertThat(result).isNotNull();
        verify(userRepository).findById(1L);
        assertThat(user.getTutorId()).isEqualTo(tutor);
    }

    @Test
    void create_WithNonExistentTutor_ShouldThrowResourceNotFoundException() {
        requestDTO.setTutorId(99L);
        when(userRepository.existsByEmail("user@test.com")).thenReturn(false);
        when(userMapper.toEntity(requestDTO)).thenReturn(user);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.create(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAll_ShouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDTO(user)).thenReturn(responseDTO);

        List<UserResponseDTO> result = userService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getEmail()).isEqualTo("user@test.com");
    }

    @Test
    void getById_ShouldReturnUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.getById(2L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
    }

    @Test
    void getById_ShouldThrowResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getActive_ShouldReturnActiveUsers() {
        when(userRepository.findByActive(true)).thenReturn(List.of(user));
        when(userMapper.toDTO(user)).thenReturn(responseDTO);

        List<UserResponseDTO> result = userService.getActive();

        assertThat(result).hasSize(1);
    }

    @Test
    void update_ShouldReturnUpdatedUser() {
        UserRequestDTO updateDTO = new UserRequestDTO();
        updateDTO.setName("Updated");
        updateDTO.setSurname("User");

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO updatedResponse = new UserResponseDTO();
        updatedResponse.setId(2L);
        updatedResponse.setName("Updated");
        updatedResponse.setSurname("User");
        when(userMapper.toDTO(any(User.class))).thenReturn(updatedResponse);

        UserResponseDTO result = userService.update(2L, updateDTO);

        assertThat(result.getName()).isEqualTo("Updated");
        assertThat(result.getSurname()).isEqualTo("User");
    }

    @Test
    void update_WithTutorId_ShouldResolveTutor() {
        UserRequestDTO updateDTO = new UserRequestDTO();
        updateDTO.setTutorId(1L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(tutor));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO updatedResponse = new UserResponseDTO();
        updatedResponse.setId(2L);
        when(userMapper.toDTO(any(User.class))).thenReturn(updatedResponse);

        userService.update(2L, updateDTO);

        assertThat(user.getTutorId()).isEqualTo(tutor);
    }

    @Test
    void update_ShouldThrowResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, new UserRequestDTO()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_ShouldDeleteUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        userService.delete(2L);

        assertThat(user.getActive()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    void delete_ShouldThrowResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
