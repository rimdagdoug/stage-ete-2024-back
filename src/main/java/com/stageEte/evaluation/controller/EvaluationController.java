package com.stageEte.evaluation.controller;

import com.stageEte.evaluation.dto.EvaluationDTO;
import com.stageEte.evaluation.model.Evaluation;
import com.stageEte.evaluation.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService service;

    @GetMapping("/evaluations")
    public ResponseEntity<List<Evaluation>> listEvaluations() {
        return service.listEvaluations();
    }

    @GetMapping("/evaluations/{id}")
    public ResponseEntity<Evaluation> detailEvaluation(@PathVariable Long id) {
        return service.detailEvaluation(id);
    }

    @PostMapping("/evaluations")
    public ResponseEntity<Evaluation> addEvaluation(@RequestBody EvaluationDTO evaluation) {
        return service.addEvaluation(evaluation);
    }

    @PutMapping("/evaluations/{id}")
    public ResponseEntity<Evaluation> updateEvaluation(@PathVariable Long id, @RequestBody EvaluationDTO request) {
        return service.updateEvaluation(id, request);
    }

    @DeleteMapping("/evaluations/{id}")
    public ResponseEntity<String> deleteEvaluation(@PathVariable Long id) {
        return service.deleteEvaluation(id);
    }
}
