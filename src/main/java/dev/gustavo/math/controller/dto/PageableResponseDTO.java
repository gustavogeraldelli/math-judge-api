package dev.gustavo.math.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "Paginated response wrapper")
public record PageableResponseDTO<T>(
        @Schema(description = "List of items returned in the current page", type = "array")
        List<T> items,

        @Schema(description = "Current page number (0-indexed)", type = "integer", example = "0")
        Integer page,

        @Schema(description = "Number of items per page", type = "integer", example = "10")
        Integer size,

        @Schema(description = "Total number of elements across all pages", type = "long", example = "70")
        Long totalElements,

        @Schema(description = "Total number of pages available", type = "integer", example = "5")
        Integer totalPages

) {
    public PageableResponseDTO(Page<T> page) {
        this(page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
