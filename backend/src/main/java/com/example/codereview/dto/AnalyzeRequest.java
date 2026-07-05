package com.example.codereview.dto;

import jakarta.validation.constraints.NotBlank;

public record AnalyzeRequest(
        @NotBlank(message = "문제는 비어 있을 수 없습니다") String problem,
        @NotBlank(message = "작성한 코드는 비어 있을 수 없습니다") String code
) {
}
