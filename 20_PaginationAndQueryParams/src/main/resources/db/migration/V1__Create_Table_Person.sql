CREATE TABLE IF NOT EXISTS person (
    id BIGSERIAL NOT NULL,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    address VARCHAR(100) NOT NULL,
    gender VARCHAR(6) NOT NULL,
    PRIMARY KEY (id)
);
