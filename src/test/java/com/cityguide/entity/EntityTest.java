package com.cityguide.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    @DisplayName("Attraction.builder — создаёт объект со всеми полями")
    void attraction_builder() {
        Attraction a = Attraction.builder()
                .id(1L)
                .name("Эрмитаж")
                .category(Category.MUSEUM)
                .latitude(59.9399)
                .longitude(30.3146)
                .description("Описание")
                .address("Дворцовая пл., 2")
                .build();

        assertThat(a.getId()).isEqualTo(1L);
        assertThat(a.getName()).isEqualTo("Эрмитаж");
        assertThat(a.getCategory()).isEqualTo(Category.MUSEUM);
        assertThat(a.getLatitude()).isEqualTo(59.9399);
        assertThat(a.getLongitude()).isEqualTo(30.3146);
        assertThat(a.getDescription()).isEqualTo("Описание");
        assertThat(a.getAddress()).isEqualTo("Дворцовая пл., 2");
        assertThat(a.getReviews()).isEmpty();
    }

    @Test
    @DisplayName("Attraction.noArgsConstructor + setters работают")
    void attraction_setters() {
        Attraction a = new Attraction();
        a.setId(2L);
        a.setName("Русский музей");
        a.setCategory(Category.GALLERY);
        a.setLatitude(59.93);
        a.setLongitude(30.33);
        a.setDescription("Музей");
        a.setAddress("Ул. 1");
        a.setReviews(new ArrayList<>());

        assertThat(a.getId()).isEqualTo(2L);
        assertThat(a.getName()).isEqualTo("Русский музей");
        assertThat(a.getCategory()).isEqualTo(Category.GALLERY);
        assertThat(a.getReviews()).isEmpty();
    }

    @Test
    @DisplayName("Attraction.allArgsConstructor работает")
    void attraction_allArgsConstructor() {
        Attraction a = new Attraction(3L, "Парк", Category.PARK,
                60.0, 30.0, "Парк отдыха", "ул. Парковая", new ArrayList<>());
        assertThat(a.getId()).isEqualTo(3L);
        assertThat(a.getCategory()).isEqualTo(Category.PARK);
    }


    @Test
    @DisplayName("Review.builder — создаёт объект со всеми полями")
    void review_builder() {
        Attraction attraction = Attraction.builder()
                .id(1L).name("X").category(Category.PARK)
                .latitude(0.0).longitude(0.0).build();

        LocalDateTime now = LocalDateTime.now();
        Review r = Review.builder()
                .id(10L)
                .attraction(attraction)
                .authorName("Иван")
                .rating(5)
                .comment("Отлично")
                .createdAt(now)
                .build();

        assertThat(r.getId()).isEqualTo(10L);
        assertThat(r.getAttraction()).isEqualTo(attraction);
        assertThat(r.getAuthorName()).isEqualTo("Иван");
        assertThat(r.getRating()).isEqualTo(5);
        assertThat(r.getComment()).isEqualTo("Отлично");
        assertThat(r.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Review.noArgsConstructor + setters работают")
    void review_setters() {
        Review r = new Review();
        r.setId(1L);
        r.setAuthorName("Мария");
        r.setRating(4);
        r.setComment("Хорошо");
        r.setCreatedAt(LocalDateTime.now());

        assertThat(r.getAuthorName()).isEqualTo("Мария");
        assertThat(r.getRating()).isEqualTo(4);
    }

    @Test
    @DisplayName("Review.allArgsConstructor работает")
    void review_allArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Review r = new Review(1L, null, "Алексей", 3, "Нормально", now);
        assertThat(r.getId()).isEqualTo(1L);
        assertThat(r.getAuthorName()).isEqualTo("Алексей");
    }

    @Test
    @DisplayName("Category: все значения присутствуют")
    void category_values() {
        assertThat(Category.values()).containsExactlyInAnyOrder(
                Category.MUSEUM, Category.PARK, Category.MONUMENT,
                Category.TEMPLE, Category.GALLERY, Category.THEATER,
                Category.RESTAURANT, Category.SHOPPING,
                Category.ENTERTAINMENT, Category.OTHER
        );
    }

    @Test
    @DisplayName("Category.valueOf работает")
    void category_valueOf() {
        assertThat(Category.valueOf("MUSEUM")).isEqualTo(Category.MUSEUM);
        assertThat(Category.valueOf("PARK")).isEqualTo(Category.PARK);
    }
}
