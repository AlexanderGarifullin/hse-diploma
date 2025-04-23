-- liquibase formatted sql

-- changeset alexandergarifullin:1
CREATE TABLE IF NOT EXISTS diplom.programming_languages (
  id           BIGSERIAL    PRIMARY KEY,
  language     TEXT         UNIQUE NOT NULL
);
