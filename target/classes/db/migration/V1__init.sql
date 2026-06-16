
CREATE TABLE IF NOT EXISTS attractions (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255)  NOT NULL,
    category    VARCHAR(50)   NOT NULL,
    latitude    DOUBLE PRECISION NOT NULL,
    longitude   DOUBLE PRECISION NOT NULL,
    description VARCHAR(1000),
    address     VARCHAR(255)
);

CREATE INDEX idx_attractions_category ON attractions (category);
CREATE INDEX idx_attractions_lat_lon  ON attractions (latitude, longitude);

CREATE TABLE IF NOT EXISTS reviews (
    id             BIGSERIAL PRIMARY KEY,
    attraction_id  BIGINT NOT NULL REFERENCES attractions (id) ON DELETE CASCADE,
    author_name    VARCHAR(100) NOT NULL,
    rating         SMALLINT CHECK (rating BETWEEN 1 AND 5),
    comment        VARCHAR(2000),
    created_at     TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT chk_review_not_empty CHECK (rating IS NOT NULL OR comment IS NOT NULL)
);

CREATE INDEX idx_reviews_attraction_id ON reviews (attraction_id);
