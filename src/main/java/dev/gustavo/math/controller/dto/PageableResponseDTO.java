package dev.gustavo.math.controller.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageableResponseDTO<T>(
        List<T> items,
        Integer page,
        Integer size,
        Long totalElements,
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
