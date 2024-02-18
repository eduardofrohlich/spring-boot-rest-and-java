CREATE TABLE IF NOT EXISTS users
(
    id                      BIGSERIAL NOT NULL,
    user_name               VARCHAR(255) DEFAULT NULL,
    full_name               VARCHAR(255) DEFAULT NULL,
    password                VARCHAR(255) DEFAULT NULL,
    account_non_expired     BOOLEAN DEFAULT NULL,
    account_non_locked      BOOLEAN DEFAULT NULL,
    credentials_non_expired BOOLEAN DEFAULT NULL,
    enabled                 BOOLEAN DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE (user_name)
);