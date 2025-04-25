-- liquibase formatted sql

-- changeset alexandergarifullin:1
ALTER TABLE diplom.tasks
ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW();

