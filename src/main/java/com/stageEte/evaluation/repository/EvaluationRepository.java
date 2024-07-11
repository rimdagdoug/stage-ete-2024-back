package com.stageEte.evaluation.repository;

import com.stageEte.evaluation.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
}
