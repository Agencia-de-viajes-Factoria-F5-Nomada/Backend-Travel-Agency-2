package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.DriverRequestDTO;
import com.inditex.g1_agencia_viajes.dto.DriverResponseDTO;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.mapper.DriverMapper;
import com.inditex.g1_agencia_viajes.model.Driver;
import com.inditex.g1_agencia_viajes.repository.BusRepository;
import com.inditex.g1_agencia_viajes.repository.DriverRepository;
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
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private DriverMapper driverMapper;

    private DriverService driverService;

    private Driver driver;
    private DriverRequestDTO requestDTO;
    private DriverResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        driverService = new DriverService(driverRepository, busRepository, driverMapper);

        driver = new Driver();
        driver.setId(1L);
        driver.setName("Driver Test");
        driver.setPhone("123456789");
        driver.setLicenceActive(true);
        driver.setImageUrl("http://image.com/driver.jpg");

        requestDTO = new DriverRequestDTO();
        requestDTO.setName("Driver Test");
        requestDTO.setPhone("123456789");
        requestDTO.setLicenceActive(true);
        requestDTO.setImageUrl("http://image.com/driver.jpg");

        responseDTO = new DriverResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Driver Test");
        responseDTO.setPhone("123456789");
        responseDTO.setLicenceActive(true);
        responseDTO.setImageUrl("http://image.com/driver.jpg");
    }

    @Test
    void create_ShouldReturnDriverResponseDTO() {
        when(driverMapper.toEntity(requestDTO)).thenReturn(driver);
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toDTO(driver)).thenReturn(responseDTO);

        DriverResponseDTO result = driverService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Driver Test");
    }

    @Test
    void getAll_ShouldReturnListOfDrivers() {
        when(driverRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(driver)));
        when(driverMapper.toDTO(driver)).thenReturn(responseDTO);

        Page<DriverResponseDTO> result = driverService.getAll(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Driver Test");
    }

    @Test
    void getById_ShouldReturnDriver() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverMapper.toDTO(driver)).thenReturn(responseDTO);

        DriverResponseDTO result = driverService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getById_ShouldThrowResourceNotFoundException() {
        when(driverRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getActive_ShouldReturnActiveDrivers() {
        when(driverRepository.findByLicenceActive(eq(true), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(driver)));
        when(driverMapper.toDTO(driver)).thenReturn(responseDTO);

        Page<DriverResponseDTO> result = driverService.getActive(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void update_ShouldReturnUpdatedDriver() {
        DriverRequestDTO updateDTO = new DriverRequestDTO();
        updateDTO.setName("Updated Driver");
        updateDTO.setPhone("987654321");

        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        DriverResponseDTO updatedResponse = new DriverResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setName("Updated Driver");
        updatedResponse.setPhone("987654321");
        when(driverMapper.toDTO(any(Driver.class))).thenReturn(updatedResponse);

        DriverResponseDTO result = driverService.update(1L, updateDTO);

        assertThat(result.getName()).isEqualTo("Updated Driver");
        assertThat(result.getPhone()).isEqualTo("987654321");
    }

    @Test
    void update_ShouldThrowResourceNotFoundException() {
        when(driverRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.update(99L, new DriverRequestDTO()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_ShouldDeactivateDriver() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));

        driverService.delete(1L);

        assertThat(driver.getActive()).isFalse();
        verify(driverRepository).save(driver);
    }

    @Test
    void delete_ShouldThrowResourceNotFoundException() {
        when(driverRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
