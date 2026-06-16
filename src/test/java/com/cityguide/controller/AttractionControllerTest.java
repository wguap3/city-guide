package com.cityguide.controller;

import com.cityguide.dto.*;
import com.cityguide.entity.Category;
import com.cityguide.exception.GlobalExceptionHandler;
import com.cityguide.exception.ResourceNotFoundException;
import com.cityguide.service.AttractionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttractionController.class)
@Import(GlobalExceptionHandler.class)
class AttractionControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean AttractionService attractionService;


    @Test
    @DisplayName("GET /api/attractions/{id} — 200 OK с телом")
    void getById_ok() throws Exception {
        AttractionDto dto = buildAttractionDto(1L, "Эрмитаж", 4.7, 42L);
        when(attractionService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/attractions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Эрмитаж"))
                .andExpect(jsonPath("$.averageRating").value(4.7))
                .andExpect(jsonPath("$.ratingCount").value(42));
    }

    @Test
    @DisplayName("GET /api/attractions/{id} — 404, если не найдено")
    void getById_notFound() throws Exception {
        when(attractionService.getById(99L))
                .thenThrow(ResourceNotFoundException.attraction(99L));

        mockMvc.perform(get("/api/attractions/99"))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("POST /api/attractions — 201 Created с телом")
    void create_ok() throws Exception {
        AttractionCreateDto req = new AttractionCreateDto();
        req.setName("Эрмитаж");
        req.setCategory(Category.MUSEUM);
        req.setLatitude(59.9399);
        req.setLongitude(30.3146);

        AttractionDto resp = buildAttractionDto(1L, "Эрмитаж", null, 0L);
        when(attractionService.create(any(AttractionCreateDto.class))).thenReturn(resp);

        mockMvc.perform(post("/api/attractions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /api/attractions — 400 при отсутствии обязательных полей")
    void create_validationError() throws Exception {
        AttractionCreateDto req = new AttractionCreateDto();
        // name, category, lat, lon не заданы

        mockMvc.perform(post("/api/attractions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/attractions/nearby — 200 OK со списком")
    void findNearby_ok() throws Exception {
        AttractionDto a1 = buildAttractionDto(1L, "Эрмитаж", 4.7, 42L);
        a1.setDistanceKm(0.5);
        when(attractionService.findNearby(any(NearbySearchRequest.class)))
                .thenReturn(List.of(a1));

        mockMvc.perform(get("/api/attractions/nearby")
                        .param("lat", "59.93")
                        .param("lon", "30.31")
                        .param("radius", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Эрмитаж"))
                .andExpect(jsonPath("$[0].distanceKm").value(0.5));
    }

    @Test
    @DisplayName("GET /api/attractions/nearby — 400 без обязательных параметров")
    void findNearby_missingParams() throws Exception {
        mockMvc.perform(get("/api/attractions/nearby"))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("POST /api/attractions/{id}/ratings — 201 Created")
    void addRating_ok() throws Exception {
        RatingCreateDto req = new RatingCreateDto();
        req.setAuthorName("Иван");
        req.setRating(5);

        ReviewDto resp = new ReviewDto();
        resp.setId(1L); resp.setRating(5); resp.setAuthorName("Иван");
        resp.setAttractionId(1L); resp.setCreatedAt(LocalDateTime.now());

        when(attractionService.addRating(eq(1L), any(RatingCreateDto.class))).thenReturn(resp);

        mockMvc.perform(post("/api/attractions/1/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    @DisplayName("POST /api/attractions/{id}/ratings — 400 при оценке вне диапазона")
    void addRating_invalidRating() throws Exception {
        RatingCreateDto req = new RatingCreateDto();
        req.setAuthorName("Иван");
        req.setRating(10); // недопустимое значение

        mockMvc.perform(post("/api/attractions/1/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("POST /api/attractions/{id}/reviews — 201 Created")
    void addReview_ok() throws Exception {
        ReviewCreateDto req = new ReviewCreateDto();
        req.setAuthorName("Мария");
        req.setComment("Отлично!");
        req.setRating(5);

        ReviewDto resp = new ReviewDto();
        resp.setId(2L); resp.setAuthorName("Мария");
        resp.setComment("Отлично!"); resp.setRating(5);
        resp.setAttractionId(1L); resp.setCreatedAt(LocalDateTime.now());

        when(attractionService.addReview(eq(1L), any(ReviewCreateDto.class))).thenReturn(resp);

        mockMvc.perform(post("/api/attractions/1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comment").value("Отлично!"));
    }


    @Test
    @DisplayName("GET /api/attractions/{id}/reviews — 200 OK со списком")
    void getReviews_ok() throws Exception {
        ReviewDto r1 = new ReviewDto(); r1.setId(1L); r1.setAuthorName("А"); r1.setRating(5);
        ReviewDto r2 = new ReviewDto(); r2.setId(2L); r2.setAuthorName("Б"); r2.setComment("Хорошо");

        when(attractionService.getReviews(1L)).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/attractions/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/attractions/{id}/reviews — 404, если не найдено")
    void getReviews_notFound() throws Exception {
        when(attractionService.getReviews(99L))
                .thenThrow(ResourceNotFoundException.attraction(99L));

        mockMvc.perform(get("/api/attractions/99/reviews"))
                .andExpect(status().isNotFound());
    }


    private AttractionDto buildAttractionDto(Long id, String name,
                                              Double avgRating, Long ratingCount) {
        AttractionDto dto = new AttractionDto();
        dto.setId(id);
        dto.setName(name);
        dto.setCategory(Category.MUSEUM);
        dto.setLatitude(59.9399);
        dto.setLongitude(30.3146);
        dto.setAverageRating(avgRating);
        dto.setRatingCount(ratingCount);
        return dto;
    }
}
