-- liquibase formatted sql

-- changeset alexandergarifullin:1
INSERT INTO diplom.solutions_types (type) VALUES
  ('MAIN'),
  ('CORRECT'),
  ('INCORRECT')
ON CONFLICT (type) DO NOTHING;

-- changeset alexandergarifullin:2
INSERT INTO diplom.programming_languages (language) VALUES
  ('CPP20')
ON CONFLICT (language) DO NOTHING;

