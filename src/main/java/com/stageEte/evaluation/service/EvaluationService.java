package com.stageEte.evaluation.service;

import com.stageEte.evaluation.dto.EvaluationDTO;
import com.stageEte.evaluation.model.Evaluation;
import com.stageEte.evaluation.model.User;
import com.stageEte.evaluation.repository.EvaluationRepository;
import com.stageEte.evaluation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final UserRepository userRepository;
    public ResponseEntity<List<Evaluation>> listEvaluations() {
        try {
            List<Evaluation> evaluations = evaluationRepository.findAll();
            if (evaluations.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(evaluations, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Evaluation> detailEvaluation(Long id) {
        Optional<Evaluation> evaluation = evaluationRepository.findById(id);
        return evaluation.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<Evaluation> addEvaluation(EvaluationDTO evaluationDetails) {
        try {
             Evaluation evaluation = new Evaluation();
            User developer = userRepository.findById(evaluationDetails.developerId()).orElse(null);
            User manager = userRepository.findById(evaluationDetails.managerId()).orElse(null);
            evaluation.setDeveloper(developer);
            evaluation.setManager(manager);
            evaluation.setStatus(evaluationDetails.statut());
            Evaluation savedEvaluation = evaluationRepository.save(evaluation);
            return new ResponseEntity<>(savedEvaluation, HttpStatus.CREATED);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Evaluation> updateEvaluation(Long id, EvaluationDTO evaluationDetails) {
        try {
            Optional<Evaluation> evaluationOptional = evaluationRepository.findById(id);
            if (evaluationOptional.isPresent()) {
                User developer = userRepository.findById(evaluationDetails.developerId()).orElse(null);
                User manager = userRepository.findById(evaluationDetails.managerId()).orElse(null);
                Evaluation evaluation = evaluationOptional.get();
                evaluation.setDeveloper(developer);
                evaluation.setManager(manager);
                evaluation.setStatus(evaluationDetails.statut());
                return new ResponseEntity<>(evaluationRepository.save(evaluation), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> deleteEvaluation(Long id) {
        try {
            if (evaluationRepository.existsById(id)) {
                evaluationRepository.deleteById(id);
                return new ResponseEntity<>("Evaluation deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Evaluation not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
