# City Guide API

REST API путеводителя по городу — достопримечательности, оценки и отзывы.

## Стек

- Java 17, Spring Boot 3.3
- PostgreSQL 16
- Liquibase (миграции)
- MapStruct (маппинг)
- Lombok
- SpringDoc / Swagger UI
- Docker + Docker Compose

---

## Быстрый старт (Docker Compose)

```bash
# 1. Клонировать / распаковать проект
cd city-guide

# 2. Собрать и запустить всё одной командой
docker compose up --build
```

Сервис поднимется на http://localhost:8080  
Swagger UI: http://localhost:8080/swagger-ui.html

---

## Запуск без Docker (локально)

### Предусловия

- Java 17+
- Maven 3.8+
- PostgreSQL (создайте БД `cityguide`, пользователь `cityguide`, пароль `cityguide`)

### Команды

```bash
# Запуск (Liquibase применит миграции автоматически)
mvn spring-boot:run

# Только тесты
mvn test

# Сборка jar
mvn package -DskipTests
java -jar target/city-guide-1.0.0.jar
```

### Переменные окружения (опционально)

| Переменная    | По умолчанию  | Описание          |
|---------------|---------------|-------------------|
| `DB_HOST`     | `localhost`   | Хост PostgreSQL   |
| `DB_PORT`     | `5432`        | Порт PostgreSQL   |
| `DB_NAME`     | `cityguide`   | Имя БД            |
| `DB_USER`     | `cityguide`   | Пользователь БД   |
| `DB_PASSWORD` | `cityguide`   | Пароль БД         |

---

## Миграции БД (Liquibase)

Чейнджлоги находятся в `src/main/resources/db/changelog/`.

| Файл                                       | Описание                         |
|--------------------------------------------|----------------------------------|
| `db.changelog-master.yaml`                 | Мастер-файл, подключает остальные |
| `changesets/001-init-schema.yaml`          | Создание таблиц и индексов       |
| `changesets/002-seed-data.yaml`            | Тестовые данные (Петербург)      |

---

## Эндпоинты API

### Достопримечательности

| Метод | URL                              | Описание                                             |
|-------|----------------------------------|------------------------------------------------------|
| POST  | `/api/attractions`               | Создать достопримечательность                        |
| GET   | `/api/attractions/{id}`          | Получить по id (со средней оценкой)                  |
| GET   | `/api/attractions/nearby`        | Найти в радиусе с фильтрами и сортировкой            |

#### GET /api/attractions/nearby — параметры запроса

| Параметр     | Тип     | Обязательный | По умолчанию | Описание                                     |
|--------------|---------|:------------:|:------------:|----------------------------------------------|
| `lat`        | double  | ✅           | —            | Широта пользователя                          |
| `lon`        | double  | ✅           | —            | Долгота пользователя                         |
| `radius`     | double  | ✅           | —            | Радиус поиска в км                           |
| `category`   | string  | ❌           | —            | MUSEUM, PARK, MONUMENT, TEMPLE, GALLERY, … |
| `minRating`  | double  | ❌           | —            | Минимальная средняя оценка (1–5)             |
| `maxResults` | int     | ❌           | 10           | Максимум результатов (1–100)                 |
| `sortBy`     | string  | ❌           | `distance`   | `distance` / `rating` / `name`              |

### Оценки

| Метод | URL                              | Описание                            |
|-------|----------------------------------|-------------------------------------|
| POST  | `/api/attractions/{id}/ratings`  | Выставить оценку 1–5                |

Тело запроса:
```json
{ "authorName": "Иван Иванов", "rating": 5 }
```

### Отзывы

| Метод | URL                              | Описание                            |
|-------|----------------------------------|-------------------------------------|
| POST  | `/api/attractions/{id}/reviews`  | Написать отзыв (текст и/или оценка) |
| GET   | `/api/attractions/{id}/reviews`  | Получить все отзывы                 |

Тело запроса для создания отзыва:
```json
{
  "authorName": "Мария Петрова",
  "comment": "Прекрасное место, обязательно вернусь!",
  "rating": 5
}
```

---

## Примеры запросов (curl)

```bash
# Создать достопримечательность
curl -X POST http://localhost:8080/api/attractions \
  -H "Content-Type: application/json" \
  -d '{"name":"Эрмитаж","category":"MUSEUM","latitude":59.9399,"longitude":30.3146,"address":"Дворцовая пл., 2"}'

# Найти музеи в радиусе 3 км с рейтингом >= 4, отсортированные по рейтингу
curl "http://localhost:8080/api/attractions/nearby?lat=59.93&lon=30.31&radius=3&category=MUSEUM&minRating=4&sortBy=rating"

# Выставить оценку
curl -X POST http://localhost:8080/api/attractions/1/ratings \
  -H "Content-Type: application/json" \
  -d '{"authorName":"Иван","rating":5}'

# Написать отзыв
curl -X POST http://localhost:8080/api/attractions/1/reviews \
  -H "Content-Type: application/json" \
  -d '{"authorName":"Мария","comment":"Потрясающе!","rating":5}'

# Получить отзывы
curl http://localhost:8080/api/attractions/1/reviews
```

---

## Тестирование и покрытие

```bash
mvn test
```

Покрытие проверено с помощью **JaCoCo 0.8.12** (`mvn test` → `target/site/jacoco/index.html`).

| Пакет                      | Инструкции | Ветки |
|----------------------------|:----------:|:-----:|
| `exception`                | 100 %      | 100 % |
| `controller`               | 100 %      | n/a   |
| `config`                   | 100 %      | n/a   |
| `mapper`                   | 92 %       | 64 %  |
| `service`                  | 94 %       | 65 %  |
| `entity`                   | 88 %       | 50 %  |
| **Итого (бизнес-логика)**  | **92 %**   | **65 %** |

> DTO и главный класс исключены из отчёта — там нет бизнес-логики (только Lombok-генерация и точка входа).

---

## Категории

`MUSEUM`, `PARK`, `MONUMENT`, `TEMPLE`, `GALLERY`, `THEATER`, `RESTAURANT`, `SHOPPING`, `ENTERTAINMENT`, `OTHER`
