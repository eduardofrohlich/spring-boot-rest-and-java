﻿CREATE TABLE IF NOT EXISTS permission (
    id BIGSERIAL NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id)
);