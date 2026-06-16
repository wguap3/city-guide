package com.cityguide.controller;

import com.cityguide.dto.*;
import com.cityguide.service.AttractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attractions")
@RequiredArgsConstructor
@Tag(name = "Attractions", description = "Управление достопримечательностями и отзывами")
public class AttractionController {

    private final AttractionService attractionService;

    // ------------------------------------------------------------------
    // CRUD достопримечательностей
    // ------------------------------------------------------------------

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать достопримечательность")
    public AttractionDto create(@Valid @RequestBody AttractionCreateDto dto) {
        return attractionService.create(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить информацию о достопримечательности по id (со средней оценкой)")
    public AttractionDto getById(@PathVariable Long id) {
        return attractionService.getById(id);
    }

    // ------------------------------------------------------------------
    // Поиск поблизости
    // ------------------------------------------------------------------

    @GetMapping("/nearby")
    @Operation(summary = "Найти достопримечательности в радиусе от пользователя",
               description = """
                       Параметры:
                       - lat, lon — координаты пользователя (обязательные)
                       - radius — радиус в км (обязательный)
                       - category — фильтр по категории (необязательный)
                       - minRating — минимальная средняя оценка (необязательный)
                       - maxResults — лимит результатов (по умолчанию 10)
                       - sortBy — distance | rating | name (по умолчанию distance)
                       """)
    public List<AttractionDto> findNearby(@Valid NearbySearchRequest request) {
        return attractionService.findNearby(request);
    }

    // ------------------------------------------------------------------
    // Оценки
    // ------------------------------------------------------------------

    @PostMapping("/{id}/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Выставить оценку достопримечательности (1–5)")
    public ReviewDto addRating(@PathVariable Long id,
                               @Valid @RequestBody RatingCreateDto dto) {
        return attractionService.addRating(id, dto);
    }

    // ------------------------------------------------------------------
    // Отзывы
    // ------------------------------------------------------------------

    @PostMapping("/{id}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Написать отзыв о достопримечательности")
    public ReviewDto addReview(@PathVariable Long id,
                               @Valid @RequestBody ReviewCreateDto dto) {
        return attractionService.addReview(id, dto);
    }

    @GetMapping("/{id}/reviews")
    @Operation(summary = "Получить все отзывы о достопримечательности")
    public List<ReviewDto> getReviews(@PathVariable Long id) {
        return attractionService.getReviews(id);
    }
}
