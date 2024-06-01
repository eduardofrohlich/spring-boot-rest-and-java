CREATE TABLE IF NOT EXISTS user_permission
(
    id_user       BIGINT NOT NULL,
    id_permission BIGINT NOT NULL,
    PRIMARY KEY (id_user, id_permission),
    FOREIGN KEY (id_user) REFERENCES users (id),
    FOREIGN KEY (id_permission) REFERENCES permission (id)
);