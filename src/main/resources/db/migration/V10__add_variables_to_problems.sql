ALTER TABLE tb_problems ADD COLUMN variables TEXT;

UPDATE tb_problems
SET variables = '["x"]'
WHERE type = 'EXPRESSION';

UPDATE tb_problems
SET variables = '[]'
WHERE type = 'NUMERIC';
