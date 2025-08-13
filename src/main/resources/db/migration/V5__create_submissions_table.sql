CREATE TABLE tb_submissions (
    id SERIAL PRIMARY KEY,
    challenge_id INTEGER NOT NULL,
    user_id UUID NOT NULL,
    expression VARCHAR(255) NOT NULL,
    status VARCHAR(16) NOT NULL,
    submitted_at TIMESTAMP NOT NULL,
    CONSTRAINT submission_challenge_fk FOREIGN KEY (challenge_id) REFERENCES tb_challenges(id),
    CONSTRAINT submission_user_fk FOREIGN KEY (user_id) REFERENCES tb_users(id)
)