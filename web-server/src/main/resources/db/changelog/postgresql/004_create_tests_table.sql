-- liquibase formatted sql

-- changeset alexandergarifullin:1
CREATE TABLE IF NOT EXISTS diplom.tests (
  id           BIGSERIAL PRIMARY KEY,
  task_id      BIGINT    NOT NULL,
  input        TEXT      NOT NULL,
  CONSTRAINT fk_tests_task
    FOREIGN KEY (task_id)
    REFERENCES diplom.tasks(id)
    ON DELETE CASCADE
);

-- changeset alexandergarifullin:2
CREATE UNIQUE INDEX IF NOT EXISTS idx_tests_task_input
  ON diplom.tests(task_id, input);

-- changeset alexandergarifullin:3
CREATE INDEX IF NOT EXISTS idx_tests_task_id
  ON diplom.tests(task_id);