ALTER TABLE tb_submissions RENAME COLUMN expression TO answer;
ALTER TABLE tb_test_cases RENAME COLUMN input TO variable_values;
ALTER TABLE tb_test_cases RENAME COLUMN expected_output TO expected_answer;
