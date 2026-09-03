package com.example.codereview.service;

import com.example.codereview.analysis.CodeStructureSummary;
import com.example.codereview.analysis.PythonStructureAnalyzer;
import com.example.codereview.dto.AnalyzeRequest;
import com.example.codereview.dto.AnalyzeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClaudeAnalysisService {

    private static final String TOOL_NAME = "submit_analysis";

    private static final String TOOL_SCHEMA_JSON = """
            {
              "name": "submit_analysis",
              "description": "코드 분석 결과를 구조화된 형식으로 제출합니다.",
              "input_schema": {
                "type": "object",
                "properties": {
                  "complexity": {
                    "type": "object",
                    "properties": {
                      "time": { "type": "string", "description": "시간 복잡도, 예: O(n^2)" },
                      "space": { "type": "string", "description": "공간 복잡도, 예: O(n)" }
                    },
                    "required": ["time", "space"]
                  },
                  "styleFeedback": {
                    "type": "array",
                    "items": { "type": "string" },
                    "description": "코드 스타일/가독성에 대한 피드백"
                  },
                  "detectedPatterns": {
                    "type": "array",
                    "items": { "type": "string" },
                    "description": "감지된 알고리즘 패턴, 예: 완전탐색, 그리디, DP, BFS/DFS"
                  },
                  "missingConcepts": {
                    "type": "array",
                    "items": {
                      "type": "object",
                      "properties": {
                        "concept": { "type": "string" },
                        "diagnosis": { "type": "string", "description": "이 문제에 더 적합했던 개념과, 현재 코드가 왜 비효율적인지에 대한 구체적 진단" }
                      },
                      "required": ["concept", "diagnosis"]
                    }
                  },
                  "improvements": {
                    "type": "array",
                    "items": {
                      "type": "object",
                      "properties": {
                        "description": { "type": "string" },
                        "example": { "type": "string", "description": "리팩토링된 코드 예시" }
                      },
                      "required": ["description", "example"]
                    }
                  }
                },
                "required": ["complexity", "styleFeedback", "detectedPatterns", "missingConcepts", "improvements"]
              }
            }
            """;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final PythonStructureAnalyzer structureAnalyzer;
    private final String apiKey;
    private final String model;
    private final JsonNode toolSchema;

    public ClaudeAnalysisService(
            WebClient anthropicWebClient,
            ObjectMapper objectMapper,
            PythonStructureAnalyzer structureAnalyzer,
            @Value("${anthropic.api.key}") String apiKey,
            @Value("${anthropic.api.model}") String model
    ) {
        this.webClient = anthropicWebClient;
        this.objectMapper = objectMapper;
        this.structureAnalyzer = structureAnalyzer;
        this.apiKey = apiKey;
        this.model = model;
        this.toolSchema = objectMapper.readTree(TOOL_SCHEMA_JSON);
    }

    public AnalyzeResponse analyze(AnalyzeRequest request) {
        JsonNode response = webClient.post()
                .uri("/v1/messages")
                .header("x-api-key", apiKey)
                .bodyValue(buildPayload(request))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return extractAnalysis(response);
    }

    private Map<String, Object> buildPayload(AnalyzeRequest request) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", model);
        payload.put("max_tokens", 4096);
        payload.put("tools", List.of(toolSchema));
        payload.put("tool_choice", Map.of("type", "tool", "name", TOOL_NAME));
        payload.put("messages", List.of(Map.of("role", "user", "content", buildPrompt(request))));
        return payload;
    }

    private String buildPrompt(AnalyzeRequest request) {
        String structureSection = structureAnalyzer.analyze(request.code())
                .map(CodeStructureSummary::toPromptContext)
                .map(context -> "\n\n[ANTLR4로 미리 파싱한 코드 구조]\n" + context)
                .orElse("");

        return """
                아래는 코딩 테스트 문제와 그에 대한 사용자의 풀이 코드입니다.
                문제와 코드를 분석해서 %s 도구를 호출해 결과를 제출하세요.

                [문제]
                %s

                [사용자 코드]
                %s%s
                """.formatted(TOOL_NAME, request.problem(), request.code(), structureSection);
    }

    private AnalyzeResponse extractAnalysis(JsonNode response) {
        for (JsonNode block : response.path("content")) {
            if ("tool_use".equals(block.path("type").asString())) {
                return objectMapper.treeToValue(block.path("input"), AnalyzeResponse.class);
            }
        }
        throw new IllegalStateException("Claude 응답에서 tool_use 블록을 찾지 못했습니다");
    }
}
