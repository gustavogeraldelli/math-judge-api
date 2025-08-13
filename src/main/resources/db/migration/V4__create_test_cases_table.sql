CREATE TABLE tb_test_cases (
    id SERIAL PRIMARY KEY,
    challenge_id INTEGER NOT NULL,
    input VARCHAR(255) NOT NULL,
    expected_output VARCHAR(255) NOT NULL,
    CONSTRAINT testcase_challenge_fk FOREIGN KEY (challenge_id) REFERENCES tb_challenges(id)
)