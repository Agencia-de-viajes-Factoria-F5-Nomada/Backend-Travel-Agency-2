package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.service.CloudinaryService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CloudinaryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJson
class CloudinaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CloudinaryService cloudinaryService;



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
