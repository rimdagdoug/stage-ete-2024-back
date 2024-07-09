package com.stageEte.evaluation.controller;

import com.stageEte.evaluation.dto.ResultEvaluationDTO;
import com.stageEte.evaluation.model.ResultEvaluation;
import com.stageEte.evaluation.service.ResultEvaluationService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/result")
@Validated
public class ResultEvaluationController {
    private final ResultEvaluationService service;

    @PostMapping
    public ResponseEntity<ResultEvaluation> addResultEvaluation(@RequestBody ResultEvaluationDTO dto) {
        return service.addResultEvaluation(dto);
    }

    @GetMapping
    public ResponseEntity<List<ResultEvaluation>> getAllResultEvaluations() {
        return service.listResultEvaluations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultEvaluation> getResultEvaluationById(@PathVariable Long id) {
        return service.detailEvaluation(id);
    }

    @PutMapping("/noteInput/{id}")
    public ResponseEntity<ResultEvaluation> updateResultEvaluation(
            @PathVariable @NotNull Long id,
            @RequestBody ResultEvaluationDTO dto,
            Principal connectedUser) {
        return service.noteInput(id, dto, connectedUser);
    }






}
