package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.UserRequestDTO;
import com.inditex.g1_agencia_viajes.dto.UserResponseDTO;
import com.inditex.g1_agencia_viajes.exception.ForbiddenAccessException;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.mapper.UserMapper;
import com.inditex.g1_agencia_viajes.model.Role;
import com.inditex.g1_agencia_viajes.model.User;
import com.inditex.g1_agencia_viajes.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());
        when(userMapper.toEntity(requestDTO)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDTO(any(User.class))).thenReturn(responseDTO);

        UserResponseDTO result = userService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("user@test.com");
    }

    @Test
    void create_WithExistingEmail_ShouldReturnExistingUser() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("user@test.com");
    }

    @Test
    void create_WithTutor_ShouldResolveTutor() {
        requestDTO.setTutorId(1L);
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());
        when(userMapper.toEntity(requestDTO)).thenReturn(user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(tutor));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDTO(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            UserResponseDTO dto = new UserResponseDTO();
            dto.setId(saved.getId());
            dto.setName(saved.getName());
            dto.setSurname(saved.getSurname());
            dto.setEmail(saved.getEmail());
            dto.setTutorId(saved.getTutorId() != null ? saved.getTutorId().getId() : null);
            dto.setActive(saved.getActive());
            return dto;
        });

        UserResponseDTO result = userService.create(requestDTO);

        assertThat(result).isNotNull();
        verify(userRepository).findById(1L);
        assertThat(result.getTutorId()).isEqualTo(1L);
    }

    @Test
    void create_WithNonExistentTutor_ShouldThrowResourceNotFoundException() {
        requestDTO.setTutorId(99L);
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.create(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAll_WhenAdmin_ShouldReturnAllUsers() {
        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(user)));
        when(userMapper.toDTO(user)).thenReturn(responseDTO);

        Page<UserResponseDTO> result = userService.getAll(Pageable.unpaged(), 1L, Role.ADMIN);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getEmail()).isEqualTo("user@test.com");
    }

    @Test
    void getAll_WhenEmployee_ShouldReturnOnlyTheirBookingUsers() {
        when(userRepository.findByBookingsEmployeeId(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));
        when(userMapper.toDTO(user)).thenReturn(responseDTO);

        Page<UserResponseDTO> result = userService.getAll(Pageable.unpaged(), 1L, Role.EMPLOYEE);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getEmail()).isEqualTo("user@test.com");
    }

    @Test
    void getById_WhenAdmin_ShouldReturnUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.getById(2L, 1L, Role.ADMIN);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
    }

    @Test
    void getById_WhenEmployeeOwnUser_ShouldReturnUser() {
        when(userRepository.existsUserInEmployeeBookings(2L, 1L)).thenReturn(true);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.getById(2L, 1L, Role.EMPLOYEE);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
    }

    @Test
    void getById_WhenEmployeeOtherUser_ShouldThrowForbidden() {
        when(userRepository.existsUserInEmployeeBookings(99L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> userService.getById(99L, 1L, Role.EMPLOYEE))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessageContaining("No tienes permiso para ver los datos de este cliente");

        verify(userRepository, never()).findById(any());
    }

    @Test
    void getById_WhenNotFound_ShouldThrowResourceNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L, 1L, Role.ADMIN))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getActive_WhenAdmin_ShouldReturnActiveUsers() {
        when(userRepository.findByActive(eq(true), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(user)));

        Page<UserResponseDTO> result = userService.getActive(Pageable.unpaged(), 1L, Role.ADMIN);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getActive_WhenEmployee_ShouldReturnOnlyTheirBookingUsers() {
        when(userRepository.findByBookingsEmployeeId(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));
        when(userMapper.toDTO(user)).thenReturn(responseDTO);

        Page<UserResponseDTO> result = userService.getActive(Pageable.unpaged(), 1L, Role.EMPLOYEE);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void update_ShouldReturnUpdatedUser() {
        UserRequestDTO updateDTO = new UserRequestDTO();
        updateDTO.setName("Updated");
        updateDTO.setSurname("User");

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        doAnswer(inv -> {
            user.setName(updateDTO.getName());
            user.setSurname(updateDTO.getSurname());
            return null;
        }).when(userMapper).updateFromDto(updateDTO, user);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toDTO(any(User.class))).thenAnswer(inv -> {
            User saved = inv.getArgument(0);
            UserResponseDTO dto = new UserResponseDTO();
            dto.setId(saved.getId());
            dto.setName(saved.getName());
            dto.setSurname(saved.getSurname());
            return dto;
        });

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
        doNothing().when(userMapper).updateFromDto(updateDTO, user);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toDTO(any(User.class))).thenReturn(responseDTO);

        UserResponseDTO result = userService.update(2L, updateDTO);

        assertThat(result).isNotNull();
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
