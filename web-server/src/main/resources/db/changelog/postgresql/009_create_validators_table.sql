-- liquibase formatted sql

-- changeset alexandergarifullin:1
CREATE TABLE IF NOT EXISTS diplom.validators (
  id              BIGSERIAL PRIMARY KEY,
  name            TEXT      NOT NULL,
  code            TEXT      NOT NULL,
  task_id         BIGINT    NOT NULL,
  language_id     BIGINT    NOT NULL,
  CONSTRAINT fk_validators_task
    FOREIGN KEY (task_id)
    REFERENCES diplom.tasks(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_validators_language
    FOREIGN KEY (language_id)
    REFERENCES diplom.programming_languages(id)
    ON DELETE CASCADE
);

-- changeset alexandergarifullin:2
CREATE UNIQUE INDEX IF NOT EXISTS idx_validators_task_id_code
  ON diplom.validators(task_id, code);

-- changeset alexandergarifullin:3
CREATE INDEX IF NOT EXISTS idx_validators_task_id ON diplom.validators(task_id);

-- changeset alexandergarifullin:4
CREATE INDEX IF NOT EXISTS idx_validators_language_id ON diplom.validators(language_id);