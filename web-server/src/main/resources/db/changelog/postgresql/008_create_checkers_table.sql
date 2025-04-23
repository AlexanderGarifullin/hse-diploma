-- liquibase formatted sql

-- changeset alexandergarifullin:1
CREATE TABLE IF NOT EXISTS diplom.checkers (
  id              BIGSERIAL PRIMARY KEY,
  name            TEXT      NOT NULL,
  code            TEXT      NOT NULL,
  task_id         BIGINT    NOT NULL,
  language_id     BIGINT    NOT NULL,
  CONSTRAINT fk_checkers_task
    FOREIGN KEY (task_id)
    REFERENCES diplom.tasks(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_checkers_language
    FOREIGN KEY (language_id)
    REFERENCES diplom.programming_languages(id)
    ON DELETE CASCADE
);

-- changeset alexandergarifullin:2
CREATE UNIQUE INDEX IF NOT EXISTS idx_checkers_task_id_code
  ON diplom.checkers(task_id, code);

-- changeset alexandergarifullin:3
CREATE INDEX IF NOT EXISTS idx_checkers_task_id ON diplom.checkers(task_id);

-- changeset alexandergarifullin:4
CREATE INDEX IF NOT EXISTS idx_checkers_language_id ON diplom.checkers(language_id);