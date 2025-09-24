ALTER TABLE tb_challenges RENAME TO tb_problems;

ALTER TABLE tb_submissions DROP CONSTRAINT submission_challenge_fk;
ALTER TABLE tb_submissions RENAME COLUMN challenge_id TO problem_id;
ALTER TABLE tb_submissions ADD CONSTRAINT tb_submissions_problem_fk
    FOREIGN KEY (problem_id) REFERENCES tb_problems(id);

ALTER TABLE tb_test_cases DROP CONSTRAINT testcase_challenge_fk;
ALTER TABLE tb_test_cases RENAME COLUMN challenge_id TO problem_id;
ALTER TABLE tb_test_cases ADD CONSTRAINT testcase_problem_fk
    FOREIGN KEY (problem_id) REFERENCES tb_problems(id);