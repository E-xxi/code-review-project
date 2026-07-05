package com.example.codereview.dto;

import java.util.List;

public record AnalyzeResponse(
        ComplexityEstimate complexity,
        List<String> styleFeedback,
        List<String> detectedPatterns,
        List<ConceptDiagnosis> missingConcepts,
        List<ImprovementSuggestion> improvements
) {

    public record ComplexityEstimate(
            String time,
            String space
    ) {
    }

    public record ConceptDiagnosis(
            String concept,
            String diagnosis
    ) {
    }

    public record ImprovementSuggestion(
            String description,
            String example
    ) {
    }
}
