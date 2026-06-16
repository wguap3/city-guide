package com.cityguide.exception;

import com.cityguide.controller.AttractionController;
import com.cityguide.service.AttractionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttractionController.class)
@Import(GlobalExceptionHandler.class)
class ExceptionHandlerTest {

    @Autowired MockMvc mockMvc;
    @MockBean AttractionService attractionService;

    @Test
    @DisplayName("ResourceNotFoundException → 404")
    void resourceNotFound_returns404() throws Exception {
        when(attractionService.getById(anyLong()))
                .thenThrow(ResourceNotFoundException.attraction(42L));

        mockMvc.perform(get("/api/attractions/42"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("IllegalArgumentException → 400")
    void illegalArgument_returns400() throws Exception {
        when(attractionService.addReview(anyLong(), any()))
                .thenThrow(new IllegalArgumentException("Отзыв пуст"));

        mockMvc.perform(post("/api/attractions/1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"authorName\":\"X\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Validation error → 400 с полем errors")
    void validationError_returns400WithErrors() throws Exception {
        mockMvc.perform(post("/api/attractions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("ResourceNotFoundException.attraction — сообщение содержит id")
    void resourceNotFoundException_message() {
        ResourceNotFoundException ex = ResourceNotFoundException.attraction(99L);
        assertThat(ex.getMessage()).contains("99");
    }
}
