package com.stageEte.evaluation.repository;

import com.stageEte.evaluation.model.Evaluation;
import com.stageEte.evaluation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByDeveloperId(Long developerId);
}
