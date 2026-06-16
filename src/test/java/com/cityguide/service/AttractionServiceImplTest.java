package com.cityguide.service;

import com.cityguide.dto.*;
import com.cityguide.entity.Attraction;
import com.cityguide.entity.Category;
import com.cityguide.entity.Review;
import com.cityguide.exception.ResourceNotFoundException;
import com.cityguide.mapper.AttractionMapper;
import com.cityguide.mapper.ReviewMapper;
import com.cityguide.repository.AttractionRepository;
import com.cityguide.repository.ReviewRepository;
import com.cityguide.repository.projection.AttractionNearbyProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttractionServiceImplTest {

    @Mock AttractionRepository attractionRepository;
    @Mock ReviewRepository reviewRepository;
    @Mock AttractionMapper attractionMapper;
    @Mock ReviewMapper reviewMapper;

    @InjectMocks AttractionServiceImpl service;

    private Attraction hermitage;
    private AttractionDto hermitageDto;

    @BeforeEach
    void setUp() {
        hermitage = Attraction.builder()
                .id(1L)
                .name("Эрмитаж")
                .category(Category.MUSEUM)
                .latitude(59.9399)
                .longitude(30.3146)
                .build();

        hermitageDto = new AttractionDto();
        hermitageDto.setId(1L);
        hermitageDto.setName("Эрмитаж");
        hermitageDto.setCategory(Category.MUSEUM);
        hermitageDto.setLatitude(59.9399);
        hermitageDto.setLongitude(30.3146);
    }



    @Test
    @DisplayName("getById: возвращает DTO с рейтингом, если достопримечательность найдена")
    void getById_found() {
        when(attractionRepository.findById(1L)).thenReturn(Optional.of(hermitage));
        when(attractionMapper.toDto(hermitage)).thenReturn(hermitageDto);
        when(reviewRepository.findAverageRatingByAttractionId(1L)).thenReturn(Optional.of(4.5));
        when(reviewRepository.countRatingsByAttractionId(1L)).thenReturn(10L);

        AttractionDto result = service.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAverageRating()).isEqualTo(4.5);
        assertThat(result.getRatingCount()).isEqualTo(10L);
    }

    @Test
    @DisplayName("getById: бросает ResourceNotFoundException, если не найдено")
    void getById_notFound() {
        when(attractionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }



    @Test
    @DisplayName("create: сохраняет сущность и возвращает DTO")
    void create_success() {
        AttractionCreateDto createDto = new AttractionCreateDto();
        createDto.setName("Эрмитаж");
        createDto.setCategory(Category.MUSEUM);
        createDto.setLatitude(59.9399);
        createDto.setLongitude(30.3146);

        when(attractionMapper.toEntity(createDto)).thenReturn(hermitage);
        when(attractionRepository.save(hermitage)).thenReturn(hermitage);
        when(attractionMapper.toDto(hermitage)).thenReturn(hermitageDto);
        when(reviewRepository.findAverageRatingByAttractionId(1L)).thenReturn(Optional.empty());
        when(reviewRepository.countRatingsByAttractionId(1L)).thenReturn(0L);

        AttractionDto result = service.create(createDto);

        assertThat(result.getName()).isEqualTo("Эрмитаж");
        verify(attractionRepository).save(hermitage);
    }


    @Test
    @DisplayName("addRating: сохраняет оценку и возвращает ReviewDto")
    void addRating_success() {
        RatingCreateDto dto = new RatingCreateDto();
        dto.setAuthorName("Иван");
        dto.setRating(5);

        Review savedReview = Review.builder()
                .id(1L)
                .attraction(hermitage)
                .authorName("Иван")
                .rating(5)
                .createdAt(LocalDateTime.now())
                .build();

        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setId(1L);
        reviewDto.setRating(5);

        when(attractionRepository.findById(1L)).thenReturn(Optional.of(hermitage));
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);
        when(reviewMapper.toDto(savedReview)).thenReturn(reviewDto);

        ReviewDto result = service.addRating(1L, dto);

        assertThat(result.getRating()).isEqualTo(5);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("addRating: бросает исключение, если достопримечательность не найдена")
    void addRating_attractionNotFound() {
        when(attractionRepository.findById(42L)).thenReturn(Optional.empty());

        RatingCreateDto dto = new RatingCreateDto();
        dto.setAuthorName("Иван");
        dto.setRating(4);

        assertThatThrownBy(() -> service.addRating(42L, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    @DisplayName("addReview: сохраняет отзыв с текстом и оценкой")
    void addReview_success() {
        ReviewCreateDto dto = new ReviewCreateDto();
        dto.setAuthorName("Мария");
        dto.setComment("Отлично!");
        dto.setRating(5);

        Review entity = Review.builder()
                .authorName("Мария").comment("Отлично!").rating(5).build();
        Review saved = Review.builder()
                .id(10L).attraction(hermitage).authorName("Мария")
                .comment("Отлично!").rating(5).createdAt(LocalDateTime.now()).build();

        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setId(10L);
        reviewDto.setComment("Отлично!");

        when(attractionRepository.findById(1L)).thenReturn(Optional.of(hermitage));
        when(reviewMapper.toEntity(dto)).thenReturn(entity);
        when(reviewRepository.save(entity)).thenReturn(saved);
        when(reviewMapper.toDto(saved)).thenReturn(reviewDto);

        ReviewDto result = service.addReview(1L, dto);

        assertThat(result.getComment()).isEqualTo("Отлично!");
        verify(reviewRepository).save(entity);
    }

    @Test
    @DisplayName("addReview: бросает исключение, если нет ни текста, ни оценки")
    void addReview_emptyReview() {
        ReviewCreateDto dto = new ReviewCreateDto();
        dto.setAuthorName("Петр");

        assertThatThrownBy(() -> service.addReview(1L, dto))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(reviewRepository);
    }


    @Test
    @DisplayName("getReviews: возвращает список DTO отзывов")
    void getReviews_success() {
        Review r1 = Review.builder().id(1L).attraction(hermitage)
                .authorName("А").rating(5).createdAt(LocalDateTime.now()).build();
        Review r2 = Review.builder().id(2L).attraction(hermitage)
                .authorName("Б").comment("Хорошо").createdAt(LocalDateTime.now()).build();

        ReviewDto dto1 = new ReviewDto(); dto1.setId(1L);
        ReviewDto dto2 = new ReviewDto(); dto2.setId(2L);

        when(attractionRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.findByAttractionIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(r1, r2));
        when(reviewMapper.toDto(r1)).thenReturn(dto1);
        when(reviewMapper.toDto(r2)).thenReturn(dto2);

        List<ReviewDto> result = service.getReviews(1L);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("getReviews: бросает исключение, если достопримечательность не найдена")
    void getReviews_notFound() {
        when(attractionRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.getReviews(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    @DisplayName("findNearby: возвращает список, отсортированный по расстоянию по умолчанию")
    void findNearby_sortByDistance() {
        AttractionNearbyProjection near = mockProjection(1L, "Близко",  0.5, 4.0, 3L);
        AttractionNearbyProjection far  = mockProjection(2L, "Далеко",  3.0, 5.0, 1L);

        when(attractionRepository.findNearby(anyDouble(), anyDouble(), anyDouble(), any(), any()))
                .thenReturn(List.of(far, near));   // намеренно в обратном порядке

        NearbySearchRequest req = new NearbySearchRequest();
        req.setLat(59.93); req.setLon(30.31); req.setRadius(10.0);
        req.setSortBy("distance"); req.setMaxResults(10);

        List<AttractionDto> result = service.findNearby(req);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDistanceKm()).isLessThan(result.get(1).getDistanceKm());
    }

    @Test
    @DisplayName("findNearby: сортировка по рейтингу (desc)")
    void findNearby_sortByRating() {
        AttractionNearbyProjection low  = mockProjection(1L, "Низкий",  1.0, 2.5, 5L);
        AttractionNearbyProjection high = mockProjection(2L, "Высокий", 2.0, 4.8, 10L);

        when(attractionRepository.findNearby(anyDouble(), anyDouble(), anyDouble(), any(), any()))
                .thenReturn(List.of(low, high));

        NearbySearchRequest req = new NearbySearchRequest();
        req.setLat(59.93); req.setLon(30.31); req.setRadius(10.0);
        req.setSortBy("rating"); req.setMaxResults(10);

        List<AttractionDto> result = service.findNearby(req);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAverageRating())
                .isGreaterThanOrEqualTo(result.get(1).getAverageRating());
    }

    @Test
    @DisplayName("findNearby: сортировка по имени (asc)")
    void findNearby_sortByName() {
        AttractionNearbyProjection alpha = mockProjection(1L, "Аэропорт", 1.0, 3.0, 2L);
        AttractionNearbyProjection beta  = mockProjection(2L, "Музей",    2.0, 4.0, 5L);

        when(attractionRepository.findNearby(anyDouble(), anyDouble(), anyDouble(), any(), any()))
                .thenReturn(List.of(beta, alpha));   // намеренно в обратном порядке

        NearbySearchRequest req = new NearbySearchRequest();
        req.setLat(59.93); req.setLon(30.31); req.setRadius(10.0);
        req.setSortBy("name"); req.setMaxResults(10);

        List<AttractionDto> result = service.findNearby(req);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isLessThan(result.get(1).getName());
    }

    @Test
    @DisplayName("findNearby: лимит maxResults работает корректно")
    void findNearby_maxResults() {
        List<AttractionNearbyProjection> projections = List.of(
                mockProjection(1L, "A", 0.1, 3.0, 1L),
                mockProjection(2L, "B", 0.2, 4.0, 2L),
                mockProjection(3L, "C", 0.3, 5.0, 3L)
        );

        when(attractionRepository.findNearby(anyDouble(), anyDouble(), anyDouble(), any(), any()))
                .thenReturn(projections);

        NearbySearchRequest req = new NearbySearchRequest();
        req.setLat(0.0); req.setLon(0.0); req.setRadius(100.0);
        req.setSortBy("distance"); req.setMaxResults(2);

        List<AttractionDto> result = service.findNearby(req);

        assertThat(result).hasSize(2);
    }


    private AttractionNearbyProjection mockProjection(Long id, String name,
                                                       Double distance, Double avgRating, Long count) {
        AttractionNearbyProjection p = mock(AttractionNearbyProjection.class, withSettings().lenient());
        when(p.getId()).thenReturn(id);
        when(p.getName()).thenReturn(name);
        when(p.getCategory()).thenReturn("MUSEUM");
        when(p.getLatitude()).thenReturn(59.93);
        when(p.getLongitude()).thenReturn(30.31);
        when(p.getDescription()).thenReturn(null);
        when(p.getAddress()).thenReturn(null);
        when(p.getDistanceKm()).thenReturn(distance);
        when(p.getAverageRating()).thenReturn(avgRating);
        when(p.getRatingCount()).thenReturn(count);
        return p;
    }
}
