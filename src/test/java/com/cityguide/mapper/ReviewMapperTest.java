package com.cityguide.mapper;

import com.cityguide.dto.ReviewCreateDto;
import com.cityguide.dto.ReviewDto;
import com.cityguide.entity.Attraction;
import com.cityguide.entity.Category;
import com.cityguide.entity.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ReviewMapperTest {

    @Autowired
    ReviewMapper mapper;

    @Test
    @DisplayName("toDto: все поля маппятся корректно")
    void toDto_allFields() {
        Attraction attraction = Attraction.builder()
                .id(5L).name("Эрмитаж").category(Category.MUSEUM)
                .latitude(59.9).longitude(30.3).build();

        LocalDateTime now = LocalDateTime.now();
        Review review = Review.builder()
                .id(1L)
                .attraction(attraction)
                .authorName("Иван")
                .rating(5)
                .comment("Отлично!")
                .createdAt(now)
                .build();

        ReviewDto dto = mapper.toDto(review);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getAttractionId()).isEqualTo(5L);
        assertThat(dto.getAuthorName()).isEqualTo("Иван");
        assertThat(dto.getRating()).isEqualTo(5);
        assertThat(dto.getComment()).isEqualTo("Отлично!");
        assertThat(dto.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("toDto: review без оценки — rating null")
    void toDto_noRating() {
        Attraction attraction = Attraction.builder()
                .id(1L).name("X").category(Category.PARK)
                .latitude(0.0).longitude(0.0).build();

        Review review = Review.builder()
                .id(2L).attraction(attraction)
                .authorName("Мария").comment("Хорошее место")
                .createdAt(LocalDateTime.now()).build();

        ReviewDto dto = mapper.toDto(review);

        assertThat(dto.getRating()).isNull();
        assertThat(dto.getComment()).isEqualTo("Хорошее место");
    }

    @Test
    @DisplayName("toEntity: поля из ReviewCreateDto маппятся, attraction и createdAt игнорируются")
    void toEntity_fromCreateDto() {
        ReviewCreateDto dto = new ReviewCreateDto();
        dto.setAuthorName("Пётр");
        dto.setComment("Замечательно");
        dto.setRating(4);

        Review entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getAttraction()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getAuthorName()).isEqualTo("Пётр");
        assertThat(entity.getComment()).isEqualTo("Замечательно");
        assertThat(entity.getRating()).isEqualTo(4);
    }

    @Test
    @DisplayName("toDto: null review возвращает null")
    void toDto_null() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    @DisplayName("toEntity: null dto возвращает null")
    void toEntity_null() {
        assertThat(mapper.toEntity(null)).isNull();
    }
}
