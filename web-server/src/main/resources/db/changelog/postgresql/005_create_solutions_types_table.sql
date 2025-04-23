-- liquibase formatted sql

-- changeset alexandergarifullin:1
CREATE TABLE IF NOT EXISTS diplom.solutions_types (
  id          BIGSERIAL     PRIMARY KEY,
  type        TEXT          UNIQUE NOT NULL
);