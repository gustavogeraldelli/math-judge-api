package dev.gustavo.math.entity;

import dev.gustavo.math.entity.enums.ProblemDifficulty;
import dev.gustavo.math.entity.enums.ProblemType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_problems")
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 64)
    private ProblemDifficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 32)
    private ProblemType type;

    @OneToMany(mappedBy = "problem",  fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
    private List<TestCase> testCases;

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
    private List<Submission> submissions;

}
