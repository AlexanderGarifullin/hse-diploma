-- liquibase formatted sql

-- changeset alexandergarifullin:1
CREATE TABLE IF NOT EXISTS diplom.tasks (
  id           BIGSERIAL PRIMARY KEY,
  owner_id     BIGINT    NOT NULL,
  name         TEXT      NOT NULL,
  legend       TEXT      NOT NULL,
  input        TEXT      NOT NULL,
  output       TEXT      NOT NULL,
  time_limit   INTEGER   NOT NULL,
  memery_limit INTEGER   NOT NULL,
  CONSTRAINT fk_tasks_owner
    FOREIGN KEY (owner_id)
    REFERENCES diplom.users(id)
    ON DELETE CASCADE
);

-- changeset alexandergarifullin:2
CREATE INDEX IF NOT EXISTS idx_tasks_owner_id
  ON diplom.tasks(owner_id);
