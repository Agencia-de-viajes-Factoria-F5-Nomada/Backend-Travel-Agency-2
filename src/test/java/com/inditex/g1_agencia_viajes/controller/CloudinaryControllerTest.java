package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.service.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private CloudinaryController cloudinaryController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cloudinaryController).build();
    }

    @Test
    void upload_ShouldReturn200() throws Exception {
        when(cloudinaryService.uploadImage(any())).thenReturn("https://res.cloudinary.com/test/image.jpg");

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "fake-image-content".getBytes());

        mockMvc.perform(multipart("/api/images/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://res.cloudinary.com/test/image.jpg"));
    }

    @Test
    void delete_ShouldReturn200() throws Exception {
        doNothing().when(cloudinaryService).deleteImage("test-public-id");

        mockMvc.perform(delete("/api/images/delete/test-public-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Imagen eliminada correctamente"));
    }
}
