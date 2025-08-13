CREATE TABLE tb_challenges (
    id SERIAL PRIMARY KEY,
    title VARCHAR(64),
    description TEXT NOT NULL,
    difficulty VARCHAR(64) NOT NULL
)