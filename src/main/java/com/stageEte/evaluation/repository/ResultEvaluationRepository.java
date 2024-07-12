package com.stageEte.evaluation.repository;

import com.stageEte.evaluation.model.ResultEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultEvaluationRepository extends JpaRepository<ResultEvaluation,Long> {
    ResultEvaluation findByEvaluationIdAndSkillsId(Long evaluationId, Long skillId);
    List<ResultEvaluation> findByEvaluationId(Long evaluationId);
}
