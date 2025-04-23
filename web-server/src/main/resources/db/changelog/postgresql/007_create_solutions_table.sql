-- liquibase formatted sql

-- changeset alexandergarifullin:1
CREATE TABLE IF NOT EXISTS diplom.solutions (
  id              BIGSERIAL PRIMARY KEY,
  name            TEXT      NOT NULL,
  code            TEXT      NOT NULL,
  task_id         BIGINT    NOT NULL,
  type_id         BIGINT    NOT NULL,
  language_id     BIGINT    NOT NULL,
  CONSTRAINT fk_solutions_task
    FOREIGN KEY (task_id)
    REFERENCES diplom.tasks(id)
    ON DELETE CASCADE,

   CONSTRAINT fk_solutions_type
    FOREIGN KEY (type_id)
    REFERENCES diplom.solutions_types(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_solutions_language
    FOREIGN KEY (language_id)
    REFERENCES diplom.programming_languages(id)
    ON DELETE CASCADE
);

-- changeset alexandergarifullin:2
CREATE UNIQUE INDEX IF NOT EXISTS idx_solutions_task_id_code
  ON diplom.solutions(task_id, code);

-- changeset alexandergarifullin:3
CREATE INDEX IF NOT EXISTS idx_solutions_task_id ON diplom.solutions(task_id);

-- changeset alexandergarifullin:4
CREATE INDEX IF NOT EXISTS idx_solutions_type_id ON diplom.solutions(type_id);

-- changeset alexandergarifullin:5
CREATE INDEX IF NOT EXISTS idx_solutions_language_id ON diplom.solutions(language_id);