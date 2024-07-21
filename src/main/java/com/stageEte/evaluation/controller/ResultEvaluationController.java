package com.stageEte.evaluation.controller;

import com.stageEte.evaluation.dto.NoteEvalDTO;
import com.stageEte.evaluation.dto.ResultEvaluationMultipleDTO;
import com.stageEte.evaluation.model.ResultEvaluation;
import com.stageEte.evaluation.service.ResultEvaluationService;
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

    @GetMapping
    public ResponseEntity<List<ResultEvaluation>> getAllResultEvaluations() {
        return service.listResultEvaluations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultEvaluation> getResultEvaluationById(@PathVariable Long id) {
        return service.detailEvaluation(id);
    }

    @PutMapping("/noteInput")
    public ResponseEntity<String> updateResultEvaluationMultiple(
            @RequestBody NoteEvalDTO dto,
            Principal connectedUser) {
        return service.noteInputMultiple(dto, connectedUser);
    }

    @GetMapping("/allNote")
    public ResponseEntity<List<ResultEvaluation>> getAllNoteResultEvaluations() {
        return service.getAllNoteResultEvaluations();
    }

    @GetMapping("/updateFinalScore")
    public ResponseEntity<Float> updateFinalScore(@RequestParam Long evaluationId) {
        return service.updateFinalScore(evaluationId);
    }



}
