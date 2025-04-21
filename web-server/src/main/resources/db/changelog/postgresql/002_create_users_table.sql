-- liquibase formatted sql

-- changeset alexandergarifullin:1
CREATE TABLE IF NOT EXISTS diplom.users (
  id        BIGSERIAL   PRIMARY KEY,
  username  TEXT        UNIQUE NOT NULL,
  password  TEXT        NOT NULL
);
