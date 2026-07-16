package dev.gustavo.math.controller;

import dev.gustavo.math.controller.doc.IRankingController;
import dev.gustavo.math.controller.dto.PageableResponseDTO;
import dev.gustavo.math.controller.dto.ranking.RankingResponseDTO;
import dev.gustavo.math.entity.enums.ProblemDifficulty;
import dev.gustavo.math.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ranking")
@RequiredArgsConstructor
public class RankingController implements IRankingController {

    private final RankingService rankingService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageableResponseDTO<RankingResponseDTO> findRanking(@RequestParam(defaultValue = "0") Integer page,
                                                               @RequestParam(defaultValue = "10") Integer size,
                                                               @RequestParam(required = false) ProblemDifficulty difficulty) {
        var ranking = rankingService.findRanking(difficulty, PageRequest.of(page, size));
        return new PageableResponseDTO<>(ranking);
    }
}
