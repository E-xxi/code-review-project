package com.example.codereview.controller;

import com.example.codereview.dto.AnalyzeRequest;
import com.example.codereview.dto.AnalyzeResponse;
import com.example.codereview.service.ClaudeAnalysisService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AnalyzeController {

    private final ClaudeAnalysisService claudeAnalysisService;

    public AnalyzeController(ClaudeAnalysisService claudeAnalysisService) {
        this.claudeAnalysisService = claudeAnalysisService;
    }

    @PostMapping("/analyze")
    public AnalyzeResponse analyze(@Valid @RequestBody AnalyzeRequest request) {
        return claudeAnalysisService.analyze(request);
    }
}
