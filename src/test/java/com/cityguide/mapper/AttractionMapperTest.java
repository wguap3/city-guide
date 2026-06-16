package com.cityguide.mapper;

import com.cityguide.dto.AttractionCreateDto;
import com.cityguide.dto.AttractionDto;
import com.cityguide.entity.Attraction;
import com.cityguide.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AttractionMapperTest {

    @Autowired
    AttractionMapper mapper;

    @Test
    @DisplayName("toDto: все поля маппятся корректно")
    void toDto_allFields() {
        Attraction attraction = Attraction.builder()
                .id(1L)
                .name("Эрмитаж")
                .category(Category.MUSEUM)
                .latitude(59.9399)
                .longitude(30.3146)
                .description("Описание")
                .address("Дворцовая пл., 2")
                .build();

        AttractionDto dto = mapper.toDto(attraction);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Эрмитаж");
        assertThat(dto.getCategory()).isEqualTo(Category.MUSEUM);
        assertThat(dto.getLatitude()).isEqualTo(59.9399);
        assertThat(dto.getLongitude()).isEqualTo(30.3146);
        assertThat(dto.getDescription()).isEqualTo("Описание");
        assertThat(dto.getAddress()).isEqualTo("Дворцовая пл., 2");
        assertThat(dto.getAverageRating()).isNull();
        assertThat(dto.getRatingCount()).isNull();
        assertThat(dto.getDistanceKm()).isNull();
    }

    @Test
    @DisplayName("toEntity: все поля маппятся корректно")
    void toEntity_allFields() {
        AttractionCreateDto dto = new AttractionCreateDto();
        dto.setName("Русский музей");
        dto.setCategory(Category.MUSEUM);
        dto.setLatitude(59.9384);
        dto.setLongitude(30.3320);
        dto.setDescription("Описание музея");
        dto.setAddress("Инженерная ул., 4");

        Attraction entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("Русский музей");
        assertThat(entity.getCategory()).isEqualTo(Category.MUSEUM);
        assertThat(entity.getLatitude()).isEqualTo(59.9384);
        assertThat(entity.getLongitude()).isEqualTo(30.3320);
        assertThat(entity.getDescription()).isEqualTo("Описание музея");
        assertThat(entity.getAddress()).isEqualTo("Инженерная ул., 4");
        assertThat(entity.getReviews()).isEmpty();
    }

    @Test
    @DisplayName("updateFromDto: обновляет только непустые поля")
    void updateFromDto_partialUpdate() {
        Attraction existing = Attraction.builder()
                .id(1L)
                .name("Старое название")
                .category(Category.PARK)
                .latitude(59.0)
                .longitude(30.0)
                .description("Старое описание")
                .address("Старый адрес")
                .build();

        AttractionCreateDto patch = new AttractionCreateDto();
        patch.setName("Новое название");
        patch.setCategory(Category.MUSEUM);
        patch.setLatitude(59.9399);
        patch.setLongitude(30.3146);

        mapper.updateFromDto(patch, existing);

        assertThat(existing.getId()).isEqualTo(1L); // id не меняется
        assertThat(existing.getName()).isEqualTo("Новое название");
        assertThat(existing.getCategory()).isEqualTo(Category.MUSEUM);
        assertThat(existing.getLatitude()).isEqualTo(59.9399);
    }

    @Test
    @DisplayName("toDto: null entity возвращает null")
    void toDto_null() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    @DisplayName("toEntity: null dto возвращает null")
    void toEntity_null() {
        assertThat(mapper.toEntity(null)).isNull();
    }
}
