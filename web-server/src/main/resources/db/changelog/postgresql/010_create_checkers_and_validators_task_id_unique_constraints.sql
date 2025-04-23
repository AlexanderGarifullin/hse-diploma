-- liquibase formatted sql

-- changeset alexandergarifullin:1
DROP INDEX IF EXISTS idx_validators_task_id;

-- changeset alexandergarifullin:2
DROP INDEX IF EXISTS idx_checkers_task_id;

-- changeset alexandergarifullin:3
CREATE UNIQUE INDEX IF NOT EXISTS uq_checkers_task_id
  ON diplom.checkers(task_id);

-- changeset alexandergarifullin:4
CREATE UNIQUE INDEX IF NOT EXISTS uq_validators_task_id
  ON diplom.validators(task_id);
