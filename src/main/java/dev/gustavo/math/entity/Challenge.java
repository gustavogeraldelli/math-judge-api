package dev.gustavo.math.entity;

import dev.gustavo.math.entity.enums.ChallengeDifficulty;
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
@Table(name = "tb_challenges")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 64)
    private ChallengeDifficulty difficulty;

    @OneToMany(mappedBy = "challenge",  fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
    private List<TestCase> testCases;

    @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
    private List<Submission> submissions;

}
